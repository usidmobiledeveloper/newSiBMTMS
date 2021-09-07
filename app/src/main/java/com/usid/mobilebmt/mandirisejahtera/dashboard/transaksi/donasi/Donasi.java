package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.NumberTextWatcher;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.DoubleToCurrency;
import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class Donasi extends AppCompatActivity implements IAsyncHandler {
    private Bundle extras;
    private TextView tvErrorMsg, tvJudul;
    private EditText edNominal;
    private String trxID = "", strNokartu = "", strImsi = "", strPIN = "", titleActionBar = "";
    private Button btnCancel, btnDonasi;
    private long nominal = 0;
    private SharedPreferences config;

    //tambahan ayik==
    private Menu statusMenu;
    private String jwtlocal;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true;
    private ProgressDialog pdLoadingInq;
    String kodebmt = "", namaInstansi = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_donasi);

        kodebmt = getResources().getString(R.string.kodebmt);

        if (kodebmt.equals("0059")) {// BEN SILATU
            namaInstansi = "JPZIS BEN SILATU PEDULI";
        } else {
            namaInstansi = getResources().getString(R.string.nama_instansi);
        }

        cnd = new Ctd(Donasi.this);

        tvJudul = (TextView) findViewById(R.id.tvJudul);
        tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        btnDonasi = (Button) findViewById(R.id.btnDonasi);
        btnCancel = (Button) findViewById(R.id.btCancel);
        edNominal = (EditText) findViewById(R.id.edNomDonasi);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                titleActionBar = "";
            } else {
                titleActionBar = extras.getString("TITLE");
            }
        } else {
            titleActionBar = (String) savedInstanceState.getSerializable("TITLE");
        }
        setTitle(titleActionBar);
        tvJudul.setText(titleActionBar);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
           /* try {
                strImsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            strImsi = Utility.getIMSIRead(this);
        }
        pdLoadingInq = new ProgressDialog(Donasi.this);
        pdLoadingInq.setCancelable(false);
        pdLoadingInq.setIndeterminate(false);
        pdLoadingInq.setTitle("Memproses");
        pdLoadingInq.setMessage("Tunggu...");
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edNominal.addTextChangedListener(new NumberTextWatcher(edNominal));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnDonasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(Donasi.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    cekStrNominal();
                    tvErrorMsg.setVisibility(View.GONE);
                    if (!validateNominal()) {
                        return;
                    }
                    if (titleActionBar.equals("Donasi ZAKAT"))
                        showConfirmPinDonasi(getString(R.string.pin_mobilebmt), 1);
                    else if (titleActionBar.equals("Donasi WAKAF"))
                        showConfirmPinDonasi(getString(R.string.pin_mobilebmt), 2);
                    else if (titleActionBar.equals("Donasi INFAQ"))
                        showConfirmPinDonasi(getString(R.string.pin_mobilebmt), 3);
                }
            }
        });

        // tambahan baru ayik
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            //new AsyncInqBMT().execute();
            trxID = String.valueOf(System.currentTimeMillis());
        }
    }

    private void cekStrNominal() {
        String nom = "";
        if (edNominal.getText().toString().indexOf(",") != -1) {
            nom = edNominal.getText().toString().replace(",", "");
            if (nom.equals("")) nominal = 0;
            else nominal = Long.parseLong(nom);
        } else {
            nom = edNominal.getText().toString().replace(".", "");
            if (nom.equals("")) nominal = 0;
            else nominal = Long.parseLong(nom);
        }
    }

    private boolean validateNominal() {
        if (edNominal.getText().toString().trim().equals("")) {
            edNominal.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal tidak boleh kosong!");
            showAlert("Error", "Nominal tidak boleh kosong!");
            return false;
        } else if (nominal < 1000) {
            edNominal.requestFocus();
            edNominal.setSelection(edNominal.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal donasi minimal Rp. 1.000,-");
            showAlert("Error", "Nominal donasi minimal Rp. 1.000,-");
            return false;
        } else if (nominal > 10000000) {
            edNominal.requestFocus();
            edNominal.setSelection(edNominal.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal transfer maksimal Rp. 10.000.000,-");
            showAlert("Error", "Nominal transfer maksimal Rp. 10.000.000,-");
            return false;
        } else {
            tvErrorMsg.setVisibility(View.GONE);
            tvErrorMsg.setText("");
        }
        return true;
    }

    private String toJsonString(String nokartu, String imsi, long nominal, String pin, String
            berita, String idtrx) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("pin", pin);
        obj.put("nominal", nominal);
        obj.put("berita", berita);
        obj.put("trxid", idtrx);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            obj.put("versi", pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (Map<String, String> map : Utility2.additionalObject(this)) {
            for (String key : map.keySet()) {
                obj.put(key, map.get(key));
            }
        }

        return obj.toString();
    }

    private class AsyncInqBMT extends AsyncTask<Void, Void, Void> {
        private Boolean status = false;
        private long IDtrx = 0;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi.";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadingInq.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(Donasi.this).decrypt(getResources().getString(R.string.urlGetID)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("GET");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine).append("\n");
                }
                in.close();
                Log.d("AYIK", "inq donasi response " + response.toString());
                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                IDtrx = (long) jsonObject.get("trxid");
                status = true;
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
                Log.d("AYIK", "inq donasi exc " + ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
            if (status) {
                trxID = Long.toString(IDtrx);
            } else {
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            }
        }
    }

    private class AsyncLAZ extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(Donasi.this);
        private Boolean stLAZ = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.",
                strFaktur = "", strRekAsal = "", strDate = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(Donasi.this).decrypt(getResources().getString(R.string.urlLAZ)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), nominal, Utility.md5(strPIN),
                        "Donasi ZAKAT", trxID);

                Log.d("AYIK", "zkt:strcek\n" + strCek);

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

                Log.d("AYIK", "zkt:response " + response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stLAZ = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                nominal = (long) jsonObject.get("nominal");
                strFaktur = (String) jsonObject.get("fakturtujuan");
                strRekAsal = (String) jsonObject.get("rekeningasal");
                strDate = (String) jsonObject.get("datetime");
                //jwtlocal = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                stLAZ = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stLAZ) {
                ket = "Tanggal : " + strDate + "\n" + strFaktur + "\nAlhamdulillah... Donasi dari " + strRekAsal + " senilai Rp. " + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga Zakat " + namaInstansi + ". Semoga bertambah barokah.";
                SysDB dbsys = new SysDB(Donasi.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Donasi ZAKAT SUKSES\n" + ket);
                dbsys.close();
                showConfirmDonasiSukses("Donasi ZAKAT SUKSES", "" + strDate, "" + strFaktur,
                        "Alhamdulillah... Donasi dari " + strRekAsal + " senilai Rp. "
                                + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga Zakat " + namaInstansi + ". Semoga bertambah barokah.");
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.startsWith("404")) {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi ZAKAT GAGAL " + msga);
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi ZAKAT GAGAL", msga);
                    dbsys.close();
                } else {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi ZAKAT TIMEOUT " + msga);
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi ZAKAT TIMEOUT", msga);
                    dbsys.close();
                }

            }
        }
    }

    private class AsyncLKAF extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(Donasi.this);
        private Boolean stLAZ = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.", strFaktur = "", strRekAsal = "", strDate = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(Donasi.this).decrypt(getResources().getString(R.string.urlLKAF)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), nominal, Utility.md5(strPIN),
                        "Donasi WAKAF", trxID);
                Log.d("AYIK", "wkf:strcek\n" + strCek);
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

                Log.d("AYIK", "wkf:response " + response.toString());
                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stLAZ = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                nominal = (long) jsonObject.get("nominal");
                strFaktur = (String) jsonObject.get("fakturtujuan");
                strRekAsal = (String) jsonObject.get("rekeningasal");
                strDate = (String) jsonObject.get("datetime");
                //jwtlocal = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                stLAZ = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stLAZ) {
                ket = "Tanggal : " + strDate + "\n" + strFaktur + "\nAlhamdulillah... Donasi dari " + strRekAsal + " senilai Rp. " + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga WAKAF " + namaInstansi + ". Semoga bertambah barokah.";
                SysDB dbsys = new SysDB(Donasi.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Donasi WAKAF SUKSES\n" + ket);
                dbsys.close();
                showConfirmDonasiSukses("Donasi WAKAF SUKSES", "" + strDate, "" + strFaktur,
                        "Alhamdulillah... Donasi dari " + strRekAsal + " senilai Rp. "
                                + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga WAKAF " + namaInstansi + ". Semoga bertambah barokah.");
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.startsWith("404")) {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi WAKAF GAGAL " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi WAKAF GAGAL", msga);
                } else {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi WAKAF TIMEOUT " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi WAKAF TIMEOUT", msga);
                }

            }
        }
    }

    private class AsyncINFAQ extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(Donasi.this);
        private Boolean stLAZ = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.", strFaktur = "", strRekAsal = "", strDate = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(Donasi.this).decrypt(getResources().getString(R.string.urlInfaq)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), nominal, Utility.md5(strPIN),
                        "Donasi INFAQ", trxID);
                Log.d("AYIK", "infq:strcek\n" + strCek);
//                System.out.println(">>> " + strCek);
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

                Log.d("AYIK", "infq:response " + response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stLAZ = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                nominal = (long) jsonObject.get("nominal");
                strFaktur = (String) jsonObject.get("fakturtujuan");
                strRekAsal = (String) jsonObject.get("rekeningasal");
                strDate = (String) jsonObject.get("datetime");
                //jwtlocal = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                stLAZ = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stLAZ) {
                ket = "Tanggal : " + strDate + "\n" + strFaktur + "\nAlhamdulillah... Infaq dari " + strRekAsal + " senilai Rp. " + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga INFAQ " + namaInstansi + ". Semoga bertambah barokah.";
                SysDB dbsys = new SysDB(Donasi.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Donasi Infaq SUKSES\n" + ket);
                dbsys.close();
                showConfirmDonasiSukses("Donasi Infaq SUKSES", "" + strDate, "" + strFaktur,
                        "Alhamdulillah... Infaq dari " + strRekAsal + " senilai Rp. "
                                + DoubleToCurrency(nominal) + ",- telah diterima dengan baik oleh Lembaga " + namaInstansi + ". Semoga bertambah barokah.");
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.startsWith("404")) {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi Infaq GAGAL " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi Infaq GAGAL", msga);
                } else {
                    SysDB dbsys = new SysDB(Donasi.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Donasi Infaq TIMEOUT " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Donasi Infaq TIMEOUT", msga);
                }

            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finis = true;
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmDonasiSukses(String Judul, String tgls, String faktur, String
            darirek) {

        Intent antarbank = new Intent(Donasi.this, DonasiSukses.class);

        ArrayList<String> arr = new ArrayList<String>();

        arr.add("Tanggal#" + tgls);
        arr.add("Faktur#" + faktur);
        //arr.add("Dari Rek.#" + darirek);

        antarbank.putExtra("judul", Judul.replace("SUKSES", "Berhasil"));
        antarbank.putExtra("darirek", darirek);
        antarbank.putStringArrayListExtra("arr", arr);

        startActivity(antarbank);
        finish();

       /* final Dialog dialog = new Dialog(Donasi.this);
        Utility.playNotificationSound(Donasi.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_donasisukses);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
        tgl.setText(tgls);
        TextView prods = (TextView) dialog.findViewById(R.id.faktur);
        prods.setText(faktur);
        TextView noh = (TextView) dialog.findViewById(R.id.darirek);
        noh.setText(darirek);
        dialog.setCancelable(false);
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK SIMPAN");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dialog dialog2 = Dialog.class.cast(dialog);
                takeScreenshot(dialog2);
                onBackPressed();
                dialog.dismiss();
            }
        });
        dialog.show();*/
    }

    private void showConfirmPinDonasi(String msg, final int fi) {
        final Dialog dialog = new Dialog(Donasi.this);
        finis = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_pin);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        final EditText edPin = (EditText) dialog.findViewById(R.id.edInfoPin);
        textPin.setText(msg);
        dialog.setCancelable(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finis = true;
                dialog.cancel();
            }
        });
        final Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (jwtlocal.equals("0"))
                    Toast.makeText(Donasi.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    config = getApplicationContext().getSharedPreferences("config", 0);
                    if (Utility.is6digit(strPIN)) {
                        if (fi == 1) new AsyncLAZ().execute();
                        else if (fi == 2) new AsyncLKAF().execute();
                        else if (fi == 3) new AsyncINFAQ().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void takeScreenshot(Dialog dialog) {
        try {
            String sDCard = "";
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            File newFolder = new File(sDCard + getString(R.string.path1));
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date naw = new Date();
            String now = sdf4.format(naw);
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = sDCard + getString(R.string.path2) + now + ".jpg";
            // create bitmap screen capture

            View v1 = dialog.getWindow().getDecorView().getRootView();
//            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            bitmap = mark(bitmap, getString(R.string.watermark));
            v1.setDrawingCacheEnabled(false);
            File imageFile = new File(mPath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(getApplicationContext(), "Screenshoot tersimpan di " + mPath, Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            Toast.makeText(Donasi.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private static Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setAlpha(1);
        paint.setColor(Color.LTGRAY);
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        canvas.drawText(watermark, 50, h * 99 / 100, paint);
        return result;
    }

    //tambahan ayik==
    @Override
    public void onPostExec(Boolean status, String jwt) {
        if (status) {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
            jwtlocal = jwt;
            cnd.countD.start();
        } else {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
            jwtlocal = jwt;

            if (jwtlocal.equals("401")) {
                Utility2.showAlertRelogin(Donasi.this);
            }
        }
        sts = sts + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
        if (stats && sts > 1) {
            cnd.countD.cancel();
            jwtlocal = "0";
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
            AuthLogin2 task = new AuthLogin2(Donasi.this, Donasi.this);
            task.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.action_status).setEnabled(false);
        menu.findItem(R.id.action_logout).setVisible(false);
        statusMenu = menu;
        if (!stats) {
            cnd.countD.cancel();
            jwtlocal = "0";
            AuthLogin2 task = new AuthLogin2(Donasi.this, Donasi.this);
            task.execute();
            stats = true;
            sts = 1;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        cnd.countD.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (Utility.isNetworkAvailable(context)) ;
            onResume();
        }
    }

    private void mReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
                    if (finis) //finish();
                        Utility2.showAlertRelogin(Donasi.this);
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(Donasi.this, Donasi.this);
                        task.execute();
                    }
                }
            }
        };
    }
}