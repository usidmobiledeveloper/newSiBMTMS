//package com.usid.mobilebmt.mandirisejahtera.unused;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.net.ConnectivityManager;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//import android.widget.Toast;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi.Donasi;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.BPJSpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PDAMpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.PLNpembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran.TelkomPembayaran;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.ETollPembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PLNpembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian.PulsaPembelian;
//import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
//import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
//import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
//import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
//import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
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
//import java.util.List;
//import java.util.Set;
//
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.finisa;
//import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
//
//public class NewTransaksiActivity extends AppCompatActivity implements IAsyncHandler {
//
//    private CardView btnTrfRekening, btnTrfBmt, btnTrfBank, btnLaz, btnLkaf, btnListrik, btnPdam, btnTelepon, btnBPJS, btnTv, btnTiket, btnPendidikan, btnAsuransi, btnMultifinance, btnTokenPln, btnPulsa;
//    private String kodeproduk = "";
//    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
//    ListView list;
//    private Class nextintent = null;
//
//    private Menu statusMenu;
//    private String jwtlocal;
//    private int sts = 0;
//    private boolean stats = false;
//    private Ctd cnd;
//    private NetworkChangeReceiver receiver;
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    private boolean finis = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.new_activity_transaksi);
//
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//        }
//
//        cnd = new Ctd(this);
//
//        mReceiver();
//        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        receiver = new NetworkChangeReceiver();
//        registerReceiver(receiver, intentFilter);
//
//        btnTrfRekening = findViewById(R.id.cv_antarrekening);
//        btnTrfBmt = findViewById(R.id.cv_antarbmt);
//        btnTrfBank = findViewById(R.id.cv_antarbank);
//        btnLaz = findViewById(R.id.cv_laz);
//        btnLkaf = findViewById(R.id.cv_lkaf);
//        btnListrik = findViewById(R.id.cv_pln);
//        btnPdam = findViewById(R.id.cv_pdam);
//        btnTelepon = findViewById(R.id.cv_telp);
//        btnBPJS = findViewById(R.id.cv_bpjs);
//        btnTv = findViewById(R.id.cv_tv);
//        btnTiket = findViewById(R.id.cv_tiket);
//        btnPendidikan = findViewById(R.id.cv_pendidikan);
//        btnAsuransi = findViewById(R.id.cv_asuransi);
//        btnMultifinance = findViewById(R.id.cv_multifinance);
//        btnTokenPln = findViewById(R.id.cv_token);
//        btnPulsa = findViewById(R.id.cv_pulsa);
//
//        btnTrfRekening.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, AntarRekening.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnTrfBmt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnTrfBank.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(NewTransaksiActivity.this, "Under Construction Menu!", Toast.LENGTH_LONG).show();
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                   /* Intent intent = new Intent(NewTransaksiActivity.this, AntarBank.class);
//                    startActivity(intent);*/
//                }
//            }
//        });
//
//        btnLaz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    /*Intent intent = new Intent(NewTransaksiActivity.this, Donasi.class);
//                    intent.putExtra("TITLE", "Donasi LAZ-BMT");
//                    startActivity(intent);*/
//                    dialogLaz();
//                }
//            }
//        });
//
//        btnLkaf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, Donasi.class);
//                    intent.putExtra("TITLE", "Donasi WAKAF");
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnListrik.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, PLNpembayaran.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnPdam.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "pdam";
//                    nextintent = PDAMpembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//
//        btnTelepon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    kodeproduk = "telp_pasca";
//                    nextintent = TelkomPembayaran.class;
//                    new AsyncListProd().execute();
//                }
//            }
//        });
//
//       /* btnInternet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });*/
//
//        btnTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnTiket.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnPendidikan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnAsuransi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnMultifinance.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//
//        btnTokenPln.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, PLNpembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnPulsa.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (jwtpub.equals("0"))
//                    Toast.makeText(NewTransaksiActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
//                else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, PulsaPembelian.class);
//                    startActivity(intent);
//                }
//            }
//        });
//
//        btnBPJS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewTransaksiActivity.this, BPJSpembayaran.class);
//                startActivity(intent);
//            }
//        });
//
//        CardView btnEmoney = findViewById(R.id.cv_emoney);
//        btnEmoney.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(NewTransaksiActivity.this, ETollPembelian.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    private void dialogLaz() {
//        List<String> mList = new ArrayList<String>();
//        mList.add("Donasi INFAQ");
//        mList.add("Donasi ZAKAT");
//
//        final CharSequence[] mDonatur = mList.toArray(new String[mList.size()]);
//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(NewTransaksiActivity.this);
//        dialogBuilder.setTitle("Pilih Jenis Donasi");
//        dialogBuilder.setItems(mDonatur, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int item) {
//                String mPilih = mDonatur[item].toString();
//                if (mPilih.equals("Donasi INFAQ")) {
//                    Intent intent = new Intent(NewTransaksiActivity.this, Donasi.class);
//                    intent.putExtra("TITLE", "Donasi INFAQ");
//                    startActivity(intent);
//                } else {
//                    Intent intent = new Intent(NewTransaksiActivity.this, Donasi.class);
//                    intent.putExtra("TITLE", "Donasi ZAKAT");
//                    startActivity(intent);
//                }
//
//            }
//        });
//        AlertDialog alertDialogObject = dialogBuilder.create();
//        alertDialogObject.show();
//    }
//
//    private class AsyncListProd extends AsyncTask<Void, Void, Boolean> {
//        ProgressDialog pdLoading = new ProgressDialog(NewTransaksiActivity.this);
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
//                String uri = new NumSky(NewTransaksiActivity.this).decrypt(getResources().getString(R.string.urlListProd)) + kodeproduk;
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
//                    ListAdapter adapter = new SimpleAdapter(NewTransaksiActivity.this, oslist, R.layout.list_item, new String[]{"nmproduk"}, new int[]{R.id.text1});
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewTransaksiActivity.this);
//                    list = new ListView(NewTransaksiActivity.this);
//                    list.setAdapter(adapter);
//                    alertDialog.setView(list);
//                    final Dialog dialog = alertDialog.create();
//                    dialog.show();
//                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                            String kdprod = oslist.get(+position).get("kdproduk");
//                            String wil = oslist.get(+position).get("nmproduk");
//                            Intent intent = new Intent(NewTransaksiActivity.this, nextintent);
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
//        new android.app.AlertDialog.Builder(NewTransaksiActivity.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finisa = true;
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    // tambahan baru ayik
//    @Override
//    public void onPostExec(Boolean status, String jwt) {
//        if (status) {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
//            jwtlocal = jwt;
//            cnd.countD.start();
//        } else {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
//            jwtlocal = jwt;
//        }
//        sts = sts + 1;
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
//        if (stats && sts > 1) {
//            cnd.countD.cancel();
//            jwtlocal = "0";
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
//            AuthLogin2 task = new AuthLogin2(this, this);
//            task.execute();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        menu.findItem(R.id.action_status).setEnabled(false);
//        menu.findItem(R.id.action_logout).setVisible(false);
//        statusMenu = menu;
//        if (!stats) {
//            cnd.countD.cancel();
//            jwtlocal = "0";
//            AuthLogin2 task = new AuthLogin2(NewTransaksiActivity.this, NewTransaksiActivity.this);
//            task.execute();
//            stats = true;
//            sts = 1;
//        }
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        cnd.countD.cancel();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
//
//    public class NetworkChangeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            if (Utility.isNetworkAvailable(context)) ;
//            onResume();
//        }
//    }
//
//    private void mReceiver() {
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
//                    if (finis) finish();
//                    else {
////                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
//                        cnd.countD.cancel();
//                        jwtlocal = "0";
//                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
//                        AuthLogin2 task = new AuthLogin2(NewTransaksiActivity.this, NewTransaksiActivity.this);
//                        task.execute();
//                    }
//                }
//            }
//        };
//    }
//}
