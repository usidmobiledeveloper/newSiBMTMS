package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran;

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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class TelkomPembayaran extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private TextView output = null;
    private Bundle extras;
    private Button btnProsesTelkom, btnDaftarTrf;
    private EditText edIdPelNo;
    private TextView tvJudul;
    private LinearLayout mKonten;
    private String strWiil = "", strImsi = "", trxID = "", strNokartu = "", strPIN = "", JenisTRX = "", strKeyPass, strIDPEL, strNama = "", strPPOBID, strIDREFF = "";
    private ProgressDialog pdLoadingInq;
    //tambahan ayik==
    private Menu statusMenu;
    private String jwtlocal;
    private int sts = 0;
    private long longJml = 0;
    private boolean stats = false;
    private CtdPPOB cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true, timeup = false, sdialog = false;

    @BindView(R.id.btn_lanjut)
    Button btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telkom_pembayaran);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        //tambahan ayik==
        cnd = new CtdPPOB(TelkomPembayaran.this);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                JenisTRX = "";
                strWiil = "";
            } else {
                JenisTRX = extras.getString("JNSTRX");
                strWiil = extras.getString("WILAYAH");
            }
        } else {
            JenisTRX = (String) savedInstanceState.getSerializable("JNSTRX");
            strWiil = (String) savedInstanceState.getSerializable("WILAYAH");
        }
        mKonten = (LinearLayout) findViewById(R.id.mKontenStruk);
        btnProsesTelkom = (Button) findViewById(R.id.btnProses);
        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        edIdPelNo = (EditText) findViewById(R.id.edIdPelNo);
        tvJudul = (TextView) findViewById(R.id.tvJudul);
        output = (TextView) findViewById(R.id.output);
        edIdPelNo.requestFocus();
        if (JenisTRX.contains("TELEPON")) {
            tvJudul.setText(strWiil);
            output.setText("Masukkan ID Pelanggan (Kode area+No.Telp)");
        } else if (JenisTRX.contains("SPEEDY")) {
            tvJudul.setText(strWiil);
            output.setText("Masukkan ID Pelanggan (No.Speedy)");
        } else {
            tvJudul.setText(strWiil);
            output.setText("Masukkan Nomor ID Pelanggan");
            edIdPelNo.setHint("No. ID Pelanggan");
        }
        btnProsesTelkom.setVisibility(View.GONE);
        mKonten.setVisibility(View.GONE);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(TelkomPembayaran.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
          /*  try {
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
        pdLoadingInq = new ProgressDialog(TelkomPembayaran.this);
        pdLoadingInq.setCancelable(false);
        pdLoadingInq.setIndeterminate(false);
        pdLoadingInq.setTitle("Memproses");
        pdLoadingInq.setMessage("Tunggu...");
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncInqBMT().execute();
        }
        btnProsesTelkom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(TelkomPembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmPinTelkomPasca(getString(R.string.pin_mobilebmt));
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TelkomPembayaran.this, DaftarTELKOMPembayaran.class);
                startActivityForResult(intent, 0);
            }
        });
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
        edIdPelNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    btnProsesTelkom.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                    btnLanjut.setVisibility(View.VISIBLE);
                }
            }
        });
        edIdPelNo.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (jwtlocal.equals("0")) {
                        Toast.makeText(TelkomPembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                    } else {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edIdPelNo.getWindowToken(), 0);
                        strIDPEL = edIdPelNo.getText().toString();
                        finis = false;
                        new AsyncInquiryTelkomPPOB().execute();
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(TelkomPembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edIdPelNo.getWindowToken(), 0);
                    strIDPEL = edIdPelNo.getText().toString();
                    finis = false;
                    new AsyncInquiryTelkomPPOB().execute();
                }
            }
        });
        // tambahan baru ayik
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(TelkomPembayaran.this).decrypt(getResources().getString(R.string.urlinquiry)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = Utility.toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), "0", JenisTRX, "0", 0, "0", "0", "0", "0", "0", versi);
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
                    JenisTRX = (String) jsonObject.get("kodeprodukbiller");
                    strPPOBID = (String) jsonObject.get("ppobid");
//                    strCAID = (String) jsonObject.get("ppobupline");
                    strKeyPass = (String) jsonObject.get("ppobpass");
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
            if (pdLoadingInq.isShowing()) pdLoadingInq.dismiss();
            if (status) {
                trxID = Long.toString(IDtrx);
            } else {
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            }
        }
    }

    private class AsyncInquiryTelkomPPOB extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(TelkomPembayaran.this);
        private String ket = "001 Error koneksi terputus!!\nSilahkan coba lagi.", struk = "", rc = "";
        private Boolean status = false;

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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(TelkomPembayaran.this).decrypt(getResources().getString(R.string.urlInqPPOB)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
//                String strCek = toJsonStringPPOB(strCAID, strKeyPass, JenisTRX, strIDPEL);
                String strCek = Utility.toJsonStringPPOB(strPPOBID, strKeyPass, JenisTRX, strIDPEL, trxID);
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
                            struk = (String) datas.get("struk");
                            strIDPEL = (String) datas.get("idpel");
                            strNama = (String) datas.get("nama_pelanggan");
                            longJml = (long) datas.get("nominal");
                            strIDREFF = (String) datas.get("noresi");
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
            if (status) {
                countDPPOBLocal.start();
                if (rc.equals("0000")) {
                    timeup = false;
                    mKonten.setVisibility(View.VISIBLE);
                    btnLanjut.setVisibility(View.GONE);
                    btnProsesTelkom.setVisibility(View.VISIBLE);
                    String[] parts = struk.split("\n");
                    ArrayList<String> arr = new ArrayList<>();
                    for (String part : parts) {
                        if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                            arr.add(part);
                        }
                    }
                    mKonten.removeAllViews();
                    Collections.reverse(arr);
                    Utility.addRow(arr, TelkomPembayaran.this, mKonten);
                } else {
                    edIdPelNo.requestFocus();
                    btnProsesTelkom.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                    showAlert("Gagal #" + rc, ket);
                }
            } else {
                edIdPelNo.requestFocus();
                btnProsesTelkom.setVisibility(View.GONE);
                mKonten.setVisibility(View.GONE);
                showAlert("Gagal #002", ket);
            }
        }
    }

    private class AsyncPaymentTelkomPascaBMT extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(TelkomPembayaran.this);
        private Boolean status = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String rc = "",
                ket = "404 Kesalahan koneksi, transaksi terputus! (TIMEOUT)\nSilahkan cek mutasi transaksi terakhir.", struk = "", strFaktur = "", strRekAsal = "", strDate = "";

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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(TelkomPembayaran.this).decrypt(getResources().getString(R.string.urlPayPPOB)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", jwtlocal);
//                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), JenisTRX, strIDPEL + " a.n " + strNama, longJml, trxID, strCAID, md5(strKeyPass), strIDREFF, strIDPEL, Long.toString(longJml));
                String strCek = Utility.toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), JenisTRX, strIDPEL + " a.n " + strNama, longJml, trxID, strPPOBID, strKeyPass, strIDREFF, strIDPEL, 0);
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
                            struk = (String) datas.get("struk");
                            longJml = (long) datas.get("nominal");
                            strFaktur = (String) datas.get("faktur");
                            strIDREFF = (String) datas.get("noresi");
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
            edIdPelNo.requestFocus();
            btnProsesTelkom.setVisibility(View.GONE);
            mKonten.setVisibility(View.GONE);
            if (status) {
                if (rc.equals("0000")) {
                    String[] parts = struk.split("\n");
                    ArrayList<String> arr = new ArrayList<>();
                    for (String part : parts) {
                        if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                            arr.add(part);
                        }
                    }
//                    ket = strWiil + " Pascabayar" + "\nTanggal     : " + strDate + "\nFaktur      : " + strFaktur + "\nDari        : " + strRekAsal + "\nID Pelanggan: " + strIDPEL + "\nNama Pel    : " + strNama + "\nTotal Bayar : Rp. " + Utility.DoubleToCurrency(longJml) + "\nTrxID       : " + trxID + "\n";
                    ket = strWiil + " Pascabayar\nTanggal     : " + strDate + "\nFaktur      : " + strFaktur + "\nTrxID       : " + trxID + "\nDari        : " + strRekAsal + "\n";
                    String keta = "\nTransaksi Pembayaran " + strWiil + " Berhasil\n";
                    String isi = "";
                    for (String item : arr) {
                        isi = isi + item + "\n";
                    }
                    keta = keta + isi;

                    SysDB dbsys = new SysDB(TelkomPembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.CreateTableTELKOM();
//                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " SUKSES\n" + ket);
//                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " (Reff)\n" + keta);
                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " SUKSES (Reff)\n" + ket + keta);
                    int jumla = 0;
                    Cursor cur1 = dbsys.cekTELKOMData(strIDPEL);
                    if (cur1.moveToFirst()) {
                        jumla = cur1.getInt(0);
                    }
                    cur1.close();
                    if (jumla == 0) dbsys.insertTELKOM(strIDPEL, strNama);
                    dbsys.close();
                    Intent intentPayment = new Intent(TelkomPembayaran.this, TelkomPembayaranSukses.class);
                    intentPayment.putExtra("JUDUL", strWiil);
                    intentPayment.putExtra("ISI", isi);
                    startActivity(intentPayment);
                    finish();
                } else {
                    String msga = "#" + rc + " - " + ket + "\n";
                    SysDB dbsys = new SysDB(TelkomPembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " STATUS " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran " + strWiil + " STATUS", msga);
                }
            } else {
                if(ket.startsWith("404")){
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(TelkomPembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " TIMEOUT " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran " + strWiil + " TIMEOUT", msga);
                }else {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(TelkomPembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran " + strWiil + " GAGAL " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran " + strWiil + " GAGAL", msga);
                }

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String rekening = intent.getStringExtra("IDPEL");
                edIdPelNo.setText(rekening);
                edIdPelNo.requestFocus();
                edIdPelNo.setSelection(edIdPelNo.length());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(TelkomPembayaran.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                edIdPelNo.setText("");
                edIdPelNo.requestFocus();
                sdialog = false;
                finis = true;
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(TelkomPembayaran.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinTelkomPasca(String msg) {
        final Dialog dialog = new Dialog(TelkomPembayaran.this);
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
                    edIdPelNo.requestFocus();
                    btnProsesTelkom.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
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
                    Toast.makeText(TelkomPembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncPaymentTelkomPascaBMT().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    // tambahan baru ayik
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
            AuthLoginPPOB task = new AuthLoginPPOB(TelkomPembayaran.this, TelkomPembayaran.this);
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
            AuthLoginPPOB task = new AuthLoginPPOB(TelkomPembayaran.this, TelkomPembayaran.this);
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
//                        Toast.makeText(PLNpembayaran.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countDPPOB.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLoginPPOB task = new AuthLoginPPOB(TelkomPembayaran.this, TelkomPembayaran.this);
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
                edIdPelNo.requestFocus();
                btnProsesTelkom.setVisibility(View.GONE);
                mKonten.setVisibility(View.GONE);
            }
        }
    }.start();
}
