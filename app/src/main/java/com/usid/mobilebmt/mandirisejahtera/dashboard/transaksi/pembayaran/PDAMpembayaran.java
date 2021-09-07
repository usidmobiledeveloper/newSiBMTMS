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
import android.util.Log;
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
import android.widget.RadioButton;
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

public class PDAMpembayaran extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private Button btnProses, btnDaftarTrf;
    private Bundle extras;
    private EditText edIdPelNo;
    private RadioButton rb1, rb2, rb3;
    private String idpel1, idpel2, idpel3;
    private LinearLayout mKonten;
    private TextView output, tvJudul;
    private String strWiil = "", strImsi = "", trxID = "", strPPOBID, strNokartu = "", strPIN = "", product = "", strKeyPass, strIDPEL, strNama, strIDREFF = "";
    private ProgressDialog pdLoadingInq;
    //tambahan ayik==
    private Menu statusMenu;
    private String jwtlocal="";
    private int sts = 0;
    private long longJml = 0;
    private boolean stats = false;
    private CtdPPOB cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true, timeup = false, sdialog = false;

    private static boolean retry = false;
    @BindView(R.id.btn_lanjut)
    Button btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdampembayaran);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //tambahan ayik==
        cnd = new CtdPPOB(PDAMpembayaran.this);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                product = "";
                strWiil = "";
            } else {
                product = extras.getString("JNSTRX");
                strWiil = extras.getString("WILAYAH");
            }
        } else {
            product = (String) savedInstanceState.getSerializable("JNSTRX");
            strWiil = (String) savedInstanceState.getSerializable("WILAYAH");
        }
        btnProses = (Button) findViewById(R.id.btnProses);
        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        edIdPelNo = (EditText) findViewById(R.id.edIdPelNo);
        tvJudul = (TextView) findViewById(R.id.tvJudul);
        mKonten = (LinearLayout) findViewById(R.id.mKontenStruk);
        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);

        if (product.equals("PDAMBANGKALAN") || product.equals("PDAMMOJOKERTO") || product.equals("PDAMTAPIN")) {
            rb1.setChecked(false);
            rb2.setChecked(true);
            rb3.setChecked(false);
        } else {
            rb1.setChecked(true);
            rb2.setChecked(false);
            rb3.setChecked(false);
        }

        output = (TextView) findViewById(R.id.output);
        //output.setText("Masukkan Nomor ID Pelanggan kemudian pilih salah satu dari pilihan ID Pel 1,2,3");
        btnProses.setVisibility(View.GONE);
        edIdPelNo.requestFocus();
        if (product.equals("WASDA")) edIdPelNo.setHint("Masukkan No. Sambungan");
        else edIdPelNo.setHint("Masukkan No. Pelanggan");
        tvJudul.setText(strWiil);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(PDAMpembayaran.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
            /*try {
                strImsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            strImsi = Utility.getIMSIRead(this);
        }
        pdLoadingInq = new ProgressDialog(PDAMpembayaran.this);
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
        } else {
            new AsyncInqBMT().execute();
        }
        btnProses.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(PDAMpembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmPinPDAMPasca(getString(R.string.pin_mobilebmt));
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PDAMpembayaran.this, DaftarPDAMPembayaran.class);
                startActivityForResult(intent, 0);
            }
        });
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                char[] acceptedChars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '/', '-', '.', ','};
                for (int i = start; i < end; i++) {
                    if (!new String(acceptedChars).contains(String.valueOf(source.charAt(i))) && !Character.isLetterOrDigit(source.charAt(i)) && !Character.toString(source.charAt(i)).equals(".") && !Character.toString(source.charAt(i)).equals(" ") && !Character.toString(source.charAt(i)).equals(",")) {
                        return "";
                    }
                }
                return null;
            }

        };
        edIdPelNo.setFilters(new InputFilter[]{filter});
        edIdPelNo.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(40)});
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
                    btnProses.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);

                    btnLanjut.setVisibility(View.VISIBLE);
                    retry = false;
                }
            }
        });
        edIdPelNo.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (jwtlocal.equals("0")) {
                        Toast.makeText(PDAMpembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                    } else {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edIdPelNo.getWindowToken(), 0);
                        if (rb1.isChecked()) {
                            idpel1 = edIdPelNo.getText().toString().trim();
                            idpel2 = "";
                            idpel3 = "";
                        } else if (rb2.isChecked()) {
                            idpel1 = "";
                            idpel2 = edIdPelNo.getText().toString().trim();
                            idpel3 = "";
                        } else if (rb3.isChecked()) {
                            idpel1 = "";
                            idpel2 = "";
                            idpel3 = edIdPelNo.getText().toString().trim();
                        } else {
                            idpel1 = "";
                            idpel2 = "";
                            idpel3 = "";
                        }

                        finis = false;
                        new AsyncInquiryPDAMPPOB().execute();
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
                Log.d("AYIK", "pdam:jwt "+ jwtlocal);
                if (jwtlocal.equals("0")) {
                    Toast.makeText(PDAMpembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edIdPelNo.getWindowToken(), 0);
                    if (rb1.isChecked()) {
                        idpel1 = edIdPelNo.getText().toString().trim();
                        idpel2 = "";
                        idpel3 = "";
                    } else if (rb2.isChecked()) {
                        idpel1 = "";
                        idpel2 = edIdPelNo.getText().toString().trim();
                        idpel3 = "";
                    } else if (rb3.isChecked()) {
                        idpel1 = "";
                        idpel2 = "";
                        idpel3 = edIdPelNo.getText().toString().trim();
                    } else {
                        idpel1 = "";
                        idpel2 = "";
                        idpel3 = "";
                    }
//                        strIDPEL = edIdPelNo.getText().toString().trim();
                    finis = false;
                    new AsyncInquiryPDAMPPOB().execute();
                }
            }
        });
        // tambahan baru ayik
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.rb1:
                if (checked) {
                    idpel1 = edIdPelNo.getText().toString();
                    idpel2 = "";
                    idpel3 = "";
                    btnProses.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                }
                break;
            case R.id.rb2:
                if (checked) {
                    idpel1 = "";
                    idpel2 = edIdPelNo.getText().toString();
                    idpel3 = "";
                    btnProses.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                }
                break;
            case R.id.rb3:
                if (checked) {
                    idpel1 = "";
                    idpel2 = "";
                    idpel3 = edIdPelNo.getText().toString();
                    btnProses.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                }
                break;
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(PDAMpembayaran.this).decrypt(getResources().getString(R.string.urlinquiry)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = Utility.toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), "0", product, "0", 0, "0", "0", "0", "0", "0", versi);
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
                    product = (String) jsonObject.get("kodeprodukbiller");
                    strPPOBID = (String) jsonObject.get("ppobid");
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

    private class AsyncInquiryPDAMPPOB extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(PDAMpembayaran.this);
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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(PDAMpembayaran.this).decrypt(getResources().getString(R.string.urlInqPDAM)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
//                String strCek = toJsonStringPPOB(strCAID, strKeyPass, JenisTRX, strIDPEL);
                String strCek = Utility.toJsonStringPDAM(strPPOBID, strKeyPass, product, idpel1, idpel2, idpel3, trxID);
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
                    btnProses.setVisibility(View.VISIBLE);
                    btnLanjut.setVisibility(View.GONE);
                    String[] parts = struk.split("\n");
                    ArrayList<String> arr = new ArrayList<>();
                    for (String part : parts) {
                        if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                            arr.add(part);
                        }
                    }
                    mKonten.removeAllViews();
                    Collections.reverse(arr);
                    Utility.addRow(arr, PDAMpembayaran.this, mKonten);
                } else if (rc.equals("0014") || rc.equals("0005")) {
                    if (!retry) {

                        if (rb1.isChecked()) {
                            rb1.setChecked(false);
                            rb2.setChecked(true);
                        } else {
                            rb1.setChecked(true);
                            rb2.setChecked(false);
                        }

                        if (rb1.isChecked()) {
                            idpel1 = edIdPelNo.getText().toString().trim();
                            idpel2 = "";
                            idpel3 = "";
                        } else if (rb2.isChecked()) {
                            idpel1 = "";
                            idpel2 = edIdPelNo.getText().toString().trim();
                            idpel3 = "";
                        } else if (rb3.isChecked()) {
                            idpel1 = "";
                            idpel2 = "";
                            idpel3 = edIdPelNo.getText().toString().trim();
                        } else {
                            idpel1 = "";
                            idpel2 = "";
                            idpel3 = "";
                        }

                        retry = true;
                        finis = false;
                        new AsyncInquiryPDAMPPOB().execute();
                    } else {
                        edIdPelNo.requestFocus();
                        btnProses.setVisibility(View.GONE);
                        mKonten.setVisibility(View.GONE);
                        showAlert("Gagal #" + rc, ket);
                    }
                } else {
                    edIdPelNo.requestFocus();
                    mKonten.setVisibility(View.GONE);
                    btnProses.setVisibility(View.GONE);
                    showAlert("Gagal #" + rc, ket);
                }
            } else {
                edIdPelNo.requestFocus();
                btnProses.setVisibility(View.GONE);
                mKonten.setVisibility(View.GONE);
                showAlert("Gagal #002", ket);
            }
        }
    }

    private class AsyncPaymentPDAMPascaBMT extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(PDAMpembayaran.this);
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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(PDAMpembayaran.this).decrypt(getResources().getString(R.string.urlPayPDAM)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", jwtlocal);
                String strCek = Utility.toJsonStringPDAMpayment(Utility.md5(strNokartu), Utility.md5(strImsi),
                        Utility.md5(strPIN), product, strIDPEL + " a.n " + strNama, longJml, trxID, strPPOBID,
                        strKeyPass, strIDREFF, idpel1, idpel2, idpel3);
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
            btnProses.setVisibility(View.GONE);
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
//                    ket = strWiil + "\nTanggal    : " + strDate + "\nFaktur     : " + strFaktur + "\nDari       : " + strRekAsal + "\nNo. PDAM   : " + strIDPEL + "\nNama Pel   : " + strNama + "\nTotal Bayar: Rp. " + Utility.DoubleToCurrency(longJml) + "\nTrxID      : " + trxID + "\n";
                    ket = strWiil + "\nTanggal    : " + strDate + "\nFaktur     : " + strFaktur + "\nTrxID      : " + trxID + "\nDari       : " + strRekAsal + "\n";
                    String keta = "\nTransaksi Pembayaran " + strWiil + " Berhasil\n";
                    String isi = "";
                    for (String item : arr) {
                        isi = isi + item + "\n";
                    }
                    keta = keta + isi;
                    SysDB dbsys = new SysDB(PDAMpembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.CreateTablePDAM();
//                    dbsys.insertSys(strTgl, "Pembayaran PDAM SUKSES\n" + ket);
//                    dbsys.insertSys(strTgl, "Pembayaran PDAM (Reff)\n" + keta);
                    dbsys.insertSys(strTgl, "Pembayaran PDAM SUKSES (Reff)\n" + ket + keta);
                    int jumla = 0;
                    Cursor cur1 = dbsys.cekPDAMData(strIDPEL);
                    if (cur1.moveToFirst()) {
                        jumla = cur1.getInt(0);
                    }
                    cur1.close();
                    if (jumla == 0) dbsys.insertPDAM(strIDPEL, strNama);
                    dbsys.close();
                    Intent intentPayment = new Intent(PDAMpembayaran.this, PDAMpembayaranSukses.class);
                    intentPayment.putExtra("JUDUL", strWiil);
                    intentPayment.putExtra("ISI", isi);
                    startActivity(intentPayment);
                    finish();
                } else {
                    String msga = "#" + rc + " - " + ket + "\n";
                    SysDB dbsys = new SysDB(PDAMpembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran PDAM STATUS " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran PDAM STATUS", msga);
                }
            } else {
                if (ket.startsWith("404")) {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(PDAMpembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran PDAM TIMEOUT " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran PDAM TIMEOUT", msga);
                } else {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(PDAMpembayaran.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembayaran PDAM GAGAL " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembayaran PDAM GAGAL", msga);
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
        new AlertDialog.Builder(PDAMpembayaran.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        new AlertDialog.Builder(PDAMpembayaran.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinPDAMPasca(String msg) {
        final Dialog dialog = new Dialog(PDAMpembayaran.this);
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
                    btnProses.setVisibility(View.GONE);
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
                    Toast.makeText(PDAMpembayaran.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncPaymentPDAMPascaBMT().execute();
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
            Log.d("AYIIK", "onpost:jwt "+ jwt);
            Log.d("AYIIK", "onpost:jwtlocal "+ jwtlocal);
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
            AuthLoginPPOB task = new AuthLoginPPOB(PDAMpembayaran.this, PDAMpembayaran.this);
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
            AuthLoginPPOB task = new AuthLoginPPOB(PDAMpembayaran.this, PDAMpembayaran.this);
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
                        AuthLoginPPOB task = new AuthLoginPPOB(PDAMpembayaran.this, PDAMpembayaran.this);
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
                btnProses.setVisibility(View.GONE);
                mKonten.setVisibility(View.GONE);
            }
        }
    }.start();
}
