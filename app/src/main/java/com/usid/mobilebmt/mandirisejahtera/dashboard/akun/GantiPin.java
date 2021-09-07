package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;

import com.google.android.material.textfield.TextInputLayout;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
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
import com.usid.mobilebmt.mandirisejahtera.registrasi.Registrasi2Activity;
import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class GantiPin extends AppCompatActivity implements IAsyncHandler {
    private Button btnCancel, btnOK;
    private SharedPreferences config;
    private TextView tvErrorMsg;
    private String strPIN = "", strPINBaru = "", imsi = "", nocard = "";
    private EditText passLama, passBaru, passBaruLagi;
    private TextInputLayout inputLayoutpassLama, inputLayoutpassBaru, inputLayoutpassBaruLagi;
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
        setContentView(R.layout.activity_ganti_pin);
        cnd = new Ctd(GantiPin.this);
        btnOK = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btCancel);
        tvErrorMsg = (TextView) findViewById(R.id.tvRegister_errorSetting);
        inputLayoutpassLama = (TextInputLayout) findViewById(R.id.input_layout_passlama);
        inputLayoutpassBaru = (TextInputLayout) findViewById(R.id.input_layout_passbaru);
        inputLayoutpassBaruLagi = (TextInputLayout) findViewById(R.id.input_layout_passbarulagi);
        passLama = (EditText) findViewById(R.id.edPasslama);
        passBaru = (EditText) findViewById(R.id.edPassBaru);
        passBaruLagi = (EditText) findViewById(R.id.edPassBarulagi);
        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
            /*try {
                imsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            imsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            nocard = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (jwtlocal.equals("0")) {
                    Toast.makeText(GantiPin.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                } else {
                    if (!validatePassLama()) {
                        return;
                    }
                    if (!validatePassBaru()) {
                        return;
                    }
                    if (!validatePassBaruLagi()) {
                        return;
                    }
                    if (!validatePassBaruSama()) {
                        return;
                    }
                    finis = false;
                    strPINBaru = passBaru.getText().toString().trim();
                    strPIN = passLama.getText().toString().trim();
                    new AsyncGantiPIN().execute();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //tambahan baru ayik==
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);
    }

    private class AsyncGantiPIN extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(GantiPin.this);
        private Boolean stGantiPin = false;
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(GantiPin.this).decrypt(getResources().getString(R.string.urlGantiPIN2)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi), Utility.md5(strPIN), Utility.md5(strPINBaru));
                conJ.setConnectTimeout(20000);
                conJ.setReadTimeout(19000);
                conJ.setDoOutput(true);
                System.out.println("V2>>>>> " + strCek);
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
                System.out.println("V2>>>>> " + jsonObject);
                stGantiPin = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");
            } catch (Exception ex) {
                ex.printStackTrace();
                stGantiPin = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
            strTgl = sdf.format(today);
            if (stGantiPin) {
                SysDB dbsys = new SysDB(GantiPin.this);
                dbsys.open();
                dbsys.CreateTableSys();
                dbsys.insertSys(strTgl, ket);
                dbsys.close();
                showConfirm("SUKSES", ket);
            } else {
                String msga = "#" + ket + "\n";
                if (!ket.equals("404 Error koneksi terputus!!\nSilahkan coba lagi")) {
                    SysDB dbsys = new SysDB(GantiPin.this);
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.insertSys(strTgl, "Ganti PIN GAGAL " + msga);
                    dbsys.close();
                }
                tvErrorMsg.setVisibility(View.VISIBLE);
                tvErrorMsg.setText(msga);
                showAlert("Ganti PIN GAGAL", msga);
            }
        }
    }

    private String toJsonString(String nokartu, String imsi, String pin, String pinbaru) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("pin", pin);
        obj.put("pinbaru", pinbaru);
        return obj.toString();
    }

    private boolean validatePassBaruSama() {
        if (passBaruLagi.getText().toString().trim().equals(passBaru.getText().toString().trim()) && !passLama.getText().toString().trim().equals(passBaruLagi.getText().toString().trim())) {
            inputLayoutpassBaruLagi.setErrorEnabled(false);
            inputLayoutpassBaru.setErrorEnabled(false);
        } else {
            inputLayoutpassBaruLagi.setError("Masukkan kembali PIN Baru");
            inputLayoutpassBaru.setError("Masukkan PIN Baru");
            passBaru.setText("");
            passBaruLagi.setText("");
            passBaru.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("Konfirmasi PIN Baru tidak sama\n atau PIN Baru tidak boleh sama dengan PIN Lama!");
            return false;
        }
        return true;
    }

    private boolean validatePassBaruLagi() {
        if (passBaruLagi.getText().toString().trim().isEmpty()) {
            inputLayoutpassBaruLagi.setError("Masukkan kembali PIN Baru");
            passBaruLagi.setText("");
            passBaruLagi.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Baru tidak boleh kosong!");
            return false;
        } else if (!Utility.is6digit(passBaruLagi.getText().toString().trim())) {
            inputLayoutpassBaruLagi.setError("Masukkan kembali PIN Baru");
            passBaruLagi.setText("");
            passBaruLagi.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Baru harus 6 digit!");
            return false;
        } else {
            inputLayoutpassBaruLagi.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassBaru() {
        if (passBaru.getText().toString().trim().isEmpty()) {
            inputLayoutpassBaru.setError("Masukkan PIN Baru");
            passBaru.setText("");
            passBaru.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Baru tidak boleh kosong!");
            return false;
        } else if (!Utility.is6digit(passBaru.getText().toString().trim())) {
            inputLayoutpassBaru.setError("Masukkan PIN Baru");
            passBaru.setText("");
            passBaru.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Baru harus 6 digit!");
            return false;
        } else {
            inputLayoutpassBaru.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePassLama() {
        if (passLama.getText().toString().trim().isEmpty()) {
            inputLayoutpassLama.setError("Masukkan PIN Lama");
            passLama.setText("");
            passLama.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Lama tidak boleh kosong!");
            return false;
        } else if (!Utility.is6digit(passLama.getText().toString().trim())) {
            inputLayoutpassLama.setError("Masukkan PIN Lama");
            passLama.setText("");
            passLama.requestFocus();
            tvErrorMsg.setVisibility(View.VISIBLE);
            tvErrorMsg.setText("PIN Lama harus 6 digit!");
            return false;
        } else {
            inputLayoutpassLama.setErrorEnabled(false);
        }
        return true;
    }

    private void showConfirm(String title, String message) {
        new android.app.AlertDialog.Builder(this).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finis = true;
                onBackPressed();
                dialog.dismiss();
            }
        }).show();
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                passBaru.setText("");
                passBaruLagi.setText("");
                passLama.setText("");
                passLama.requestFocus();
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
                Utility2.showAlertRelogin(GantiPin.this);
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
            AuthLogin2 task = new AuthLogin2(GantiPin.this, GantiPin.this);
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
            AuthLogin2 task = new AuthLogin2(GantiPin.this, GantiPin.this);
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
                        Utility2.showAlertRelogin(GantiPin.this);
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(GantiPin.this, GantiPin.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
