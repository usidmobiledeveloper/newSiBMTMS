package com.usid.mobilebmt.mandirisejahtera.registrasi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
import com.usid.mobilebmt.mandirisejahtera.utils.DeviceType;
import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class Registrasi2Activity extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private EditText edNorek, edNotelp;
    private TextView tvErrorMsg;
    private Button btnRegistrasi;
    PackageInfo pInfo;
    private int appversi;
    private String strCd = "", versis = "", tipe_hp = "", nama = "", nokartu = "";

    private Menu statusMenu;
    private String jwtlocal;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true;
    private double lat = 0.0, lng = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi2);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        cnd = new Ctd(Registrasi2Activity.this);
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);

        tvErrorMsg = (TextView) findViewById(R.id.tv_error);
        tvErrorMsg.setVisibility(View.GONE);
        edNorek = findViewById(R.id.ed_norek);
        edNotelp = findViewById(R.id.ed_notelp);
        btnRegistrasi = findViewById(R.id.btn_registrasi);
        config = getSharedPreferences("config", 0);
        populate();
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appversi = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tipe_hp = DeviceType.getDeviceName();
        versis = config.getString("MOBILEBMTUPDATE", "0");
        SnHp telpMan = new SnHp(Registrasi2Activity.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            strCd = "KARTU TIDAK DIKENALI";
        } else {
            strCd = Utility.getIMSIRegister(Registrasi2Activity.this);

        }

        Log.d("AYIK", "reg:strcd "+ strCd);

        switch (strCd) {
            case "TIDAK ADA KARTU":
                showExt("ERROR", "Masukkan SIM CARD!");
                break;
            case "KARTU TIDAK DIKENALI":
                showExt("ERROR", "KARTU TIDAK DIKENALI!");
                break;
            case "EMULATOR":
                showExt("ERROR", "DEVICE TIDAK DIKENALI!");
                break;
            default:
                if ((Integer.parseInt(versis) > appversi)) {
                    showStatusV("Update Aplikasi", "Mohon update Aplikasi " + getString(R.string.app_name) + " Versi " + versis.substring(0, 2) + "." + versis.substring(2) + "\nBuka Play Store Sekarang?");
                }
                break;
        }

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        edNotelp.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});
        edNorek.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

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
        /*edNorek.addTextChangedListener(new TextWatcher() {
            String mPreviousMac = null;

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredMac = edNorek.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);

                int selectionStart = edNorek.getText().toString().length();
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
                edNorek.removeTextChangedListener(this);
                if (cleanMac.length() <= 14) {
                    edNorek.setText(formattedMac);
                    edNorek.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    edNorek.setText(mPreviousMac);
                    edNorek.setSelection(mPreviousMac.length());
                }
                edNorek.addTextChangedListener(this);
            }
        });*/

        edNorek.addTextChangedListener(new TextWatcher() {
            String mPreviousMac = null;

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enteredMac = edNorek.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);

                int selectionStart = edNorek.getText().toString().length();
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                int lengthDiff = formattedMac.length() - enteredMac.length();
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
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
                edNorek.removeTextChangedListener(this);
                if (cleanMac.length() <= 14) {
                    edNorek.setText(formattedMac);
                    edNorek.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    edNorek.setText(mPreviousMac);
                    edNorek.setSelection(mPreviousMac.length());
                }
                edNorek.addTextChangedListener(this);
            }
        });
        edNorek.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    String strNPKSetor = edNorek.getText().toString();
                    if ((strNPKSetor.indexOf(".") == -1) && (strNPKSetor.length() == 13)) {
                        strNPKSetor = strNPKSetor.substring(0, 3) + "." + strNPKSetor.substring(3, 5) + "." + strNPKSetor.substring(5, 11) + "." + strNPKSetor.substring(11, 13);
                        edNotelp.requestFocus();
                    } else if (((strNPKSetor.indexOf(".") > -1) && (strNPKSetor.length() == 16))) {
                        strNPKSetor = strNPKSetor.substring(0, 3) + "." + strNPKSetor.substring(4, 6) + "." + strNPKSetor.substring(7, 13) + "." + strNPKSetor.substring(14, 16);
                        edNotelp.requestFocus();
                    } else {
                        edNorek.requestFocus();
                        edNorek.setSelection(edNorek.getText().length());
                        tvErrorMsg.setText("Penulisan No. Rekening salah!");
                        tvErrorMsg.setVisibility(View.VISIBLE);
                    }
                    edNorek.setText(strNPKSetor);
                    return true;
                } else {
                    return false;
                }
            }
        });
        btnRegistrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getBaseContext(), "TEST " + strImei, Toast.LENGTH_LONG).show();
                attemptRegistrasi();
            }
        });
    }

    private void attemptRegistrasi() {
        edNorek.setError(null);
        edNotelp.setError(null);

        String norek = edNorek.getText().toString();
        String notelp = edNotelp.getText().toString();
        String error = "";

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(norek)) {
            edNorek.setError("Harus diisi");
            error = "No. Rekening Harus diisi";
            focusView = edNorek;
            cancel = true;
        }

        if (TextUtils.isEmpty(notelp)) {
            edNotelp.setError("Harus diisi");
            error = error + "No. Handphone Harus diisi";
            focusView = edNotelp;
            cancel = true;
        }

        if (cancel) {
            tvErrorMsg.setText(error);
            tvErrorMsg.setVisibility(View.VISIBLE);
            focusView.requestFocus();
        } else {
            tvErrorMsg.setVisibility(View.GONE);
            new AsyncRegistrasi().execute();
//            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(Registrasi2Activity.this, AktivasiActivity.class));
        }
    }

    private class AsyncRegistrasi extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(Registrasi2Activity.this);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
        Boolean stats = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses Registrasi");
            pdLoading.setMessage("Tunggu...sedang menghubungkan ke server");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                URL obj = new URL(MyVal.URL_BASE() + new NumSky(Registrasi2Activity.this).decrypt(getResources().getString(R.string.urlRegistrasi2)));


                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");

                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                conJ.setRequestProperty("Content-Type", "application/json");

                String strCek = toJsonString(tipe_hp, edNorek.getText().toString(), edNotelp.getText().toString(),
                        getResources().getString(R.string.kodebmt));

                Log.d("AYIK", "reg:strcek\n" + strCek);

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


                Log.d("AYIK", "reg:response" + response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stats = (Boolean) jsonObject.get("status");
                if (stats) {
                    nama = (String) jsonObject.get("nama");
                    nokartu = (String) jsonObject.get("nokartu");
                } else {
                    ket = (String) jsonObject.get("keterangan");
                }
            } catch (Exception ex) {
                stats = false;
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            if (stats) {
                tvErrorMsg.setVisibility(View.GONE);
                navigatetoValidasiActivity();
            } else {
                String msga = "#" + ket + "\n";
                tvErrorMsg.setVisibility(View.VISIBLE);
                tvErrorMsg.setText(msga);
                showAlert("GAGAL Registrasi", msga);
            }
        }
    }

    private String toJsonString(String merkhp, String norek, String nohp, String kodebmt) {
        JSONObject obj = new JSONObject();
        obj.put("merkhp", merkhp);
        obj.put("rekening", norek);
        obj.put("keyword", getString(R.string.sms_start));
        obj.put("nohp", nohp);
        obj.put("kodebmt", kodebmt);

        for (Map<String, String> map : Utility2.additionalObject(this)) {
            for (String key : map.keySet()) {
                obj.put(key, map.get(key));
            }
        }

        return obj.toString();
    }


    @Override
    public void onBackPressed() {
        showConfirmExt("Konfirmasi", "Anda yakin untuk membatalkan proses?");
    }

    private void showStatusV(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pInfo)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pInfo)));
                }
                finish();
                dialog.dismiss();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

    private void showConfirmExt(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    public void navigatetoValidasiActivity() {
        Intent validasiIntent = new Intent(getApplicationContext(), AktivasiActivity.class);
        validasiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        validasiIntent.putExtra("NOKARTU", nokartu);
        validasiIntent.putExtra("NOHP", edNotelp.getText().toString());
        validasiIntent.putExtra("NAMA", nama);
        startActivity(validasiIntent);
        finish();
    }

    private void populate() {
        if (!mayRequest()) {
            return;
        }
    }

   /* private boolean mayRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, READ_SMS, RECEIVE_SMS}, REQUEST_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, READ_SMS, RECEIVE_SMS}, REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 6 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
        }
    }*/

    private boolean mayRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (
                checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            requestPermissions(new String[]{
                            READ_PHONE_STATE,
                            WRITE_EXTERNAL_STORAGE,
                            READ_CONTACTS},
                    REQUEST_READ_PHONE_STATE);
        } else {
            requestPermissions(new String[]{
                            READ_PHONE_STATE,
                            WRITE_EXTERNAL_STORAGE,
                            READ_CONTACTS},
                    REQUEST_READ_PHONE_STATE);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
        }
    }

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
                Utility2.showAlertRelogin(Registrasi2Activity.this);
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
            AuthLogin2 task = new AuthLogin2(Registrasi2Activity.this, Registrasi2Activity.this);
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
            AuthLogin2 task = new AuthLogin2(Registrasi2Activity.this, Registrasi2Activity.this);
            task.execute();
            stats = true;
            sts = 1;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null)
            unregisterReceiver(receiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        cnd.countD.cancel();
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
                        Utility2.showAlertRelogin(Registrasi2Activity.this);
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(Registrasi2Activity.this, Registrasi2Activity.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
