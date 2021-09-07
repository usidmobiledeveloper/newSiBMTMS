package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian;

import android.annotation.SuppressLint;
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
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLoginPPOB;
import com.usid.mobilebmt.mandirisejahtera.utils.CtdPPOB;
import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONArray;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class UangPembelian extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private EditText edOperator, edNominal, edNoHp;
    private SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd"), time = new SimpleDateFormat("HHmmss");
    private String strNokartu = "", strPIN = "", strOperator = "", strImsi = "", kodeproduk = "", layanan, nom, trxID = "", strCAID, strKeyPass, strPRDID, strPRDIDBiller, strIDPEL, strBiaya, strPPOBID;
    private long biayaAdmin = 0, nominal = 0, jumlah = 0, hargaHPP = 0;
    private Button btnOK, btnDaftarTrf;
    private ProgressDialog pdLoadingInq;
    private ArrayList<HashMap<String, String>> oslist = new ArrayList<HashMap<String, String>>();
    private ListView list;
    private HashMap<String, String> mHashmap;
    private ArrayList kodeX = new ArrayList();
    private String[] namaY, kodeY;
    private ArrayList namaX = new ArrayList();
    //tambahan ayik==
    private Menu statusMenu;
    private String jwtlocal;
    private int sts = 0;
    private boolean stats = false;
    private CtdPPOB cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true, timeup = false, sdialog = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_uang_pembelian);

        cnd = new CtdPPOB(UangPembelian.this);
        edOperator = (EditText) findViewById(R.id.edOperator);
        edNominal = (EditText) findViewById(R.id.edNom);
        edNoHp = (EditText) findViewById(R.id.edNoHp);
        btnOK = (Button) findViewById(R.id.btnOK);
        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(UangPembelian.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
            /*try {
                strImsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            strImsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals(".") && !Character.toString(source.charAt(i)).equals(" ") && !Character.toString(source.charAt(i)).equals(",")) {
                        return "";
                    }
                }
                return null;
            }

        };
        /*edOperator.setFilters(new InputFilter[]{filter});
        edOperator.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});*/
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (jwtlocal.equals("0")) {
                    Toast.makeText(UangPembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    if (edOperator.getText().toString().equals("") || edNominal.getText().toString().equals(""))
                        showAlert("KESALAHAN", "Voucher dan nominal tidak boleh kosong!");
                    else if (edNoHp.getText().toString().equals(""))
                        showAlert("KESALAHAN", "Nomor HP tidak boleh kosong!");
                    /*else if (edNoHp.getText().toString().length() < 10 || edNoHp.getText().toString().length() > 15)
                        showAlert("KESALAHAN", "Nomor Handphone salah!");*/
                    else if (edNoHp.getText().toString().length() < 10 || edNoHp.getText().toString().length() > 15)
                        showAlert("KESALAHAN", "Nomor HP salah!");

                    else {
                        finis = false;
                        strIDPEL = edNoHp.getText().toString();

                        new AsyncInqBMT().execute();

                    }
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UangPembelian.this, DaftarUang.class);
                startActivityForResult(intent, 0);
            }
        });
        pdLoadingInq = new ProgressDialog(UangPembelian.this);
        pdLoadingInq.setCancelable(false);
        pdLoadingInq.setIndeterminate(false);
        pdLoadingInq.setTitle("Memproses");
        pdLoadingInq.setMessage("Tunggu...");
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncInqListEToll().execute();
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
        mReceiver();
    }

    public void onClickPulsa(View view) {
        switch (view.getId()) {
            case R.id.edOperator:
                AlertDialogViewOperator();
                break;
            case R.id.edNom:
                if (edOperator.getText().toString().equals("")) {
                    showAlert("KESALAHAN", "Silahkan pilih voucher terlebih dahulu!");
                } else {
                    String str = edOperator.getText().toString();
                    if (str.contains(" ")) {
                        String[] strArr = str.split(" ");
                        //kodeproduk = strArr[0];
                        kodeproduk = strArr[0] + " " + strArr[1];
                    } else {
                        kodeproduk = edOperator.getText().toString();
                    }


                    new AsyncListProduk().execute();
                }
                break;
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String rekening = intent.getStringExtra("NOHP");
                edNoHp.setText(rekening);
                edNoHp.requestFocus();
                edNoHp.setSelection(edNoHp.length());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private class AsyncInqListEToll extends AsyncTask<Void, Void, Void> {
        private Boolean status = false;
        private String ket = "405 Error koneksi terputus!!\nSilahkan coba lagi.";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoadingInq.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL objs = new URL(MyVal.URL_BASE_PPOB() + new NumSky(UangPembelian.this).decrypt(getResources().getString(R.string.urlListProdOVO)));

                Log.d("AYIK", "ue url "+ objs.toString());

                HttpURLConnection conJ = (HttpURLConnection) objs.openConnection();
                conJ.setRequestMethod("GET");
                conJ.setRequestProperty("Content-Type", "application/json");
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
                if (conJ.getResponseCode() == 200) {
                    status = true;

                    JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONObject jsonObject = (JSONObject) objects;
                    Iterator<String> iter = jsonObject.keySet().iterator();
                    mHashmap = new HashMap<>();
                    while (iter.hasNext()) {
                        String key = iter.next();
                        String value = (String) jsonObject.get(key);
                        namaX.add(value);
                        kodeX.add(key);
                        mHashmap.put(value, key);
                    }

                   /* JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONArray jsonArray = (JSONArray) objects;
                    mHashmap = new HashMap<>();
                    for (Object jObjs : jsonArray) {
                        JSONObject obj = (JSONObject) jObjs;
                        String kode = (String) obj.get("kode");
                        String nama = (String) obj.get("nama");

                        namaX.add(nama);
                        kodeX.add(kode);

                        mHashmap.put(nama, kode);
                    }*/


                } else {
                    status = false;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
            if (!status) {
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            } else {
                namaY = new String[namaX.size()];
                kodeY = new String[kodeX.size()];

                namaY = (String[]) namaX.toArray(namaY);
                kodeY = (String[]) kodeX.toArray(kodeY);
            }
        }
    }

    private class AsyncListProduk extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(UangPembelian.this);
        Boolean stInfoMutasi = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            oslist.clear();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(UangPembelian.this).decrypt(getResources().getString(R.string.urllistproduk)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonStrings(Utility.md5(strNokartu), Utility.md5(strImsi), kodeproduk);

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
                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stInfoMutasi = (Boolean) jsonObject.get("status");
                jwtpub = (String) jsonObject.get("jwt");
                if (stInfoMutasi) {
                    JSONArray jsonArray = (JSONArray) jsonObject.get("data");
                    for (Object jObjs : jsonArray) {
                        JSONObject objs = (JSONObject) jObjs;
                        String kd = (String) objs.get("kodeproduk");
                        String nama = (String) objs.get("namaproduk");
                        HashMap<String, String> map = new HashMap<>();
                        map.put("nmproduk", nama);
                        map.put("kdproduk", kd);
                        oslist.add(map);
                    }
                } else ket = (String) jsonObject.get("keterangan");
            } catch (Exception ex) {
                ex.printStackTrace();
                stInfoMutasi = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            if (stInfoMutasi) {
                ListAdapter adapter = new SimpleAdapter(UangPembelian.this, oslist, R.layout.list_item, new String[]{"nmproduk"}, new int[]{R.id.text1});
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UangPembelian.this);
                list = new ListView(UangPembelian.this);
                list.setAdapter(adapter);
                alertDialog.setView(list);
                final Dialog dialog = alertDialog.create();
                dialog.show();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String kdprod = oslist.get(+position).get("kdproduk");
                        String wil = oslist.get(+position).get("nmproduk");
                        strPRDID = kdprod;
                        edNominal.setText(wil);
                        dialog.dismiss();
                    }
                });
            } else {
                String msga = "#" + ket + "\n";
                showExt("GAGAL Inquiry List Produk", msga);
            }
        }

        private String toJsonStrings(String nokartu, String imsi, String kodeproduk) {
            JSONObject obj = new JSONObject();
            obj.put("nokartu", nokartu);
            obj.put("imsi", imsi);
            obj.put("kodeproduk", kodeproduk);
            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                obj.put("versi", pInfo.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return obj.toString();
        }
    }

    private class AsyncInqBMT extends AsyncTask<Void, Void, Void> {
        private Boolean status = false;
        private long IDtrx = 0;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi.";
        private int versi = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versi = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            pdLoadingInq.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(UangPembelian.this).decrypt(getResources().getString(R.string.urlinquirypulsa)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = Utility.toJsonStringEToll(Utility.md5(strNokartu), Utility.md5(strImsi), "0", strPRDID, "0", 0, "0", "0", "0", "0", "0", versi, "", "");

                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
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
                JSONParser parser = new JSONParser();

                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                status = (Boolean) jsonObject.get("status");
                if (status) {
                    IDtrx = (long) jsonObject.get("trxid");
                    strPRDIDBiller = (String) jsonObject.get("kodeprodukbiller");
                    biayaAdmin = (long) jsonObject.get("admin");
                    nominal = (long) jsonObject.get("nominal");
                    strCAID = (String) jsonObject.get("ppobupline");

                    strPPOBID = (String) jsonObject.get("ppobid");
                    strKeyPass = (String) jsonObject.get("ppobpass");

                    jwtpub = (String) jsonObject.get("jwt");
                } else ket = (String) jsonObject.get("keterangan");
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (status) {
                trxID = Long.toString(IDtrx);
                if (biayaAdmin == 0) strBiaya = "(sudah termasuk PPN)";
                else strBiaya = ": Rp. " + Utility.DoubleToCurrency(biayaAdmin);
                jumlah = biayaAdmin + nominal;
                new AsyncINQPPOB().execute();
            } else {
                if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            }
        }
    }

    private class AsyncINQPPOB extends AsyncTask<Void, Void, Void> {
        String ket = "", rc = "", statusPrdk = "";
        Boolean status = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(UangPembelian.this).decrypt(getResources().getString(R.string.urlCekStatusProd)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                String strCek = Utility.toJsonStringPPOBEToll(strPPOBID, strKeyPass, strPRDIDBiller, "", "", "", "");

                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
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

                if (conJ.getResponseCode() == 200) {
                    JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONObject jsonObject = (JSONObject) objects;
                    status = (Boolean) jsonObject.get("status");
                    if (status) {
                        JSONObject datas = (JSONObject) jsonObject.get("data");
                        rc = (String) datas.get("rc");
                        ket = (String) datas.get("ket");
                        statusPrdk = (String) datas.get("status_produk");
                    } else {
                        ket = (String) jsonObject.get("error");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();

            if (status) {
                countDPPOBLocal.start();
                if (rc.equals("0000")) {
                    if (statusPrdk.equals("1")) {
                        timeup = false;
                        layanan = edNominal.getText().toString();
                        showConfirmBeli("KONFIRMASI PEMBELIAN", ": " + layanan, ": " + edNoHp.getText().toString(), ": Rp. " + Utility.DoubleToCurrency(nominal), strBiaya, ": Rp. " + Utility.DoubleToCurrency(jumlah));
                    } else if (statusPrdk.equals("2")) {
                        String msga = "#508 Maaf " + ket + ", Silahkan coba beberapa saat lagi. Atau silahkan pilih nominal lain!\n";
                        showExt("GAGAL", msga);
                    } else if (statusPrdk.equals("0")) {
                        String msga = "#509 Maaf " + ket + ", Silahkan coba beberapa saat lagi. Atau silahkan pilih nominal lain!\n";
                        showExt("GAGAL", msga);
                    } else {
                        String msga = "#510 Maaf " + ket + ", Silahkan coba beberapa saat lagi. Atau silahkan pilih nominal lain!\n";
                        showExt("GAGAL", msga);
                    }
                } else {
                    String msga = "#510 Maaf " + ket + ", Silahkan coba beberapa saat lagi. Atau silahkan pilih nominal lain!\n";
                    showExt("GAGAL", msga);
                }
            } else {
                edNoHp.requestFocus();
                edOperator.setText("");
                edNominal.setText("");
                showAlert("Gagal #002", ket);
            }
        }
    }

    private class AsyncPaymentETollBMT extends AsyncTask<Void, Void, Void> {

        ProgressDialog pdLoading = new ProgressDialog(UangPembelian.this);
        private Boolean status = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String rc = "", ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.", struk = "", strFaktur = "", strRekAsal = "", strDate = "";

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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(UangPembelian.this).decrypt(getResources().getString(R.string.urlEToll)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", jwtlocal);
//                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), JenisTRX, strIDPEL + " a.n " + strNama, longJml, trxID, strCAID, md5(strKeyPass), strIDREFF,strIDPEL, Long.toString(longJml);
                String strCek = Utility.toJsonStringEToll(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), strPRDIDBiller, strIDPEL, jumlah, trxID, strPPOBID, strKeyPass, "", strIDPEL, 0, "", "");

                conJ.setConnectTimeout(30000);
                conJ.setReadTimeout(29000);
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
                if (conJ.getResponseCode() == 200) {
                    JSONParser parser = new JSONParser();
                    Object objects = parser.parse(response.toString());
                    JSONObject jsonObject = (JSONObject) objects;
                    status = (Boolean) jsonObject.get("status");
                    if (status) {
                        JSONObject datas = (JSONObject) jsonObject.get("data");
                        boolean sukses = (boolean) datas.get("sukses");
                        rc = (String) datas.get("rc");
                        ket = (String) datas.get("ket");
                        if (sukses) {
                            struk = (String) datas.get("sn");
                            strFaktur = (String) datas.get("faktur");
                            strRekAsal = (String) datas.get("rekening");
                            strDate = (String) datas.get("datetime");
                            trxID = (String) datas.get("idtrx");
                        }
                    } else {
                        ket = (String) jsonObject.get("error");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                status = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            edOperator.setText("");
            edNominal.setText("");
//            edNoHp.requestFocus();
            if (status) {
                if (rc.equals("0000")) {
                    ket = "Tanggal    : " + strDate + "\nFaktur     : " + strFaktur + "\nDari       : " + strRekAsal + "\nProduk     : " + layanan + "\nNomor HP  : " + strIDPEL
//                            + "\nNominal    : Rp. " + Utility.DoubleToCurrency(nominal)
//                            + "\nBiaya Admin: Rp. " + Utility.DoubleToCurrency(biayaAdmin)
                            + "\nTotal Bayar: Rp. " + Utility.DoubleToCurrency(jumlah) + "\nTrxID      : " + trxID + "\n";
//                    String keta = "Transaksi Pembelian Pulsa Berhasil" + "\nTanggal    : " + strDate + "\nProduk     : " + layanan + "\nNomor HP.  : " + strIDPEL + "\nNominal    : " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(nominal), 15) + "\nBiaya Admin: " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(biayaAdmin), 15) + "\nTotal Bayar: " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(jumlah), 15) + "\nSN " + struk + "\n";
                    String keta = "Transaksi Pembelian Uang Elektronik Berhasil" + "\nTanggal    : " + strDate + "\nFaktur     : " + strFaktur + "\nTrxID      : " + trxID + "\nDari       : " + strRekAsal + "\nProduk     : " + layanan + "\nNomor HP  : " + strIDPEL + "\nNominal    : " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(nominal), 15) + "\nBiaya Admin: " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(biayaAdmin), 15) + "\nTotal Bayar: " + Utility.leftRight("Rp.", Utility.DoubleToCurrency(jumlah), 15) + /*"\nSN " + struk +*/ "\n";
                    SysDB dbsys = new SysDB(UangPembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "Pembelian Pulsa Seluler SUKSES\n" + ket);
//                    dbsys.insertSys(strTgl, "Pembelian Pulsa Seluler (Reff)\n" + keta);
                    dbsys.insertSys(strTgl, "Pembelian " + kodeproduk + " SUKSES (Reff)\n" + keta);
                    dbsys.close();
                    showConfirmBeliSukses("PEMBELIAN BERHASIL", "" + strDate, "" + layanan,
                            "" + edNoHp.getText().toString(), "Rp " + Utility.DoubleToCurrency(nominal),
                            strBiaya.replace(": Rp. ", "Rp "), "Rp " + Utility.DoubleToCurrency(jumlah), "" + struk);
                } else {
                    String msga = "#" + rc + " - " + ket + "\n";
                    SysDB dbsys = new SysDB(UangPembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian " + kodeproduk + " STATUS " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian " + kodeproduk + " STATUS", msga);
                }
            } else {
                if (ket.startsWith("404")) {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(UangPembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian " + kodeproduk + " TIMEOUT " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian " + kodeproduk + " TIMEOUT", msga);
                } else {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(UangPembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian " + kodeproduk + " GAGAL " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian " + kodeproduk + " GAGAL", msga);
                }

            }
        }
    }

    private void AlertDialogViewOperator() {
        AlertDialog.Builder builder = new AlertDialog.Builder(UangPembelian.this);
        //builder.setTitle("Pilih Penyedia Layanan");
        builder.setSingleChoiceItems(namaY, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                strOperator = namaY[item];
                //kodeproduk = kodeY[item];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                edOperator.setText(strOperator);
                //edNoHp.setText(strOperator);
                edNominal.setText("");
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edNoHp.requestFocus();
                sdialog = false;
                finis = true;
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(UangPembelian.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private void showConfirmBeli(String Judul, final String prod, final String nohp, String nomi, String admin, String jumlah) {
        final Dialog dialog = new Dialog(UangPembelian.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trxuang);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView prods = (TextView) dialog.findViewById(R.id.oprt);
        prods.setText(prod);
        TextView noh = (TextView) dialog.findViewById(R.id.nohp);
        noh.setText(nohp);
        TextView nom = (TextView) dialog.findViewById(R.id.nom);
        nom.setText(nomi);
        TextView adm = (TextView) dialog.findViewById(R.id.admin);
        adm.setText(admin);
        TextView jml = (TextView) dialog.findViewById(R.id.jml);
        jml.setText(jumlah);
        SysDB dbsys = new SysDB(UangPembelian.this);
        dbsys.open();
        dbsys.CreateTableUang();
        int jumla = 0;
        Cursor cur1 = dbsys.cekUangData(edNoHp.getText().toString());
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();
        dbsys.close();
        final CheckBox cbsimpan = dialog.findViewById(R.id.cbSimpan);
        final LinearLayout lysimpan = dialog.findViewById(R.id.LySimpan);
        final EditText edNama = dialog.findViewById(R.id.infonama);
        lysimpan.setVisibility(View.GONE);
        cbsimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbsimpan.isChecked()) {
                    lysimpan.setVisibility(View.VISIBLE);
                    edNama.requestFocus();
                    edNama.setSelection(edNama.getText().length());
                } else lysimpan.setVisibility(View.GONE);
            }
        });
        if (jumla > 0) {
            cbsimpan.setVisibility(View.GONE);
            lysimpan.setVisibility(View.GONE);
            cbsimpan.setEnabled(false);
        }
        dialog.setCancelable(false);

        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setTextSize(20);
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0"))
                    Toast.makeText(UangPembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    if (cbsimpan.isChecked()) {
                        if (edNama.getText().toString().equals("")) {
                            Toast.makeText(UangPembelian.this, "Tidak bisa diproses, Nama tidak boleh kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                SysDB dbsys = new SysDB(UangPembelian.this);
                                dbsys.open();
                                String info = edNama.getText().toString().trim();
                                dbsys.insertUang(edNoHp.getText().toString(), info);
                                dbsys.close();
                            } catch (SQLiteException e) {
                                e.printStackTrace();
                            }
                            showConfirmPinBeli(getString(R.string.pin_mobilebmt));
                            dialog.dismiss();
                        }
                    } else {
                        showConfirmPinBeli(getString(R.string.pin_mobilebmt));
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinBeli(String msg) {
        final Dialog dialog = new Dialog(UangPembelian.this);
        finis = false;
        sdialog = true;
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
                if (timeup) {
                    edNoHp.requestFocus();
                    edOperator.setText("");
                    edNominal.setText("");
                }
                sdialog = false;
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
                if (jwtlocal.equals("0") || timeup)
                    Toast.makeText(UangPembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncPaymentETollBMT().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void showConfirmBeliSukses(String Judul, String tgls, String prod, String nohp, String nomi, String admin, String jumlah, String sn) {

        Intent antarbank = new Intent(UangPembelian.this, PembelianSukses.class);
        ArrayList<String> arr = new ArrayList<String>();

        arr.add("Tanggal#" + tgls);
        arr.add("Produk#" + prod);
        arr.add("No. HP#" + nohp);
        arr.add("Nominal#" + nomi + "");
        arr.add("Admin#" + admin + "");
        arr.add("Jumlah# " + jumlah + "");
        if (sn.isEmpty()) {
            arr.add("SN# " + sn);
        } else {
            arr.add("SN#" + sn);
        }

        antarbank.putExtra("judul", "Uang Elektronik Pembelian Berhasil");
        antarbank.putStringArrayListExtra("arr", arr);

        startActivity(antarbank);
        finish();


        /*final Dialog dialog = new Dialog(UangPembelian.this);
        Utility.playNotificationSound(UangPembelian.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trxuangsukses);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
        tgl.setText(tgls);
        TextView prods = (TextView) dialog.findViewById(R.id.oprt);
        prods.setText(prod);
        TextView noh = (TextView) dialog.findViewById(R.id.nohp);
        noh.setText(nohp);
        TextView nom = (TextView) dialog.findViewById(R.id.nom);
        nom.setText(nomi);
        TextView adm = (TextView) dialog.findViewById(R.id.admin);
        adm.setText(admin);
        TextView jml = (TextView) dialog.findViewById(R.id.jml);
        jml.setText(jumlah);
        TextView sns = (TextView) dialog.findViewById(R.id.sn);
        sns.setText(sn);
        sns.setVisibility(View.GONE);
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
            Toast.makeText(UangPembelian.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
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

    //tambahan baru AYIK-log==
    @Override
    public void onPostExec(Boolean status, String jwt) {
        if (status) {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
            jwtlocal = jwt;
            cnd.countDPPOB.start();
        } else {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
            jwtlocal = jwt;
        }
        sts = sts + 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
        if (stats && sts > 1) {
            cnd.countDPPOB.cancel();
            jwtlocal = "0";
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
            AuthLoginPPOB task = new AuthLoginPPOB(UangPembelian.this, UangPembelian.this);
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
            cnd.countDPPOB.cancel();
            jwtlocal = "0";
            AuthLoginPPOB task = new AuthLoginPPOB(UangPembelian.this, UangPembelian.this);
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
        cnd.countDPPOB.cancel();
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
                    if (finis && !sdialog) finish();
                    else {
                        cnd.countDPPOB.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLoginPPOB task = new AuthLoginPPOB(UangPembelian.this, UangPembelian.this);
                        task.execute();
                    }
                }
            }
        };
    }

    CountDownTimer countDPPOBLocal = new CountDownTimer(Utility.timeoutPPOB, 1000) {
        public void onTick(long millisUntilFinished) {
        }

        public void onFinish() {
            finis = true;
            timeup = true;
            if (!sdialog) {
                edOperator.setText("");
                edNoHp.setText("");
                edNominal.setText("");
                edNoHp.requestFocus();
            }
        }
    }.start();
}