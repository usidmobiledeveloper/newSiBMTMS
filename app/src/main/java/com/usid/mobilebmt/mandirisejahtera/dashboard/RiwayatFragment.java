package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat.AdapterRiwayat;
import com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat.ItemRiwayat;
import com.usid.mobilebmt.mandirisejahtera.databinding.FragmentRiwayatBinding;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class RiwayatFragment extends Fragment {


    public RiwayatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static RiwayatFragment newInstance(String param1, String param2) {
        RiwayatFragment fragment = new RiwayatFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    FragmentRiwayatBinding binding;
    String imsi = "", nocard = "";
    private SharedPreferences config;

    AdapterRiwayat adapterRiwayat;
    ArrayList<ItemRiwayat> riwayatList = new ArrayList<>();

    boolean isLoading = false;
    String keyword = "", type = "";
    int num = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRiwayatBinding.inflate(inflater, container, false);

        config = getActivity().getSharedPreferences("config", 0);

        SnHp telpMan = new SnHp(getActivity());
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
            imsi = Utility.getIMSIRead(getActivity());
        }
        NumSky nmsk = new NumSky(getActivity());
        try {
            nocard = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //sampleList();
        adapterRiwayat = new AdapterRiwayat(riwayatList, getActivity());

        if (binding != null)
            binding.rvRiwayat.setAdapter(adapterRiwayat);

        initScrollListener();

        new AsyncRiwayat().execute();

        binding.btCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = binding.edCari.getText().toString();
                type = "CARI";
                if (keyword.length() < 2) {
                    Toast.makeText(getActivity(), "Masukkan kata kunci pencarian minimal 2 karakter", Toast.LENGTH_SHORT).show();
                } else {
                    riwayatList.clear();
                    num = 0;

                    disableViews(binding.rvRiwayat, binding.tvError);
                    enableViews(binding.pbar);
                    new AsyncCariRiwayat().execute();
                }

            }
        });


        return binding.getRoot();


    }

    private void initScrollListener() {

        if (binding != null)
            binding.rvRiwayat.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                    if (!isLoading) {
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == riwayatList.size() - 1) {
                            //bottom of list!
                            if(type.equals("CARI")){
                                //do nothing
                            }else {
                                loadMore();
                                isLoading = true;
                            }

                        }
                    }
                }
            });


    }

    private void loadMore() {
        riwayatList.add(null);
        adapterRiwayat.notifyItemInserted(riwayatList.size() - 1);

        enableViews(binding.pbar);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                riwayatList.remove(riwayatList.size() - 1);
                int scrollPosition = riwayatList.size();
                adapterRiwayat.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 25;

                if (currentSize - 1 < nextLimit) {
                    //sampleList();
                    num = num + 25;

                    if (type.equals("CARI")) {
                        //new AsyncCariRiwayat().execute();
                    } else {
                        new AsyncRiwayat().execute();
                    }

                    currentSize++;
                }

            }
        }, 1000);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), num, type, keyword);

    private String toJsonString(String nokartu, String imsi, int jmltrx, String type, String keyword) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("nokartu", nokartu);
            obj.put("imsi", imsi);

            if (type.equals("CARI")) {
                obj.put("berita", keyword);
            } else {
                obj.put("jmltransaksi", jmltrx);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    private class AsyncRiwayat extends AsyncTask<Void, Void, Void> {

        Boolean stInfoRiwayat = false;
        private String pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", tgl = "";
        String faktur = "", jumlah = "", trxid = "", keterangan = "", jenis = "", produk = "", tujuan = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            type = "";
            keyword = "";

        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlGetRiwayat)));

                Log.d("AYIK", "riwayat url "+ obj.toString());

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), num, type, keyword);
                Log.d("AYIK", "riwayat body "+ strCek);

                conJ.setConnectTimeout(20000);
                conJ.setReadTimeout(19000);
                conJ.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
                wr.writeBytes(strCek);
                wr.flush();
                wr.close();
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();

                Log.d("AYIK", "riwayat response "+ response.toString());
                JSONObject jsonObject = new JSONObject(response.toString());
                stInfoRiwayat = jsonObject.getBoolean("status");

                if (stInfoRiwayat) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        faktur = data.getString("faktur");
                        tgl = data.getString("tgl");
                        jumlah = data.getString("jumlah");
                        keterangan = data.getString("keterangan");
                        trxid = data.getString("trxid");
                        jenis = data.getString("jenis");
                        produk = data.getString("produk");
                        tujuan = data.getString("tujuan");
                        riwayatList.add(new ItemRiwayat(faktur, String.valueOf(num++), tgl, jumlah, keterangan, trxid, jenis, produk, tujuan));
                    }
                } else {
                    ket = jsonObject.getString("keterangan");
                    stInfoRiwayat = false;
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoRiwayat = false;
                ket = "ERROR, Exception kesalahan data ";
                Log.d("AYIK", "riwayat exc "+ ex.getMessage());

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (binding != null)
                disableViews(binding.pbar);

            if (stInfoRiwayat) {
                //SUCCESS
                adapterRiwayat.notifyDataSetChanged();
                isLoading = false;


            } else {
                if (riwayatList.size() >= 1) {
                    String msga = "#" + ket + "\n";
                    if (binding != null)
                        disableViews(binding.tvError);
                } else {
                    String msga = "#" + ket + "\n";
                    if (jwtpub.equals("0")) {
                        if (binding != null)
                            binding.tvError.setText("Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru");
                    } else {
                        if (binding != null) {
                            if (!Utility2.errorImsi(msga)) {
                                binding.tvError.setText(msga);
                            } else {
                                showAlert("GAGAL", msga);

                            }

                        }

                    }
                }


            }
        }
    }

    private class AsyncCariRiwayat extends AsyncTask<Void, Void, Void> {

        Boolean stInfoRiwayat = false;
        private String pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", tgl = "";
        String faktur = "", jumlah = "", trxid = "", keterangan = "", jenis = "", produk = "", tujuan = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlCariRiwayat)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), num, type, keyword);

                conJ.setConnectTimeout(20000);
                conJ.setReadTimeout(19000);
                conJ.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
                wr.writeBytes(strCek);
                wr.flush();
                wr.close();
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();

                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();

                JSONObject jsonObject = new JSONObject(response.toString());

                stInfoRiwayat = jsonObject.getBoolean("status");

                if (stInfoRiwayat) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        faktur = data.getString("faktur");
                        tgl = data.getString("tgl");
                        jumlah = data.getString("jumlah");
                        keterangan = data.getString("keterangan");
                        trxid = data.getString("trxid");
                        jenis = data.getString("jenis");
                        produk = data.getString("produk");
                        tujuan = data.getString("tujuan");

                        //Log.d("AYIK", "datas "+ faktur+ " "+ tgl +" "+ jumlah);

                        riwayatList.add(new ItemRiwayat(faktur, String.valueOf(num++), tgl, jumlah, keterangan, trxid, jenis, produk, tujuan));
                    }
                } else {
                    ket = jsonObject.getString("keterangan");
                    stInfoRiwayat = false;
                }


            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoRiwayat = false;
                ket = "ERROR, Exception kesalahan data ";

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (binding != null)
                disableViews(binding.pbar);

            if (stInfoRiwayat) {
                //SUCCESS
                adapterRiwayat.notifyDataSetChanged();
                isLoading = false;
                enableViews(binding.rvRiwayat);
            } else {

                if (riwayatList.size() >= 1) {
                    String msga = "#" + ket + "\n";
                    if (binding != null) {
                        disableViews(binding.tvError);
                    }

                } else {
                    enableViews(binding.tvError);
                    String msga = "#" + ket + "\n";
                    if (jwtpub.equals("0")) {
                        if (binding != null)
                            binding.tvError.setText("Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru");
                    } else {
                        if (binding != null) {
                            if (!Utility2.errorImsi(msga)) {
                                binding.tvError.setText(msga);
                            } else {
                                showAlert("GAGAL", msga);

                            }

                        }

                    }
                }

            }
        }
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(getActivity()).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                finisa = true;

                Toast.makeText(getActivity(), "SILAHKAN RE-AKTIVASI", Toast.LENGTH_SHORT).show();
                Utility2.reAktivasi(getActivity());
                getActivity().finish();

            }
        }).show();
    }
}