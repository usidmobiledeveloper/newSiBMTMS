package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer;

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
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.Date;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.DoubleToCurrency;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class AntarRekeningToko extends AppCompatActivity implements IAsyncHandler {
    private Button btnCancel, btnTransfer, btnDaftarTrf;
    private TextView tvErrorMsg;
    private EditText edNominal, edRekTujuan, edBerita;
    private String trxID = "", strBerita = "", strRekTujuan = "", strNokartu = "", strImsi = "", strPIN = "", strNama = "";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_transfersesama);

        // tambahan baru ayik
        cnd = new Ctd(AntarRekeningToko.this);

        btnDaftarTrf = (Button) findViewById(R.id.buttonDaftarRek);
        btnTransfer = (Button) findViewById(R.id.btnTransfer);
        btnCancel = (Button) findViewById(R.id.btCancel);
        tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
        edNominal = (EditText) findViewById(R.id.edNomTransfer);
        edRekTujuan = (EditText) findViewById(R.id.edRekTujuan);
        edBerita = (EditText) findViewById(R.id.edBerita);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
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
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        edNominal.addTextChangedListener(new NumberTextWatcher(edNominal));
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
        edBerita.setFilters(new InputFilter[]{filter});
        edBerita.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(50)});
        edRekTujuan.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
        edRekTujuan.addTextChangedListener(new TextWatcher() {
            String mPreviousMac = null;

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredMac = edRekTujuan.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);

                int selectionStart = edRekTujuan.getText().toString().length();
                if (!enteredMac.equals("")) {
                    formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                    int lengthDiff = formattedMac.length() - enteredMac.length();
                    setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
                }
            }

            private String clearNonMacCharacters(String mac) {
                return mac.toString().replaceAll("[^0-9]", "");
            }

            private String formatMacAddress(String cleanMac) {
                int grouppedCharacters = 0;
                String formattedMac = "";
                for (int i = 0; i < cleanMac.length(); ++i) {
                    formattedMac += cleanMac.charAt(i);
                    ++grouppedCharacters;
                    if (grouppedCharacters == 3) {
                        formattedMac += ".";
                    } else if (grouppedCharacters == 5) {
                        formattedMac += ".";
                    } else if (grouppedCharacters == 11) {
                        formattedMac += ".";
                    }
                }

                if (cleanMac.length() == 14)
                    formattedMac = formattedMac.substring(0, formattedMac.length() - 1);

                return formattedMac;
            }

            private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
                if (mPreviousMac != null && mPreviousMac.length() > 1) {
                    int previousColonCount = colonCount(mPreviousMac);
                    int currentColonCount = colonCount(enteredMac);

                    if (currentColonCount < previousColonCount) {
                        formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                        String cleanMac = clearNonMacCharacters(formattedMac);
                        formattedMac = formatMacAddress(cleanMac);
                    }
                }
                return formattedMac;
            }

            private int colonCount(String formattedMac) {
                return formattedMac.replaceAll("[^.]", "").length();
            }

            private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
                edRekTujuan.removeTextChangedListener(this);
                if (cleanMac.length() <= 14) {
                    edRekTujuan.setText(formattedMac);
                    edRekTujuan.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    edRekTujuan.setText(mPreviousMac);
                    edRekTujuan.setSelection(mPreviousMac.length());
                }
                edRekTujuan.addTextChangedListener(this);
            }
        });
        edRekTujuan.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    String strNPKSetor = edRekTujuan.getText().toString();
                    if ((strNPKSetor.indexOf(".") == -1) && (strNPKSetor.length() == 13)) {
                        strNPKSetor = strNPKSetor.substring(0, 3) + "." + strNPKSetor.substring(3, 5) + "." + strNPKSetor.substring(5, 11) + "." + strNPKSetor.substring(11, 13);
                        edNominal.requestFocus();
                    } else if (((strNPKSetor.indexOf(".") > -1) && (strNPKSetor.length() == 16))) {
                        strNPKSetor = strNPKSetor.substring(0, 3) + "." + strNPKSetor.substring(4, 6) + "." + strNPKSetor.substring(7, 13) + "." + strNPKSetor.substring(14, 16);
                        edNominal.requestFocus();
                    } else {
                        edRekTujuan.requestFocus();
                        edRekTujuan.setSelection(edRekTujuan.getText().length());
                        tvErrorMsg.setText("Penulisan No. Rekening salah!");
                        tvErrorMsg.setVisibility(View.VISIBLE);
                    }
                    edRekTujuan.setText(strNPKSetor);
                    return true;
                } else {
                    return false;
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(AntarRekeningToko.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    cekStrNominal();
                    tvErrorMsg.setVisibility(View.GONE);
                    strRekTujuan = edRekTujuan.getText().toString().trim();
                    if (!validateNoRek()) {
                        return;
                    }
                    if (!validateNominal()) {
                        return;
                    }
                    strBerita = edBerita.getText().toString();
                    new AsyncInqTransfer().execute();
                }
            }
        });
        btnDaftarTrf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AntarRekeningToko.this, DaftarRekening.class);
                startActivityForResult(intent, 0);
            }
        });

        /*Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        String norek = extras.getString("norek");
        if (norek != null) {
            if (norek.equals("")) {
                edRekTujuan.requestFocus();
            } else {
                edRekTujuan.setText(norek);
                edRekTujuan.setEnabled(false);
                edNominal.requestFocus();

            }

        }*/

        // tambahan baru ayik
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);


        String norek = "", nominal = "", berita = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                norek = null;
                nominal = null;
                berita = null;
            } else {
                norek = extras.getString("norek");
                nominal = extras.getString("nom");
                berita = extras.getString("berita");
            }
        } else {
            norek = (String) savedInstanceState.getSerializable("norek");
            nominal = (String) savedInstanceState.getSerializable("nom");
            berita = (String) savedInstanceState.getSerializable("berita");
        }

        if (norek == null || norek.equals("")) {
            edRekTujuan.requestFocus();
        } else {
            edRekTujuan.setText(norek);
            edNominal.setText(nominal);
            edBerita.setText(berita);

            edRekTujuan.setEnabled(false);
            edNominal.setEnabled(false);
            edBerita.setEnabled(false);
            //edNominal.requestFocus();

        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String rekening = intent.getStringExtra("REKENING");
                edRekTujuan.setText(rekening);
                edNominal.requestFocus();
            } else if (resultCode == RESULT_CANCELED) {
            }
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
        } else if (nominal < 10000) {
            edNominal.requestFocus();
            edNominal.setSelection(edNominal.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Nominal transfer minimal Rp. 10.000,-");
            showAlert("Error", "Nominal transfer minimal Rp. 10.000,-");
            return false;
//        } else if (nominal > 5000000) {
//            edNominal.requestFocus();
//            edNominal.setSelection(edNominal.getText().length());
//            tvErrorMsg.setVisibility(View.VISIBLE);
//            tvErrorMsg.setText("Nominal transfer maksimal Rp. 5.000.000,-");
//            showAlert("Error", "Nominal transfer maksimal Rp. 5.000.000,-");
//            return false;
        } else {
            tvErrorMsg.setVisibility(View.GONE);
            tvErrorMsg.setText("");
        }
        return true;
    }

    private boolean validateNoRek() {
        if (strRekTujuan.replace(".", "").matches("[0-9]*") && strRekTujuan.replace(".", "").length() == 13 && Utility.isRekening(strRekTujuan.replace(".", ""))) {
            tvErrorMsg.setVisibility(View.GONE);
            tvErrorMsg.setText("");
        } else {
            edRekTujuan.requestFocus();
            edRekTujuan.setSelection(edRekTujuan.getText().length());
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Penulisan No. Rekening salah/kurang. Hanya angka dan tanda titik.");
            showAlert("Error", "Penulisan No. Rekening salah/kurang\nHanya angka dan tanda titik.");
            return false;
        }
        return true;
    }

    private class AsyncInqTransfer extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(AntarRekeningToko.this);
        private Boolean stInqTrf = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(AntarRekeningToko.this).decrypt(getResources().getString(R.string.urlTransferinq)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), strRekTujuan, strBerita, nominal, "0", "0");
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
                stInqTrf = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                trxID = Long.toString((long) jsonObject.get("trxid"));
                strBerita = (String) jsonObject.get("berita");
                strNama = (String) jsonObject.get("nama");
                nominal = (long) jsonObject.get("nominal");
                strRekTujuan = (String) jsonObject.get("rekening");
                jwtlocal = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                stInqTrf = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stInqTrf) {
                showConfirmTransfer("KONFIRMASI TRANSFER", ": " + strRekTujuan, ": " + strNama, ": Rp. " + DoubleToCurrency(nominal) + ",-", ": " + strBerita);
            } else {
                String msga = "#" + ket + " - " + strRekTujuan + "\n";
                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
                    SysDB dbsys = new SysDB(AntarRekeningToko.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer GAGAL " + msga);
                    dbsys.close();
                }
                tvErrorMsg.setVisibility(View.VISIBLE);
                tvErrorMsg.setText(msga);
                showAlert("Transfer GAGAL", msga);
            }
        }
    }

    private String toJsonString(String nokartu, String imsi, String rekening, String berita, long nominal, String pin, String idtrx) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("rekening", rekening);
        obj.put("nominal", nominal);
        obj.put("berita", berita);
        if (!idtrx.equals("0")) obj.put("trxid", idtrx);
        if (!pin.equals("0")) obj.put("pin", pin);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            obj.put("versi", pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    private class AsyncExeTransfer extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(AntarRekeningToko.this);
        private Boolean strExeTrf = false;
        private String strTgl = "", pattern = "EEEE, dd MMMM yyyy - HH:mm:ss";
        private Date today = new Date();
        private Locale id = new Locale("in", "ID");
        private SimpleDateFormat sdf = new SimpleDateFormat(pattern, id);
        private String ket = "404 Error koneksi terputus!!\nSilahkan cek transaksi terakhir.", strFaktur = "", strRekAsal = "", strDate = "";

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
                URL obj = new URL(new NumSky(AntarRekeningToko.this).decrypt(getResources().getString(R.string.urlTransferexe)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi), strRekTujuan, strBerita, nominal, Utility.md5(strPIN), trxID);
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
                strExeTrf = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
                strBerita = (String) jsonObject.get("berita");
                strNama = (String) jsonObject.get("nama");
                nominal = (long) jsonObject.get("nominal");
                strRekTujuan = (String) jsonObject.get("rekeningtujuan");
                strFaktur = (String) jsonObject.get("fakturtujuan");
                strRekAsal = (String) jsonObject.get("rekeningasal");
                strDate = (String) jsonObject.get("datetime");
                jwtlocal = (String) jsonObject.get("jwt");
            } catch (Exception ex) {
                ex.printStackTrace();
                strExeTrf = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (strExeTrf) {
                ket = "Tanggal : " + strDate + "\n" + strFaktur + "\nDari : " + strRekAsal + "\nKe : " + strRekTujuan + "\nA.n. " + strNama + "\nRp. " + DoubleToCurrency(nominal) + ",-\nBerita : " + strBerita + "\n";
                SysDB dbsys = new SysDB(AntarRekeningToko.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, "Transfer SUKSES\n" + ket);
                dbsys.close();
                showConfirmTransferSukses("Transfer SUKSES", ": " + strDate, ": " + strFaktur, ": " + strRekAsal, ": " + strRekTujuan, ": " + strNama, ": Rp. " + DoubleToCurrency(nominal) + ",-", ": " + strBerita);
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.startsWith("404")) {
                    SysDB dbsys = new SysDB(AntarRekeningToko.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer GAGAL " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Transfer GAGAL", msga);
                } else {
                    SysDB dbsys = new SysDB(AntarRekeningToko.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Transfer TIMEOUT " + msga);
                    dbsys.close();
                    tvErrorMsg.setVisibility(View.VISIBLE);
                    tvErrorMsg.setText(msga);
                    showAlert("Transfer TIMEOUT", msga);
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

    private void showConfirmTransferSukses(String Judul, String tgls, String faktur, String darirek, String kerek, String atasnama, String nominal, String berita) {
        final Dialog dialog = new Dialog(AntarRekeningToko.this);
        Utility.playNotificationSound(AntarRekeningToko.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trfsukses);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView tgl = (TextView) dialog.findViewById(R.id.tgl);
        tgl.setText(tgls);
        TextView prods = (TextView) dialog.findViewById(R.id.faktur);
        prods.setText(faktur);
        TextView noh = (TextView) dialog.findViewById(R.id.darirek);
        noh.setText(darirek);
        TextView nom = (TextView) dialog.findViewById(R.id.kerek);
        nom.setText(kerek);
        TextView adm = (TextView) dialog.findViewById(R.id.atasnama);
        adm.setText(atasnama);
        TextView jml = (TextView) dialog.findViewById(R.id.nominal);
        jml.setText(nominal);
        TextView sns = (TextView) dialog.findViewById(R.id.berita);
        sns.setText(berita);
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
        dialog.show();
    }

    private void showConfirmTransfer(String Judul, final String rek, final String an, String jumlah, String berita) {
        final Dialog dialog = new Dialog(AntarRekeningToko.this);
        finis = false;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_confirm_trx);
        TextView textPin = (TextView) dialog.findViewById(R.id.textPin);
        textPin.setText(Judul);
        TextView reke = (TextView) dialog.findViewById(R.id.norek);
        reke.setText(rek);
        TextView ana = (TextView) dialog.findViewById(R.id.nama);
        ana.setText(an);
        TextView jml = (TextView) dialog.findViewById(R.id.jumlah);
        jml.setText(jumlah);
        TextView ber = (TextView) dialog.findViewById(R.id.berita);
        ber.setText(berita);
        SysDB dbsys = new SysDB(AntarRekeningToko.this);
        dbsys.open();
        dbsys.CreateTableTransfer();
        int jumla = 0;
        Cursor cur1 = dbsys.cekTrfData(strRekTujuan);
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();
        dbsys.close();
        final CheckBox cbsimpan = (CheckBox) dialog.findViewById(R.id.cbSimpan);
        if (jumla > 0) {
            cbsimpan.setVisibility(View.GONE);
            cbsimpan.setEnabled(false);
        }
        dialog.setCancelable(false);
        Button buttonDialogNo = (Button) dialog.findViewById(R.id.button_dialog_no);
        buttonDialogNo.setText("BATAL");
        buttonDialogNo.setTextSize(20);
        buttonDialogNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finis = true;
                dialog.cancel();
            }
        });
        Button buttonDialogYes = (Button) dialog.findViewById(R.id.button_dialog_yes);
        buttonDialogYes.setText("OK");
        buttonDialogYes.setTextSize(20);
        buttonDialogYes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (jwtlocal.equals("0"))
                    Toast.makeText(AntarRekeningToko.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    if (cbsimpan.isChecked()) {
                        try {
                            SysDB dbsys = new SysDB(AntarRekeningToko.this);
                            dbsys.open();
                            dbsys.insertTrf(strRekTujuan, strNama);
                            dbsys.close();
                        } catch (SQLiteException e) {
                            e.printStackTrace();
                        }
                    }
                    showConfirmPinTransfer(getString(R.string.pin_mobilebmt));
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private static long mLastClickTime = 0;

    private void showConfirmPinTransfer(String msg) {
        final Dialog dialog = new Dialog(AntarRekeningToko.this);
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
                    Toast.makeText(AntarRekeningToko.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                else {
                    buttonDialogYes.setEnabled(false);
                    strPIN = edPin.getText().toString().trim();
                    edPin.setText("");
                    if (Utility.is6digit(strPIN)) {
                        new AsyncExeTransfer().execute();
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
            String sDCard = Environment.getExternalStorageDirectory().getAbsolutePath();
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
            Toast.makeText(AntarRekeningToko.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
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

    // tambahan baru ayik
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
                Utility2.showAlertRelogin(AntarRekeningToko.this);
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
            AuthLogin2 task = new AuthLogin2(AntarRekeningToko.this, AntarRekeningToko.this);
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
            AuthLogin2 task = new AuthLogin2(AntarRekeningToko.this, AntarRekeningToko.this);
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
        if (receiver != null)
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
                    if (finis)
                        Utility2.showAlertRelogin(AntarRekeningToko.this);//finish();
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(AntarRekeningToko.this, AntarRekeningToko.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
