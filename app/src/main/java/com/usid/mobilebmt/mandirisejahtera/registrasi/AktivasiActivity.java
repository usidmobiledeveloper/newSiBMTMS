package com.usid.mobilebmt.mandirisejahtera.registrasi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.chaos.view.PinView;
import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class AktivasiActivity extends AppCompatActivity implements IAsyncHandler {
    private SharedPreferences config;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private TextView timerValue, tvNoHP, tvErrorMsg, timerA, timerB;
    private PinView pv_kdaktivasi;
    private Button btnAktifasi;
    private Bundle extras;
    private static CountDownTimer countDownTimer;
    private String nohp = "", nama = "", nokartu = "", strCd = "", cd = "", kdaktivasi = "";
    private Boolean isRegistered = false;
    private ProgressDialog progress_dialog;

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
        setContentView(R.layout.activity_aktivasi);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        cnd = new Ctd(AktivasiActivity.this);
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);

        timerValue = (TextView) findViewById(R.id.tv_timer);
        timerA = (TextView) findViewById(R.id.tv_timera);
        timerB = (TextView) findViewById(R.id.tv_timerb);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error);
        pv_kdaktivasi = (PinView) findViewById(R.id.firstPinView);
        btnAktifasi = findViewById(R.id.btn_registrasi);
        tvNoHP = (TextView) findViewById(R.id.nohp);
        config = getSharedPreferences("config", 0);
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                nohp = "";
                nama = "";
                nokartu = "";
            } else {
                nohp = extras.getString("NOHP");
                nama = extras.getString("NAMA");
                nokartu = extras.getString("NOKARTU");
            }
        } else {
            nohp = (String) savedInstanceState.getSerializable("NOHP");
            nama = (String) savedInstanceState.getSerializable("NAMA");
            nokartu = (String) savedInstanceState.getSerializable("NOKARTU");
        }

        Log.d("AYIK", "aktv:nohp " + nohp);
        Log.d("AYIK", "aktv:nama " + nama);
        Log.d("AYIK", "aktv:nokartu " + nokartu);

        populate();
        tvNoHP.setText(nohp);
        tvErrorMsg.setVisibility(View.GONE);
        startCountdown();
        SnHp telpMan = new SnHp(AktivasiActivity.this);

        if (5 != telpMan.telephonyManager().getSimState()) {
            strCd = "TIDAK ADA KARTU";
        } else {
            strCd = Utility.getIMSIRegister(this);
        }
        NumSky nmsk = new NumSky(AktivasiActivity.this);
        try {
            cd = nmsk.encrypt(strCd);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminate(false);
        progress_dialog.setTitle(getString(R.string.memproses));
        progress_dialog.setMessage(getString(R.string.proses_aktivasi_));*/

        if (strCd.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", getString(R.string.maasukkan_simcard));
        } else {
            //progress_dialog.show();
            Handler handler = new Handler();
            tvErrorMsg.setVisibility(View.GONE);
            /*IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            mIntentFilter.setPriority(Integer.MAX_VALUE);
            registerReceiver(mSMSReceiver, mIntentFilter);*/
            isRegistered = true;
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    //if (progress_dialog.isShowing()) progressHandler.sendEmptyMessage(1);
                }
            };
            handler.postDelayed(runnableCode, 0);
        }
        btnAktifasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pv_kdaktivasi.getText().toString().equals("")) {
                    Toast.makeText(AktivasiActivity.this, "Kode Aktivasi tidak boleh kosong", Toast.LENGTH_SHORT).show();
                } else {
                    tvErrorMsg.setText("");
                    tvErrorMsg.setVisibility(View.GONE);
                    new AsyncRegistrasi().execute();
                }

            }
        });
    }


   /* //ayik 2019-01-07
    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
                    tvErrorMsg.setVisibility(View.GONE);
                    pv_kdaktivasi.setText(kdaktivasi);
                    new AsyncRegistrasi().execute();
                    break;
                case 1:
                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
                    Toast.makeText(getBaseContext(), getString(R.string.masukkan_kode_aktivasi), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };*/

    private class AsyncRegistrasi extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(AktivasiActivity.this);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
        Boolean stats = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            kdaktivasi = pv_kdaktivasi.getText().toString();

            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle(getString(R.string.proses_aktivasi));
            pdLoading.setMessage(getString(R.string.menghubung_server));
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(AktivasiActivity.this).decrypt(getResources().getString(R.string.urlAktivasi2)));

                //  URL obj = new URL(new NumSky(AktivasiActivity.this).decrypt(getResources().getString(R.string.urlAktivasi2)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                conJ.setRequestProperty("Content-Type", "application/json");

                String strCek = toJsonString(kdaktivasi, nokartu, strCd, getResources().getString(R.string.kodebmt));
                Log.d("AYIK", "aktivs:strcek\n" + strCek);

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

                Log.d("AYIK", "aktv:response " + response.toString());

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                stats = (Boolean) jsonObject.get("status");
                if (stats) {

                    ket = (String) jsonObject.get("keterangan");

                    if (!ket.contains("Verifikasi Aktivasi")) {
                        nokartu = (String) jsonObject.get("nokartu");
                        NumSky nmsk = new NumSky(AktivasiActivity.this);
                        try {
                            nokartu = nmsk.encrypt(nokartu);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

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
                if (ket.contains("Verifikasi Aktivasi")) {
                    tvErrorMsg.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), getString(R.string.verifikasi_pin), Toast.LENGTH_SHORT).show();
                    navigatetoBaruPIN();
                } else {
                    SharedPreferences.Editor editor = config.edit();
                    editor.putString("METAREG", cd);
                    editor.putString("3D0k", nokartu);
                    editor.putString("NAMA", nama);
                    editor.putString("NOHP", nohp);
                    editor.commit();

                    tvErrorMsg.setVisibility(View.GONE);
                    Toast.makeText(getBaseContext(), getString(R.string.aktivasi_sukses), Toast.LENGTH_SHORT).show();
                    showStatusMain(getString(R.string.aktivasi_sukses), ket);
                }

            } else {
                tvErrorMsg.setVisibility(View.VISIBLE);
                tvErrorMsg.setText(ket);
                //pv_kdaktivasi.setText("");
            }
        }
    }

    private String toJsonString(String kdaktivasi, String nokartu, String imsi, String kodebmt) {
        JSONObject obj = new JSONObject();
        obj.put("kodeaktivasi", kdaktivasi);
        obj.put("nokartu", nokartu);
        //obj.put("keyword", getString(R.string.sms_start));
        obj.put("imsi", imsi);
        obj.put("kodebmt", kodebmt);

        for (Map<String, String> map : Utility2.additionalObject(this)) {
            for (String key : map.keySet()) {
                obj.put(key, map.get(key));
            }
        }


        return obj.toString();
    }

    private void startCountdown() {
        if (countDownTimer == null) {
            String getMinutes = "10";
            int noOfMinutes = Integer.parseInt(getMinutes) * 60 * 1000;
            startTimer(noOfMinutes);//start countdown
//            Toast.makeText(AktivasiActivity.this, "Please enter no. of Minutes.", Toast.LENGTH_SHORT).show();//Display toast if edittext is empty
        }
    }

    private void startTimer(int noOfMinutes) {
        countDownTimer = new CountDownTimer(noOfMinutes, 1000) {
            public void onTick(long millisUntilFinished) {
                long millis = millisUntilFinished;
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                timerValue.setText(hms);//set text
            }

            public void onFinish() {
                timerA.setVisibility(View.GONE);
                timerB.setVisibility(View.GONE);
                timerValue.setText(getString(R.string.mual_ulang_registrasi));
                countDownTimer = null;//set CountDownTimer to null
            }
        }.start();
    }

    public void navigatetoMain() {
        Intent validasiIntent = new Intent(getApplicationContext(), NewMainActivity.class);
        validasiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(validasiIntent);
        finish();
    }

    public void navigatetoBaruPIN() {
        Intent validasiIntent = new Intent(getApplicationContext(), BaruPin.class);
        validasiIntent.putExtra("NOKARTU", nokartu);
        validasiIntent.putExtra("NOHP", nohp);
        validasiIntent.putExtra("NAMA", nama);

        startActivity(validasiIntent);
        finish();
    }


    @Override
    public void onBackPressed() {
        showConfirmExt(getString(R.string.konfirmasi), getString(R.string.batalkan_proses));
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

    private void showStatusMain(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                navigatetoMain();
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

    private void populate() {
        if (!mayRequest()) {
            return;
        }
    }

    /*private boolean mayRequest() {
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
                Utility2.showAlertRelogin(AktivasiActivity.this);
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
            AuthLogin2 task = new AuthLogin2(AktivasiActivity.this, AktivasiActivity.this);
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
            AuthLogin2 task = new AuthLogin2(AktivasiActivity.this, AktivasiActivity.this);
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
                        Utility2.showAlertRelogin(AktivasiActivity.this);
                    else {
//                        Toast.makeText(GantiPin.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(AktivasiActivity.this, AktivasiActivity.this);
                        task.execute();
                    }
                }
            }
        };
    }
}
