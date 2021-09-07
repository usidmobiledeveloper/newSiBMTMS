//package com.usid.mobilebmt.mandirisejahtera.registrasi;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.PendingIntent;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.telephony.SmsManager;
//import android.telephony.SmsMessage;
//import android.text.InputFilter;
//import android.text.Spanned;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
//import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
//
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.security.GeneralSecurityException;
//
//import static android.Manifest.permission.READ_CONTACTS;
//import static android.Manifest.permission.READ_PHONE_STATE;
//import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//
//public class RegistrasiLamaActivity extends AppCompatActivity {
//    private SharedPreferences config;
//    private static final int REQUEST_READ_PHONE_STATE = 0;
//    private EditText edKartuAtm;
//    private TextView tvErrorMsg;
//    private String strCd = "", strNokartuatm, strNokartuatmENC, kdaktivasi, versis, pesan = "", cd = "";
//    PackageInfo pInfo;
//    private int appversi;
//    private ProgressDialog progress_dialog;
//    private Boolean isRegistered = false;
//    private Button btnOK;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_registrasi_lama);
//        tvErrorMsg = (TextView) findViewById(R.id.tvRegister_errorValidasi);
//        edKartuAtm = (EditText) findViewById(R.id.ednokartuatm);
//        btnOK = (Button) findViewById(R.id.btOkRegistrasi);
//        config = getSharedPreferences("config", 0);
//        populate();
//        try {
//            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            appversi = pInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        versis = config.getString("MOBILEBMTUPDATE", "0");
//        SnHp telpMan = new SnHp(RegistrasiLamaActivity.this);
//        if (5 != telpMan.telephonyManager().getSimState()) {
//            strCd = "TIDAK ADA KARTU";
//        } else {
//            /*try {
//                strCd = telpMan.telephonyManager().getSimSerialNumber();
//            } catch (Exception e) {
//            }*/
//            strCd = Utility.getIMSIRead(this);
//        }
//        NumSky nmsk = new NumSky(RegistrasiLamaActivity.this);
//        try {
//            cd = nmsk.encrypt(strCd);
//        } catch (GeneralSecurityException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputFilter filter = new InputFilter() {
//            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                for (int i = start; i < end; i++) {
//                    if (!Character.isDigit(source.charAt(i))) {
//                        return "";
//                    }
//                }
//                return null;
//            }
//        };
//        edKartuAtm.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(16)});
//        btnOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                strNokartuatm = edKartuAtm.getText().toString().trim();
//                strNokartuatm = strNokartuatm.replace(" ", "");
//                if (!Utility.is16digit(strNokartuatm)) {
//                    edKartuAtm.setSelection(edKartuAtm.getText().toString().length());
//                    showAlert("KESALAHAN", "Nomor registrasi kurang lengkap!");
//                    tvErrorMsg.setVisibility(View.VISIBLE);
//                    tvErrorMsg.setText("Nomor registrasi kurang lengkap!");
//                } else {
//                    NumSky nmsk = new NumSky(RegistrasiLamaActivity.this);
//                    try {
//                        strNokartuatmENC = nmsk.encrypt(strNokartuatm);
//                    } catch (GeneralSecurityException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    new AsyncRegistrasi().execute();
//                }
//            }
//        });
//        progress_dialog = new ProgressDialog(this);
//        progress_dialog.setCancelable(false);
//        progress_dialog.setIndeterminate(false);
//        progress_dialog.setTitle("Memproses");
//        progress_dialog.setMessage("Mohon tunggu...sedang memproses aktivasi");
//        if (strCd.equals("TIDAK ADA KARTU")) {
//            showExt("ERROR", "Masukkan SIM CARD!");
//        } else {
//            if ((Integer.parseInt(versis) > appversi)) {
//                showStatusV("Update Aplikasi", "Mohon update Aplikasi " + getString(R.string.app_name) + " Versi " + versis.substring(0, 1) + "." + versis.substring(1) + "\nBuka Play Store Sekarang?");
//            } else {
//                new AsyncPreRegistrasi().execute();
//            }
//        }
//    }
//
//    private class AsyncPreRegistrasi extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(RegistrasiLamaActivity.this);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan Cek koneksi anda", nokartu = "";
//        Boolean stats = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pdLoading.setCancelable(false);
//            pdLoading.setIndeterminate(false);
//            pdLoading.setTitle("Memproses Verifikasi");
//            pdLoading.setMessage("Tunggu...sedang menghubungkan ke server");
//            pdLoading.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                URL obj=null;
//                //URL obj = new URL(new NumSky(RegistrasiLamaActivity.this).decrypt(getResources().getString(R.string.urlCekRegistrasi)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                String strCek = toJsonString("000", Utility.md5(strCd), getResources().getString(R.string.kodebmt));
//                conJ.setConnectTimeout(20000);
//                conJ.setReadTimeout(19000);
//                conJ.setDoOutput(true);
//                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
//                wr.writeBytes(strCek);
//                wr.flush();
//                wr.close();
//                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine).append("\n");
//                }
//                in.close();
//                JSONParser parser = new JSONParser();
//                Object objects = parser.parse(response.toString());
//                JSONObject jsonObject = (JSONObject) objects;
//                stats = (Boolean) jsonObject.get("status");
//                ket = (String) jsonObject.get("keterangan");
//                nokartu = (String) jsonObject.get("nokartu");
//                kdaktivasi = (String) jsonObject.get("kodeaktivasi");
//            } catch (Exception ex) {
//                stats = false;
//                ex.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            if (stats) {
//                NumSky nmsk = new NumSky(RegistrasiLamaActivity.this);
//                try {
//                    strNokartuatmENC = nmsk.encrypt(nokartu);
//                } catch (GeneralSecurityException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                progress_dialog.show();
//                Handler handler = new Handler();
//                tvErrorMsg.setVisibility(View.GONE);
//                sendSMS(getResources().getString(R.string.noSMSGateway), getResources().getString(R.string.smsAktivasi) + " " + kdaktivasi);
//                IntentFilter mIntentFilter = new IntentFilter();
//                mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//                mIntentFilter.setPriority(Integer.MAX_VALUE);
//                registerReceiver(mSMSReceiver, mIntentFilter);
//                isRegistered = true;
//                btnOK.setEnabled(false);
//                Runnable runnableCode = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (progress_dialog.isShowing()) progressHandler.sendEmptyMessage(1);
//                    }
//                };
//                handler.postDelayed(runnableCode, 60000);
//            } else {
//                Toast.makeText(getBaseContext(), "SILAHKAN MEMASUKKAN NO REGISTRASI UNTUK MELAKUKAN REGISTRASI AWAL", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    private class AsyncRegistrasi extends AsyncTask<Void, Void, Void> {
//        ProgressDialog pdLoading = new ProgressDialog(RegistrasiLamaActivity.this);
//        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";
//        Boolean stats = false;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pdLoading.setCancelable(false);
//            pdLoading.setIndeterminate(false);
//            pdLoading.setTitle("Memproses Aktivasi");
//            pdLoading.setMessage("Tunggu...sedang menghubungkan ke server");
//            pdLoading.show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                URL obj=null;
//               // URL obj = new URL(new NumSky(RegistrasiLamaActivity.this).decrypt(getResources().getString(R.string.urlRegistrasi)));
//                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
//                conJ.setRequestMethod("POST");
//                conJ.setRequestProperty("Content-Type", "application/json");
//                String strCek = toJsonString(Utility.md5(strNokartuatm), (strCd), getResources().getString(R.string.kodebmt));
//                conJ.setConnectTimeout(20000);
//                conJ.setReadTimeout(19000);
//                conJ.setDoOutput(true);
//                DataOutputStream wr = new DataOutputStream(conJ.getOutputStream());
//                wr.writeBytes(strCek);
//                wr.flush();
//                wr.close();
//                ket = conJ.getResponseCode() + " " + conJ.getResponseMessage();
//                BufferedReader in = new BufferedReader(new InputStreamReader(conJ.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine).append("\n");
//                }
//                in.close();
//                JSONParser parser = new JSONParser();
//                Object objects = parser.parse(response.toString());
//                JSONObject jsonObject = (JSONObject) objects;
//                stats = (Boolean) jsonObject.get("status");
//                kdaktivasi = (String) jsonObject.get("kodeaktivasi");
//                ket = (String) jsonObject.get("keterangan");
//            } catch (Exception ex) {
//                stats = false;
//                ex.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//            if (pdLoading.isShowing()) pdLoading.dismiss();
//            if (stats) {
//                progress_dialog.show();
//                Handler handler = new Handler();
//                tvErrorMsg.setVisibility(View.GONE);
//                sendSMS(getResources().getString(R.string.noSMSGateway), getResources().getString(R.string.smsAktivasi) + " " + kdaktivasi);
//                IntentFilter mIntentFilter = new IntentFilter();
//                mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
//                mIntentFilter.setPriority(Integer.MAX_VALUE);
//                registerReceiver(mSMSReceiver, mIntentFilter);
//                isRegistered = true;
//                btnOK.setEnabled(false);
//                Runnable runnableCode = new Runnable() {
//                    @Override
//                    public void run() {
//                        if (progress_dialog.isShowing()) progressHandler.sendEmptyMessage(1);
//                    }
//                };
//                handler.postDelayed(runnableCode, 60000);
//            } else {
//                String msga = "#" + ket + "\n";
//                tvErrorMsg.setVisibility(View.VISIBLE);
//                tvErrorMsg.setText(msga);
//                showAlert("GAGAL Registrasi", msga);
//            }
//        }
//    }
//
//    private String toJsonString(String nokartu, String imsi, String kodebmt) {
//        JSONObject obj = new JSONObject();
//        if (nokartu != "000") obj.put("nokartu", nokartu);
//        obj.put("kodebmt", kodebmt);
//        obj.put("imsi", imsi);
//        return obj.toString();
//    }
//
//    private Handler progressHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
//                    SharedPreferences.Editor editor = config.edit();
//                    editor.putString("METAREG", cd);
//                    editor.putString("3D0k", strNokartuatmENC);
//                    editor.commit();
//                    tvErrorMsg.setVisibility(View.GONE);
//                    Toast.makeText(getBaseContext(), "REGISTRASI SUKSES", Toast.LENGTH_SHORT).show();
//                    showStatus("SUKSES REGISTRASI", pesan);
//                    break;
//                case 1:
//                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
//                    tvErrorMsg.setVisibility(View.VISIBLE);
//                    tvErrorMsg.setText("Tidak ada respon dari server.\nNomor telepon anda tidak terdaftar!\nSilahkan menghubungi CallCenter kami" + getString(R.string.call_center) + "!");
//                    showAlert("GAGAL Registrasi", "Tidak ada respon dari server.\nNomor telepon anda tidak terdaftar!\nSilahkan menghubungi CallCenter kami" + getString(R.string.call_center) + "!");
//                    break;
//            }
//        }
//    };
//
//    private BroadcastReceiver mSMSReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//                    Bundle bundle = intent.getExtras();
//                    if (bundle != null) {
//                        Object[] pdus = (Object[]) bundle.get("pdus");
//                        String sms;
//                        for (Object pdu : pdus) {
//                            SmsMessage tmp;
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                tmp = SmsMessage.createFromPdu((byte[]) pdu, bundle.getString("format"));
//                            } else {
//                                tmp = SmsMessage.createFromPdu((byte[]) pdu);
//                            }
//                            String senderMobile = tmp.getMessageBody();
//                            sms = tmp.getMessageBody();
//                            if (senderMobile.startsWith(getString(R.string.sms_start)) && tmp.getDisplayOriginatingAddress().contains("+628111010088")) {
//                                final String finalSms = sms;
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        pesan = finalSms;
//                                        progressHandler.sendEmptyMessage(0);
//                                    }
//                                });
//                                abortBroadcast();
//                                break;
//                            }
//                        }
//                    }
////                } else if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
////                    String smsSender = "";
////                    String smsBody = "";
////                    for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
////                        smsBody += smsMessage.getMessageBody();
////                    }
////
////                    if (smsBody.startsWith("")) {
////                        Log.d(TAG, "Sms with condition detected");
////                        Toast.makeText(context, "BroadcastReceiver caught conditional SMS: " + smsBody, Toast.LENGTH_LONG).show();
////                    }
////                    Log.d(TAG, "SMS detected: From " + smsSender + " With text " + smsBody);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//
//    //    private static final String TAG = "SmsBroadcastReceiver";
//    private void sendSMS(String phoneNumber, String message) {
//        String SENT = "SMS_SENT";
//        String DELIVERED = "SMS_DELIVERED";
//
//        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
//
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(getBaseContext(), "SMS AKTIVASI Terkirim...", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (isRegistered) unregisterReceiver(mSMSReceiver);
//    }
//
//    @Override
//    public void onBackPressed() {
//        showConfirmExt("Konfirmasi", "Anda yakin untuk membatalkan proses?");
//    }
//
//    private void showExt(String title, String message) {
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void showStatusV(String title, String message) {
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pInfo)));
//                } catch (android.content.ActivityNotFoundException anfe) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + pInfo)));
//                }
//                finish();
//                dialog.dismiss();
//            }
//        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void showConfirmExt(String title, String message) {
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//                dialog.dismiss();
//            }
//        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void showAlert(String title, String message) {
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    private void showStatus(String title, String message) {
//        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                navigatetoValidasiActivity();
//                dialog.dismiss();
//            }
//        }).show();
//    }
//
//    public void navigatetoValidasiActivity() {
//        Intent validasiIntent = new Intent(getApplicationContext(), UnusedMainLamaActivity.class);
//        validasiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(validasiIntent);
//        finish();
//    }
//
//    private void populate() {
//        if (!mayRequest()) {
//            return;
//        }
//    }
//
//   /* private boolean mayRequest() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
//            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, READ_SMS, RECEIVE_SMS}, REQUEST_READ_PHONE_STATE);
//        } else {
//            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS, READ_SMS, RECEIVE_SMS}, REQUEST_READ_PHONE_STATE);
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_PHONE_STATE) {
//            if (grantResults.length == 6 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                finish();
//            }
//        }
//    }*/
//
//    private boolean mayRequest() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (
//                checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
//                        checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                        checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
//            requestPermissions(new String[]{
//                            READ_PHONE_STATE,
//                            WRITE_EXTERNAL_STORAGE,
//                            READ_CONTACTS},
//                    REQUEST_READ_PHONE_STATE);
//        } else {
//            requestPermissions(new String[]{
//                            READ_PHONE_STATE,
//                            WRITE_EXTERNAL_STORAGE,
//                            READ_CONTACTS},
//                    REQUEST_READ_PHONE_STATE);
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_PHONE_STATE) {
//            if (grantResults.length == 3 &&
//                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
//                    grantResults[2] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                finish();
//            }
//        }
//    }
//}
