package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian;

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
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
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

public class PLNpembelian extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private LinearLayout mKonten;
    private LinearLayout lyTampilPra;
    private Button btnProsesPra, btnDaftarTrf;
    private EditText edIdPel, edNominal;
    private String strPPOBID, trxID = "", strNokartu = "", strPIN = "", JenisTRX, nom = "", strKeyPass, strIDPEL, strIDREFF, strNama = "", strImsi = "";
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
    private String[] nompln = {"20.000", "50.000", "100.000", "200.000", "500.000", "1.000.000"};

    @BindView(R.id.btn_lanjut)
    Button btnLanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plnpembelian);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        cnd = new CtdPPOB(PLNpembelian.this);
        edNominal = (EditText) findViewById(R.id.edNom);
        mKonten = (LinearLayout) findViewById(R.id.mKontenStruk);
        lyTampilPra = (LinearLayout) findViewById(R.id.LyTampilPra);
        lyTampilPra.setVisibility(View.GONE);
        mKonten.setVisibility(View.GONE);
        edIdPel = (EditText) findViewById(R.id.edIdPel);
        btnProsesPra = (Button) findViewById(R.id.btnProsesPra);
        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        btnProsesPra.setVisibility(View.GONE);
        edIdPel.requestFocus();
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(PLNpembelian.this);
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
        pdLoadingInq = new ProgressDialog(PLNpembelian.this);
        pdLoadingInq.setCancelable(false);
        pdLoadingInq.setIndeterminate(false);
        pdLoadingInq.setTitle("Memproses");
        pdLoadingInq.setMessage("Tunggu...");
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncInqBMT().execute();
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
        edIdPel.setFilters(new InputFilter[]{filter});
        edIdPel.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(12)});
        btnProsesPra.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(PLNpembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(edNominal.getText().toString())) {
                        Toast.makeText(PLNpembelian.this, "Isian nominal tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    } else {
                        String nominal;
                        nominal = edNominal.getText().toString().trim().replace(".", "");
                        longJml = Long.parseLong(nominal);
                        showConfirmPinPLNPra(getString(R.string.pin_mobilebmt));
                    }
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PLNpembelian.this, DaftarPLNPembelian.class);
                startActivityForResult(intent, 0);
            }
        });
        edIdPel.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    lyTampilPra.setVisibility(View.GONE);
                    mKonten.setVisibility(View.GONE);
                    btnProsesPra.setVisibility(View.GONE);
                    btnLanjut.setVisibility(View.VISIBLE);
                }
            }
        });
        edIdPel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    if (jwtlocal.equals("0")) {
                        Toast.makeText(PLNpembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                    } else {
                        finis = false;
                        final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edIdPel.getWindowToken(), 0);
                        strIDPEL = edIdPel.getText().toString().trim();
                        if (strIDPEL.length() < 11) {
                            edIdPel.setError("No. Meter/ID Pelanggan minimal 11 digit");
                        } else {
                            new AsyncInquiryPLNpraPPOB().execute();
                        }

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
                    Toast.makeText(PLNpembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    finis = false;
                    final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edIdPel.getWindowToken(), 0);
                    strIDPEL = edIdPel.getText().toString().trim();
                    if (strIDPEL.length() < 11) {
                        edIdPel.setError("No. Meter/ID Pelanggan minimal 11 digit");
                    } else {
                        new AsyncInquiryPLNpraPPOB().execute();
                    }

                }
            }
        });

        /*TextView tv = findViewById(R.id.textView1);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String isi = "Pembelian Token PLN SUKSES (Reff)\n" +
                        "Tanggal     : 08/07/2021 15:38\n" +
                        "Faktur  : MU905202107080000001\n" +
                        "TrxID       : 1625733469401\n" +
                        "Dari        : 905.11.000002.01\n" +
                        "    \n" +
                        "Transaksi Pembelian Listrik Berhasil\n" +
                        "TANGGAL     : 08-07-2021\n" +
                        "NO. RESI    : 2947703\n" +
                        "NO. METER   : 32123117528\n" +
                        "IDPEL       : 516021355780\n" +
                        "NAMA        : HASBIAN SAPUTRA\n" +
                        "TARIF/DAYA  : R1/1300 VA\n" +
                        "REF         : D600B2F49878B11D431AA4B9BE0C46A5\n" +
                        "JML KWH     :            12.80\n" +
                        "MATERAI     : Rp          0.00\n" +
                        "PPN         : Rp          0.00\n" +
                        "PPJ         : Rp      1,652.00\n" +
                        "ANGSURAN    : Rp          0.00\n" +
                        "RP TOKEN    : Rp     18,348.00\n" +
                        "ADMIN BANK  : Rp      2,500.00\n" +
                        "RP BAYAR    : Rp     22,500.00\n" +
                        "TOKEN       : 6484 3216 9084 5940 0154\n" +
                        "Informasi Hubungi Call Center 123 Atau hubungi PLN Terdekat\n";

                Intent intentPayment = new Intent(PLNpembelian.this, PLNpembelianSukses.class);
                intentPayment.putExtra("ISI", isi);
                startActivity(intentPayment);
                finish();
            }
        });*/

        mReceiver();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();

        registerReceiver(receiver, intentFilter);

    }

    private void AlertDialogViewNominal(final String[] x) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PLNpembelian.this);
        builder.setTitle("Pilih Nominal");

        builder.setSingleChoiceItems(x, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                nom = x[item];
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                edNominal.setText(nom);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    public void onClickPLN(View view) {
        switch (view.getId()) {
            case R.id.edNom:
                AlertDialogViewNominal(nompln);
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(PLNpembelian.this).decrypt(getResources().getString(R.string.urlinquiry)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = Utility.toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), "0", "PLNPRE", "0", 0, "0", "0", "0", "0", "0", versi);
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
                    strPPOBID = (String) jsonObject.get("ppobid");
                    strKeyPass = (String) jsonObject.get("ppobpass");
                    JenisTRX = (String) jsonObject.get("kodeprodukbiller");
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

    private class AsyncInquiryPLNpraPPOB extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(PLNpembelian.this);
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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(PLNpembelian.this).decrypt(getResources().getString(R.string.urlInqPPOB)));
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
            countDPPOBLocal.start();
            if (status) {
                if (rc.equals("0000")) {
                    timeup = false;
                    mKonten.setVisibility(View.VISIBLE);
                    lyTampilPra.setVisibility(View.VISIBLE);
                    btnLanjut.setVisibility(View.GONE);
                    edNominal.setText("");
                    btnProsesPra.setVisibility(View.VISIBLE);
                    String[] parts = struk.split("\n");
                    ArrayList<String> arr = new ArrayList<>();
                    for (String part : parts) {
                        if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                            arr.add(part);
                        }
                    }
                    mKonten.removeAllViews();
                    Collections.reverse(arr);
                    Utility.addRow(arr, PLNpembelian.this, mKonten);
                } else {
                    edIdPel.requestFocus();
                    mKonten.setVisibility(View.GONE);
                    lyTampilPra.setVisibility(View.GONE);
                    btnProsesPra.setVisibility(View.GONE);
                    showAlert("Gagal #" + rc, ket);
                }
            } else {
                edIdPel.requestFocus();
                mKonten.setVisibility(View.GONE);
                lyTampilPra.setVisibility(View.GONE);
                btnProsesPra.setVisibility(View.GONE);
                showAlert("Gagal #002", ket);
            }
        }
    }

    private class AsyncPaymentPLNPraBMT extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(PLNpembelian.this);
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
                URL obj = new URL(MyVal.URL_BASE_PPOB() + new NumSky(PLNpembelian.this).decrypt(getResources().getString(R.string.urlPayPPOB)));

                Log.d("AYIK", "plntoken:url "+obj.toString());

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", jwtlocal);
//                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), JenisTRX, strIDPEL + " a.n " + strNama, longJml, trxID, strCAID, md5(strKeyPass), strIDREFF,strIDPEL, Long.toString(longJml);
                String strCek = Utility.toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), Utility.md5(strPIN), JenisTRX, strIDPEL + " a.n " + strNama, longJml, trxID, strPPOBID, strKeyPass, strIDREFF, strIDPEL, 0);

                Log.d("AYIK", "plntoken:body "+strCek);

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

                Log.d("AYIK", "plntoken:response "+response.toString());

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
            edIdPel.requestFocus();
            mKonten.setVisibility(View.GONE);
            lyTampilPra.setVisibility(View.GONE);
            btnProsesPra.setVisibility(View.GONE);
            if (status) {
                if (rc.equals("0000")) {
                    String[] parts = struk.split("\n");
                    ArrayList<String> arr = new ArrayList<>();
                    for (String part : parts) {
                        if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                            arr.add(part);
                        }
                    }
//                    ket = "Tanggal     : " + strDate + "\nFaktur      : " + strFaktur + "\nDari        : " + strRekAsal + "\nID Pelanggan: " + strIDPEL + "\nNama Pel    : " + strNama + "\nTotal Bayar : Rp. " + Utility.DoubleToCurrency(longJml) + "\nTrxID       : " + trxID + "\n";
                    ket = "Tanggal     : " + strDate + "\nFaktur      : " + strFaktur + "\nTrxID       : " + trxID + "\nDari        : " + strRekAsal + "\n";
                    String strpesan = "Informasi Hubungi Call Center 123 Atau hubungi PLN Terdekat";
                    String keta = "\nTransaksi Pembelian Listrik Berhasil\n";
                    String isi = "";
                    for (String item : arr) {
                        isi = isi + item + "\n";
                    }
                    keta = keta + isi + strpesan;
                    SysDB dbsys = new SysDB(PLNpembelian.this);
                    dbsys.open();
                    dbsys.CreateTablePLNpra();
                    dbsys.CreateTableSys();
//                    dbsys.insertSys(strTgl, "Pembelian Token PLN SUKSES\n" + ket);
//                    dbsys.insertSys(strTgl, "Pembelian Token PLN (Reff)\n" + keta);
                    dbsys.insertSys(strTgl, "Pembelian Token PLN SUKSES (Reff)\n" + ket + keta);
                    int jumla = 0;
                    Cursor cur1 = dbsys.cekPLNpraData(edIdPel.getText().toString().trim());
                    if (cur1.moveToFirst()) {
                        jumla = cur1.getInt(0);
                    }
                    cur1.close();
                    if (jumla == 0)
                        dbsys.insertPLNpra(edIdPel.getText().toString().trim(), strNama);
                    dbsys.close();
                    Intent intentPayment = new Intent(PLNpembelian.this, PLNpembelianSukses.class);
                    intentPayment.putExtra("ISI", isi);
                    startActivity(intentPayment);
                    finish();
                } else {
                    String msga = "#" + rc + " - " + ket + "\n";
                    SysDB dbsys = new SysDB(PLNpembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian Token PLN STATUS " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian Token PLN STATUS", msga);
                }
            } else {

                if (ket.startsWith("404")) {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(PLNpembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian Token PLN TIMEOUT " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian Token PLN TIMEOUT", msga);
                } else {
                    String msga = "#" + ket + "\n";
                    SysDB dbsys = new SysDB(PLNpembelian.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Pembelian Token PLN GAGAL " + msga);
                    dbsys.close();
                    sdialog = true;
                    showExt("Pembelian Token PLN GAGAL", msga);
                }

            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String rekening = intent.getStringExtra("IDPEL");
                edIdPel.setText(rekening);
                edIdPel.requestFocus();
                edIdPel.setSelection(edIdPel.length());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(PLNpembelian.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                edIdPel.setText("");
                edIdPel.requestFocus();
                sdialog = false;
                finis = true;
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(PLNpembelian.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinPLNPra(String msg) {
        final Dialog dialog = new Dialog(PLNpembelian.this);
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
                    edIdPel.requestFocus();
                    mKonten.setVisibility(View.GONE);
                    lyTampilPra.setVisibility(View.GONE);
                    btnProsesPra.setVisibility(View.GONE);
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
                    Toast.makeText(PLNpembelian.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncPaymentPLNPraBMT().execute();
                    } else {
                        showAlert("Error", "Pin harus 6 digit angka!");
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    //tambahan baru ayik==
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
            AuthLoginPPOB task = new AuthLoginPPOB(PLNpembelian.this, PLNpembelian.this);
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
            AuthLoginPPOB task = new AuthLoginPPOB(PLNpembelian.this, PLNpembelian.this);
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
                        AuthLoginPPOB task = new AuthLoginPPOB(PLNpembelian.this, PLNpembelian.this);
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
                edIdPel.requestFocus();
                lyTampilPra.setVisibility(View.GONE);
                mKonten.setVisibility(View.GONE);
                btnProsesPra.setVisibility(View.GONE);
            }
        }
    }.start();
}
