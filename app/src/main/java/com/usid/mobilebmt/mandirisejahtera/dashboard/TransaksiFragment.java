package com.usid.mobilebmt.mandirisejahtera.dashboard;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.Donasi;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.BPJSpembayaran;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.MultifinancePembayaran;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PDAMpembayaran;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PLNpembayaran;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.TelkomPembayaran;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.ETollPembelian;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PLNpembelian;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PulsaPembelian;
import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.UangPembelian;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class TransaksiFragment extends Fragment {

    public TransaksiFragment() {
        // Required empty public constructor
    }

    private CardView mExpandButtonTrf, mExpandButtonByr, mExpandButtonBli, mExpandButtonDonasi;
    private CardView btnLaz, btnLkaf, btnListrik, btnPdam, btnTelepon, btnBPJS, btnTiket, btnTokenPln, btnPulsa;
    private String kodeproduk = "";
    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    ListView list;
    private Class nextintent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.new_fragment_transaksi, container, false);

        CardView cvMore = rootView.findViewById(R.id.cv_tv);
        cvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SoonActivity.class);
                i.putExtra("title", "Televisi");
                startActivity(i);
            }
        });

        CardView cvPendidikan = rootView.findViewById(R.id.cv_pendidikan);
        cvPendidikan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SoonActivity.class);
                i.putExtra("title", "Pendidikan");
                startActivity(i);
            }
        });

        CardView cvAsuransi = rootView.findViewById(R.id.cv_asuransi);
        cvAsuransi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SoonActivity.class);
                i.putExtra("title", "Asuransi");
                startActivity(i);
            }
        });

        CardView cvMulti = rootView.findViewById(R.id.cv_multifinance);
        cvMulti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {

                    kodeproduk = "pinjaman";
                    nextintent = MultifinancePembayaran.class;
                    new AsyncListProd().execute();
                }
            }
        });

        btnLaz = rootView.findViewById(R.id.cv_laz);
        btnLkaf = rootView.findViewById(R.id.cv_lkaf);
        btnListrik = rootView.findViewById(R.id.cv_pln);
        btnPdam = rootView.findViewById(R.id.cv_pdam);
        btnTelepon = rootView.findViewById(R.id.cv_telp);
        btnBPJS = rootView.findViewById(R.id.cv_bpjs);
        btnTiket = rootView.findViewById(R.id.cv_tiket);
        btnTokenPln = rootView.findViewById(R.id.cv_token);
        btnPulsa = rootView.findViewById(R.id.cv_pulsa);

        btnLaz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {

                    dialogLaz();
                }
            }
        });

        btnLkaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    if (kodebmt.equals("0008")) {//DMU{
                        Intent i = new Intent(getActivity(), SoonActivity.class);
                        i.putExtra("title", "Wakaf");
                        startActivity(i);
                    } else {
                        Intent intent = new Intent(getActivity(), Donasi.class);
                        intent.putExtra("TITLE", "Donasi WAKAF");
                        startActivity(intent);
                    }

                }
            }
        });

        btnListrik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), PLNpembayaran.class);
                    startActivity(intent);
                }
            }
        });

        btnPdam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    kodeproduk = "pdam";
                    nextintent = PDAMpembayaran.class;
                    new AsyncListProd().execute();
                }
            }
        });

        btnTelepon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    kodeproduk = "telp_pasca";
                    nextintent = TelkomPembayaran.class;
                    new AsyncListProd().execute();
                }
            }
        });

    /*    btnInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/

        btnTiket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SoonActivity.class);
                i.putExtra("title", "Tiket");
                startActivity(i);
            }
        });

        btnTokenPln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), PLNpembelian.class);
                    startActivity(intent);
                }
            }
        });

        btnPulsa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0"))
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getActivity(), PulsaPembelian.class);
                    startActivity(intent);
                }
            }
        });

        btnBPJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BPJSpembayaran.class);
                startActivity(intent);
            }
        });

        CardView btnEmoney = rootView.findViewById(R.id.cv_emoney);
        btnEmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0")) {
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(getActivity(), ETollPembelian.class);
                    startActivity(intent);
                }
            }
        });

        CardView btnUang = rootView.findViewById(R.id.cv_uang);
        btnUang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtpub.equals("0")) {
                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getActivity(), UangPembelian.class);
                    startActivity(intent);
                }
            }
        });

        TextView tvLaz = rootView.findViewById(R.id.tv_laz);
        kodebmt = getResources().getString(R.string.kodebmt);

        if (kodebmt.equals("0008")) {// DMU
            tvLaz.setText("Baitul Maal\n");
            btnLkaf.setVisibility(View.GONE);
        } else {
            tvLaz.setText("ZIS\n");
            btnLkaf.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    String kodebmt = "";

    private void dialogLaz() {
        if (kodebmt.equals("0008")) {//dmu
            Intent intent = new Intent(getActivity(), Donasi.class);
            intent.putExtra("TITLE", "Donasi INFAQ");
            startActivity(intent);
        } else {
            List<String> mList = new ArrayList<String>();
            mList.add("Donasi INFAQ");
            mList.add("Donasi ZAKAT");

            final CharSequence[] mDonatur = mList.toArray(new String[mList.size()]);
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle("Pilih Jenis Donatur");
            dialogBuilder.setItems(mDonatur, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    String mPilih = mDonatur[item].toString();
                    if (mPilih.equals("Donasi INFAQ")) {
                        Intent intent = new Intent(getActivity(), Donasi.class);
                        intent.putExtra("TITLE", "Donasi INFAQ");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), Donasi.class);
                        intent.putExtra("TITLE", "Donasi ZAKAT");
                        startActivity(intent);
                    }

                }
            });
            AlertDialog alertDialogObject = dialogBuilder.create();
            alertDialogObject.show();
        }

    }

    private class AsyncListProd extends AsyncTask<Void, Void, Boolean> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
        Boolean stats = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            finisa = false;
            oslist.clear();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String uri = new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlListProd)) + kodeproduk;
                URL obj = new URL(MyVal.URL_BASE_PPOB() + uri);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", getPrefsAuthToken());
                con.setConnectTimeout(30000);
                con.setReadTimeout(29000);
                ket = con.getResponseCode() + " " + con.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                if (con.getResponseCode() == 200) {
                    stats = true;
                    JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONObject jsonObject = (JSONObject) objects;
                    Set keys = jsonObject.keySet();
                    Iterator a = keys.iterator();
                    if (kodeproduk.equals("telp_pasca")) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("nmproduk", "TELKOM");
                        map.put("kdproduk", "TELEPON");
                        oslist.add(map);
                        HashMap<String, String> map2 = new HashMap<>();
                        map2.put("nmproduk", "SPEEDY");
                        map2.put("kdproduk", "SPEEDY");
                        oslist.add(map2);
                    }
                    while (a.hasNext()) {
                        String key = (String) a.next();
                        String value = (String) jsonObject.get(key);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("nmproduk", value);
                        map.put("kdproduk", key);
                        oslist.add(map);
                    }
                    Collections.sort(oslist, new Comparator<HashMap<String, String>>() {
                        @Override
                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                            return lhs.get("nmproduk").compareTo(rhs.get("nmproduk"));
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stats;
        }

        ListAdapter adapter;
        ArrayList<HashMap<String, String>> filteredList;

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            try {
                if (result) {
                    adapter = new SimpleAdapter(getActivity(), oslist, R.layout.list_item, new String[]{"nmproduk"},
                            new int[]{R.id.text1});
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_pdam, null);
                    alertDialog.setView(dialogView);

                    final ListView listView = dialogView.findViewById(R.id.list_pdam);
                    listView.setAdapter(adapter);

                    EditText edSearch = dialogView.findViewById(R.id.ed_search_pdam);

                    edSearch.addTextChangedListener(new TextWatcher() {

                        public void afterTextChanged(Editable s) {

                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence query, int start, int before, int count) {

                            query = query.toString().toUpperCase();
                            filteredList = new ArrayList<HashMap<String, String>>();

                            for (HashMap<String, String> model : oslist) {
                                final String text1 = model.get("nmproduk");
                                final String text2 = model.get("kdproduk");

                                if (text1.contains(query)) {
                                    model.put("nmproduk", text1);
                                    model.put("kdproduk", text2);
                                    filteredList.add(model);
                                }
                            }


                            adapter = new SimpleAdapter(getActivity(), filteredList, R.layout.list_item,
                                    new String[]{"nmproduk"}, new int[]{R.id.text1});
                            listView.setAdapter(adapter);
                        }
                    });


                    final Dialog dialog = alertDialog.create();
                    dialog.show();

                    /*list = new ListView(getActivity());
                    list.setAdapter(adapter);
                    alertDialog.setView(list);*/

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            /*String kdprod = oslist.get(+position).get("kdproduk");
                            String wil = oslist.get(+position).get("nmproduk");*/

                            String kdprod, wil;

                            if (filteredList != null) {
                                kdprod = filteredList.get(+position).get("kdproduk");
                                wil = filteredList.get(+position).get("nmproduk");
                            } else {
                                kdprod = oslist.get(+position).get("kdproduk");
                                wil = oslist.get(+position).get("nmproduk");
                            }

                            Intent intent = new Intent(getActivity(), nextintent);
                            intent.putExtra("JNSTRX", kdprod);
                            intent.putExtra("WILAYAH", wil);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
                } else {
                    String msga = "#" + ket + "\n";
                    showAlert("GAGAL Inquiry List Produk", msga);
                }
            } catch (Exception o) {
                o.printStackTrace();
                String msga = "#" + ket + "\n";
                showAlert("GAGAL Inquiry List Produk", msga);
            }
        }
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(getActivity()).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finisa = true;
                dialog.dismiss();
            }
        }).show();
    }

}
