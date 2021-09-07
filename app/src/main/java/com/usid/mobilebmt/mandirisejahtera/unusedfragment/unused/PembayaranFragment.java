//package com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import androidx.fragment.app.Fragment;
//import androidx.appcompat.app.AlertDialog;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PDAMpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PLNpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.TelkomPembayaran;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Set;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//

//public class PembayaranFragment extends Fragment {
//    private View myFragmentView;
//    private Button btnListrik, btnPDAM, btnTelepon, btnInternet, btnTVBerlangganan, btnTiket, btnPendidikan, btnAsuransi, btnMultiFinance;
//    private String kodeproduk = "";
//    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
//    ListView list;
//    private Class nextintent = null;
//
//    public PembayaranFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        // Set title
////        ((MainLamaActivity) getActivity()).setActionBarTitle(getString(R.string.pembayaran));
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        myFragmentView = inflater.inflate(R.layout.fragment_pembayaran, container, false);
//        btnListrik = (Button) myFragmentView.findViewById(R.id.btListrik);
//        btnPDAM = (Button) myFragmentView.findViewById(R.id.btPDAM);
//        btnTelepon = (Button) myFragmentView.findViewById(R.id.btTelepon);
//        btnInternet = (Button) myFragmentView.findViewById(R.id.btInternet);
//        btnTVBerlangganan = (Button) myFragmentView.findViewById(R.id.btTVBerlangganan);
//        btnTiket = (Button) myFragmentView.findViewById(R.id.btTiket);
//        btnPendidikan = (Button) myFragmentView.findViewById(R.id.btPendidikan);
//        btnAsuransi = (Button) myFragmentView.findViewById(R.id.btAsuransi);
//        btnMultiFinance = (Button) myFragmentView.findViewById(R.id.btMultiFinance);
//        btnListrik.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(getActivity(), PLNpembayaran.class);
//                    startActivity(intent);
//                }
//            }
//        });
//        btnPDAM.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "pdam";
//                    nextintent = PDAMpembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//        btnTelepon.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(getActivity(), "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "telp_pasca";
//                    nextintent = TelkomPembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//        btnInternet.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnTVBerlangganan.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnTiket.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnPendidikan.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnAsuransi.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        btnMultiFinance.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Under Construction Menu!", Toast.LENGTH_LONG).show();
//            }
//        });
//        return myFragmentView;
//    }
//
//    private class AsyncListProd extends AsyncTask<Void, Void, Boolean> {
//        ProgressDialog pdLoading = new ProgressDialog(getActivity());
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
//        Boolean stats = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            finisa = false;
//            oslist.clear();
//            pdLoading.setCancelable(false);
//            pdLoading.setIndeterminate(false);
//            pdLoading.setTitle("Memproses");
//            pdLoading.setMessage("Tunggu...");
//            pdLoading.show();
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            try {
//                String uri = new NumSky(getActivity()).decrypt(getResources().getString(R.string.urlListProd)) + kodeproduk;
//                URL obj = new URL(uri);
//                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//                con.setRequestMethod("GET");
//                con.setRequestProperty("Content-Type", "application/json");
//                con.setConnectTimeout(30000);
//                con.setReadTimeout(29000);
//                ket = con.getResponseCode() + " " + con.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine + "\n");
//                }
//                in.close();
//                if (con.getResponseCode() == 200) {
//                    stats = true;
//                    JSONParser parser = new JSONParser();
//                    Object objects = parser.parse(response.toString());
//                    JSONObject jsonObject = (JSONObject) objects;
//                    Set keys = jsonObject.keySet();
//                    Iterator a = keys.iterator();
//                    if (kodeproduk.equals("telp_pasca")) {
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("nmproduk", "TELKOM");
//                        map.put("kdproduk", "TELEPON");
//                        oslist.add(map);
//                        HashMap<String, String> map2 = new HashMap<>();
//                        map2.put("nmproduk", "SPEEDY");
//                        map2.put("kdproduk", "SPEEDY");
//                        oslist.add(map2);
//                    }
//                    while (a.hasNext()) {
//                        String key = (String) a.next();
//                        String value = (String) jsonObject.get(key);
//                        HashMap<String, String> map = new HashMap<>();
//                        map.put("nmproduk", value);
//                        map.put("kdproduk", key);
//                        oslist.add(map);
//                    }
//                    Collections.sort(oslist, new Comparator<HashMap<String, String>>() {
//                        @Override
//                        public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
//                            return lhs.get("nmproduk").compareTo(rhs.get("nmproduk"));
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return stats;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            try {
//                if (result) {
//                    ListAdapter adapter = new SimpleAdapter(getActivity(), oslist, R.layout.list_item, new String[]{"nmproduk"}, new int[]{R.id.text1});
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//                    list = new ListView(getActivity());
//                    list.setAdapter(adapter);
//                    alertDialog.setView(list);
//                    final Dialog dialog = alertDialog.create();
//                    dialog.show();
//                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            String kdprod = oslist.get(+position).get("kdproduk");
//                            String wil = oslist.get(+position).get("nmproduk");
//                            Intent intent = new Intent(getActivity(), nextintent);
//                            intent.putExtra("JNSTRX", kdprod);
//                            intent.putExtra("WILAYAH", wil);
//                            startActivity(intent);
//                            dialog.dismiss();
//                        }
//                    });
//                } else {
//                    String msga = "#" + ket + "\n";
//                    showAlert("GAGAL Inquiry List Produk", msga);
//                }
//            } catch (Exception o) {
//                o.printStackTrace();
//                String msga = "#" + ket + "\n";
//                showAlert("GAGAL Inquiry List Produk", msga);
//            }
//        }
//    }
//
//    private void showAlert(String title, String message) {
//        new android.app.AlertDialog.Builder(getActivity()).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finisa = true;
//                dialog.dismiss();
//            }
//        }).show();
//    }
//}
