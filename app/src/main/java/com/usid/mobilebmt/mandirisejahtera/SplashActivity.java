package com.usid.mobilebmt.mandirisejahtera;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.usid.mobilebmt.mandirisejahtera.model.User;
import com.usid.mobilebmt.mandirisejahtera.registrasi.BaruPin;
import com.usid.mobilebmt.mandirisejahtera.registrasi.RegistrasiViewModel;
import com.usid.mobilebmt.mandirisejahtera.repository.Resource;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.usid.mobilebmt.mandirisejahtera.utils.MyVal.MYTAG;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.setPrefsAuthToken;

public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private final Handler handler = new Handler();
    private View img;
    private SharedPreferences config;
    PackageInfo pInfo;
    private TextView tvVer, textView;
    private String imgsplash = "";

    RegistrasiViewModel viewModel;

    private final Runnable startActivityRunnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            //Log.d("AYII", Utility2.getISO8601());
            /*@SuppressLint("HardwareIds") String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d("AYIK~", "id " + id);
            if (id.equals("e41d1095ff0fb60f")) {
                startActivity(new Intent(SplashActivity.this, InfoActivity.class));
                finish();
                return;
            }*/
            doLogin();
        }
    };

    //NU-Kas

    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            dummyTLS();

        img = findViewById(R.id.imgSplash);
        config = getSharedPreferences("config", 0);
        imgsplash = config.getString("IMAGE", "");

        populate();
        tvVer = (TextView) findViewById(R.id.textViewversi);
        textView = (TextView) findViewById(R.id.textView);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVer.setText("Versi " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnimationSet set = new AnimationSet(true);

        Animation fadeIn = FadeIn(2000);
        fadeIn.setStartOffset(0);
        set.addAnimation(fadeIn);

        Animation fadeOut = FadeOut(1000);
        fadeOut.setStartOffset(1000);
        set.addAnimation(fadeOut);

        img.startAnimation(set);
        tvVer.setAnimation(set);
        textView.setAnimation(set);
        handler.postDelayed(startActivityRunnable, 3000);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(startActivityRunnable);
    }

    private Animation FadeIn(int t) {
        Animation fade;
        fade = new AlphaAnimation(0.0f, 1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }

    private Animation FadeOut(int t) {
        Animation fade;
        fade = new AlphaAnimation(1.0f, 1.0f);
        fade.setDuration(t);
        fade.setInterpolator(new AccelerateInterpolator());
        return fade;
    }

    private class AsyncCekVersion extends AsyncTask<Void, Void, Void> {
        private int appversi_now;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String uri = MyVal.URL_BASE() + new NumSky(SplashActivity.this).decrypt(getResources().getString(R.string.urlAppVersionCek)) + getResources().getString(R.string.appurlnameUpper)
                        + "/" + BuildConfig.VERSION_CODE;
                URL obj = new URL(uri);


                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("Authorization", getPrefsAuthToken());
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(3000);

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();

                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                //long version = (long) jsonObject.get("version");
                String ket = (String) jsonObject.get("keterangan");
                Boolean status = (Boolean) jsonObject.get("status");
                if (status) {

                    appversi_now = BuildConfig.VERSION_CODE;

                    SharedPreferences.Editor editor = config.edit();
                    editor.putString("MOBILEBMTUPDATE", Integer.toString(appversi_now));
                    if (!imgsplash.equals(ket)) editor.putString("IMAGE", ket);
                    editor.apply();

                    Intent transaksiIntent = new Intent(getApplicationContext(), ControllerActivity.class);
                    transaksiIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(transaksiIntent);
                    finish();

                } else {
                    showAlertAppVersion("ERROR", "Aplikasi versi lama. Silahkan update aplikasi melalui Play Store");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
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
    }*/

    private boolean mayRequest() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (
                checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        requestPermissions(new String[]{
                        READ_PHONE_STATE,
                        WRITE_EXTERNAL_STORAGE,
                        CAMERA,
                        READ_CONTACTS},
                REQUEST_READ_PHONE_STATE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PHONE_STATE) {
            if (grantResults.length == 4 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED) {
            } else {
                finish();
            }
        }
    }

    private void doLogin() {
        viewModel = new RegistrasiViewModel();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        String datetime = sdf.format(new Date());

        viewModel.getLogin(datetime).observe(SplashActivity.this,
                new Observer<Resource<User>>() {
                    @Override
                    public void onChanged(Resource<User> resource) {
                        switch (resource.status) {
                            case SUCCESS:
                                assert resource.data != null;
                                String token = resource.data.getToken();

                                setPrefsAuthToken(token);

                                new AsyncCekVersion().execute();

                                break;

                            case ERROR:
                                String message = resource.message;
                                showAlert("ERROR", message);

                                break;
                            default:
                                break;
                        }

                    }
                });
    }

    private void dummyTLS() {

        try {

            ProviderInstaller.installIfNeeded(getApplicationContext());

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);

            SSLEngine engine = sslContext.createSSLEngine();

            String[] supportedProtocols = engine.getSupportedProtocols();
            /*for (String protocol : supportedProtocols) {

            }*/

            engine.setEnabledProtocols(supportedProtocols);

        } catch (KeyManagementException e) {
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();

        }


    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(title).setMessage(message).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }


    public void showAlertAppVersion(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                }
                finish();

            }
        });
        /*builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });*/

        AlertDialog dialog = builder.create();
        dialog.show();


    }
}