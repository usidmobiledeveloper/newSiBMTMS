package com.usid.mobilebmt.mandirisejahtera;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.soundcloud.android.crop.Crop;
import com.usid.mobilebmt.mandirisejahtera.dashboard.RiwayatFragment;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekeningToko;
import com.usid.mobilebmt.mandirisejahtera.dashboard.scan.ScanActivity;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.AntarRekening;
import com.usid.mobilebmt.mandirisejahtera.dashboard.BerandaFragment;
import com.usid.mobilebmt.mandirisejahtera.dashboard.AkunFragment;
import com.usid.mobilebmt.mandirisejahtera.dashboard.TransaksiFragment;
import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationUtils;
import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationsInfoActivity;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class NewMainActivity extends AppCompatActivity implements IAsyncHandler {

    private Toolbar toolbar;

    public static NewMainActivity main;

    public CardView layoutProfile;

    private ImageView iv_status;
    boolean doubleBackToExitPressedOnce = false;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private SharedPreferences config;
    PackageInfo pInfo;
    private int appversi;
    private String imsi = "";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String jwtpub;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver2 receiver;
    public static boolean finisa = true;
    private String dirPath = "";
    private ImageView resultView;
    private TextView tvnama, tvnohp;
    private Uri fileUri;
    private String fileName = fileName = "fp_mobilebmt.jpg";
    static final int REQUEST_IMAGE_CAPTURE = 8888;
    private ProgressDialog progress_dialog;

    private boolean cam = false;
    ViewPager viewPagerX;

    String nocard = "";
    TextView tvVA, tvTapVA;
    ProgressBar pbarVA;
    ImageButton btnSalinVA;
    LinearLayout llVA;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    fragment = new BerandaFragment();
                    layoutProfile.setVisibility(View.GONE);

                    break;
                case R.id.navigation_transaksi:
                    fragment = new TransaksiFragment();
                    layoutProfile.setVisibility(View.GONE);

                    break;
                case R.id.navigation_scan:
                    if (jwtpub.equals("0"))
                        Toast.makeText(NewMainActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                    else {
                        new IntentIntegrator(NewMainActivity.this).setCaptureActivity(ScanActivity.class).initiateScan();
                    }

                    break;
                case R.id.navigation_riwayat:
                    fragment = new RiwayatFragment();
                    layoutProfile.setVisibility(View.GONE);
                    break;

                case R.id.navigation_account:
                    fragment = new AkunFragment();
                    layoutProfile.setVisibility(View.GONE);

                    break;
            }
            return loadFragment(fragment);
        }
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            dummyTLS();

        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + getString(R.string.path2) + "data";

        layoutProfile = findViewById(R.id.layout_profile);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        loadFragment(new BerandaFragment());
        layoutProfile.setVisibility(View.GONE);

        tvnama = findViewById(R.id.fakun_nama);
        tvnohp = findViewById(R.id.fakun_telp);
        config = getSharedPreferences("config", 0);
        tvnama.setText(config.getString("NAMA", ""));
        tvnohp.setText(config.getString("NOHP", ""));
        iv_status = toolbar.findViewById(R.id.img_indikator);
        resultView = findViewById(R.id.img_profile);

        progress_dialog = new ProgressDialog(this);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminate(false);
        progress_dialog.setMessage("Memproses Cropping...");
        populate();
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appversi = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SnHp telpMan = new SnHp(this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = getString(R.string.tidak_ada_kartu);
        } else {

            imsi = Utility.getIMSIRead(this);
        }
        String versis = config.getString("MOBILEBMTUPDATE", "0");
        if ((Integer.parseInt(versis) > appversi)) {
            showStatusV(getString(R.string.update_aplikasi), getString(R.string.mohon_update_aplikasi) + versis.substring(0, 1) + "." + versis.substring(1) + "\nBuka Play Store Sekarang?");
        } else if (imsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        }
        regFirebase();
        cnd = new Ctd(NewMainActivity.this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver2();
        registerReceiver(receiver, filter);

        if (!stats) {
            cnd.countD.cancel();
            jwtpub = "0";
            AuthLogin2 task = new AuthLogin2(NewMainActivity.this, NewMainActivity.this);
            task.execute();
            stats = true;
            sts = 1;
        }

        resultView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewMainActivity.this);
                alertDialogBuilder.setTitle(R.string.foto_akun);
                final String[] items = {getString(R.string.ambil_foro), getString(R.string.galeri)};
                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String action = items[i];
                        if (action.equalsIgnoreCase("ambil foto")) {
                            camera();
                        } else {
                            Crop.pickImage(NewMainActivity.this);
                        }
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        //ayik baru
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                //Toast.makeText(this, "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
            }
        }

        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
            imsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(NewMainActivity.this);
        try {
            nocard = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        tvVA = findViewById(R.id.fakun_va);
        llVA = findViewById(R.id.ll_va);
        tvTapVA = findViewById(R.id.fakun_tapviewva);
        pbarVA = findViewById(R.id.fakun_pbar);
        btnSalinVA = findViewById(R.id.fakun_btnsalinva);

        tvTapVA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncVA().execute();
            }
        });

        btnSalinVA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vanama = tvnama.getText().toString();
                String vanomor = tvVA.getText().toString();

                ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Salin token", "a.n. " + vanama + "\n" + vanomor);
                assert manager != null;
                manager.setPrimaryClip(clipData);

                Toast.makeText(NewMainActivity.this, "Salin VA", Toast.LENGTH_SHORT).show();

            }
        });

        main = this;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_notif:
//                Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(NewMainActivity.this, NotificationsInfoActivity.class));
                return true;
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(NewMainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFrag(Fragment fragment, String title) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

    public class NetworkChangeReceiver2 extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (Utility.isNetworkAvailable(context)) ;
            onResume();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate("main", 0);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                finish();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_dua_kali), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        }
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

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
        //TAMBAHAN AYIK=============================================================================
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
        //==========================================================================================
        // tambahan baru ayik==
        File file = new File(dirPath, ".nomedia");
        if (!file.exists()) {
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
            File files = new File(dirPath, ".nomedia");
            try {
                FileOutputStream fOut = new FileOutputStream(files);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File imgFile = new File(dirPath + "/" + fileName);
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
            resultView.setImageBitmap(myBitmap);
        }


        if (stats && sts > 1) {
            cnd.countD.cancel();
            jwtpub = "0";
            iv_status.setImageResource(R.drawable.ic_action_status_yellow);
            AuthLogin2 task = new AuthLogin2(NewMainActivity.this, NewMainActivity.this);
            task.execute();
        }
    }

    private void populate() {
        if (!mayRequest()) {
            return;
        }
    }


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

    private void regFirebase() {
        //TAMBAHAN AYIK=============================================================================
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                }
                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
                    if (finisa)
//                        finish();
                        Utility2.showAlertRelogin(NewMainActivity.this);
                    else {
                        cnd.countD.cancel();
                        jwtpub = "0";
                        iv_status.setImageResource(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(NewMainActivity.this, NewMainActivity.this);
                        task.execute();
                    }
                }
            }
        };
        displayFirebaseRegId();
        //==========================================================================================
    }

    //TAMBAHAN AYIK=================================================================================
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
    }

    @Override
    protected void onPause() {
        // TAMBAHAN AYIK============================================================================
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //==========================================================================================
        // tambahan baru ayik==
        cnd.countD.cancel();
    }

    //tambahan baru ayik==
    @Override
    public void onPostExec(Boolean status, String jwt) {
        if (status) {
            iv_status.setImageResource(R.drawable.ic_action_status_blue);
            jwtpub = jwt;
            cnd.countD.start();
        } else {
            iv_status.setImageResource(R.drawable.ic_action_status_red);
            jwtpub = jwt;

            Log.d("AYIK", "jwt " + jwtpub);

            if (jwtpub.equals("401")) {
                Utility2.showAlertRelogin(NewMainActivity.this);
            }
        }

        sts = sts + 1;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            new ResultPhoto().execute();
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        } else {
            IntentResult iResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, result);
            if (iResult != null) {
                if (iResult.getContents() == null) {
                    Toast.makeText(NewMainActivity.this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                } else {
                    IntentResult iResult2 = IntentIntegrator.parseActivityResult(requestCode, resultCode, result);
                    if (iResult2 != null) {
                        if (iResult2.getContents() == null) {
                            Toast.makeText(NewMainActivity.this, "Scan dibatalkan", Toast.LENGTH_SHORT).show();
                        } else {
                            try {

                              /*  String norek = new NumSky(NewMainActivity.this).decrypt(iResult2.getContents());
                                if (norek.length() == 13) {
                                    if (jwtpub.equals("0"))
                                        Toast.makeText(NewMainActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                                    else {
                                        Intent intent = new Intent(NewMainActivity.this, AntarRekening.class);
                                        intent.putExtra("norek", norek);
                                        startActivity(intent);
                                    }
                                    if (!cam) {
                                        cam = true;
                                    }
                                } else {
                                    Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                }*/

                                String datas = new NumSky(NewMainActivity.this).decrypt(iResult2.getContents());
                                String norek = "", nom = "", berita = "";

                                if (datas.contains("#")) {
                                    String[] arrdatas = datas.split("#");
                                    norek = arrdatas[0].replace(".", "");
                                    nom = arrdatas[1];
                                    berita = arrdatas[2];

                                    if (norek.length() == 13) {
                                        if (jwtpub.equals("0"))
                                            Toast.makeText(NewMainActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                                        else {
                                            Intent intent = new Intent(NewMainActivity.this, AntarRekeningToko.class);
                                            intent.putExtra("norek", norek);
                                            intent.putExtra("nom", nom);
                                            intent.putExtra("berita", berita);
                                            startActivity(intent);
                                        }
                                        if (!cam) {
                                            cam = true;
                                        }
                                    } else {
                                        Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                        //reloadFragment();
                                    }

                                } else {
                                    norek = datas.replace(".", "");
                                    if (norek.length() == 13) {
                                        if (jwtpub.equals("0"))
                                            Toast.makeText(NewMainActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                                        else {
                                            Intent intent = new Intent(NewMainActivity.this, AntarRekening.class);
                                            intent.putExtra("norek", norek);
                                            startActivity(intent);
                                        }
                                        if (!cam) {
                                            cam = true;
                                        }
                                    } else {
                                        Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                        //reloadFragment();
                                    }
                                }

                            } catch (GeneralSecurityException e) {
                                e.printStackTrace();
                                //Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();


                                String norek = "", nom = "", berita = "";
                                byte[] dataX = Base64.decode(iResult2.getContents(), Base64.DEFAULT);
                                try {
                                    String datas = new String(dataX, "UTF-8");

                                    String[] arrdatas = datas.split("#");
                                    norek = arrdatas[0].replace(".", "");
                                    nom = arrdatas[1];
                                    berita = arrdatas[2];

                                    if (norek.length() == 13) {
                                        if (jwtpub.equals("0"))
                                            Toast.makeText(NewMainActivity.this, "Tidak bisa diproses, periksa koneksi anda tunggu hingga indikator berwarna biru", Toast.LENGTH_SHORT).show();
                                        else {
                                            Intent intent = new Intent(NewMainActivity.this, AntarRekeningToko.class);
                                            intent.putExtra("norek", norek);
                                            intent.putExtra("nom", nom);
                                            intent.putExtra("berita", berita);
                                            startActivity(intent);
                                        }
                                        if (!cam) {
                                            cam = true;
                                        }
                                    } else {
                                        Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                                        //reloadFragment();
                                    }

                                } catch (UnsupportedEncodingException ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(NewMainActivity.this, "Format No. Rekening salah, silahkan coba lagi", Toast.LENGTH_SHORT).show();
                            }

                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, result);
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, result);
            }
        }

        super.onActivityResult(requestCode, resultCode, result);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, result);
        }

    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            progress_dialog.show();
            Uri img = Crop.getOutput(result);
            new Save().execute(img);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void camera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File newfile = createFile("SrcFilePhoto");
            fileUri = FileProvider.getUriForFile(NewMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", newfile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, "No Camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createFile(String fileName) {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        File newfile = new File(dir, fileName + ".jpg");
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }
        return newfile;
    }

    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.setRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    //ayik 2019-01-07
    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
                    break;
                case 1:
                    break;
            }
        }
    };

    class ResultPhoto extends AsyncTask<Void, Void, Void> {
        private ProgressDialog mProgressDialog = new ProgressDialog(NewMainActivity.this);

        @Override
        protected void onPreExecute() {
            //mProgressDialog.setTitle("Whatever title");
            mProgressDialog.setMessage(getString(R.string.memproses_foro));
            mProgressDialog.show();
        }

        protected Void doInBackground(Void... params) {
            File newfile = new File(dirPath + "/" + "SrcFilePhoto.jpg");
            Bitmap bitmap = BitmapFactory.decodeFile(dirPath + "/" + "SrcFilePhoto.jpg");
            bitmap = imageOreintationValidator(bitmap, newfile.getAbsolutePath());
            saveImage(newfile.getAbsolutePath(), bitmap);
            Uri imgFile = FileProvider.getUriForFile(NewMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", newfile);
            beginCrop(imgFile);
            return null;
        }

        protected void onPostExecute(Void result) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    class Save extends AsyncTask<Uri, Void, Void> {
        private ProgressDialog mProgressDialog = new ProgressDialog(NewMainActivity.this);

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Memproses Cropping...");
            mProgressDialog.show();
        }

        protected Void doInBackground(Uri... imageUri) {
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(NewMainActivity.this.getContentResolver(), imageUri[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dirPath, fileName);
            try {
                FileOutputStream fOut = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    File newFile = new File(dirPath + "/" + fileName);
                    Uri imgFile = FileProvider.getUriForFile(NewMainActivity.this, BuildConfig.APPLICATION_ID + ".provider", newFile);
                    if (newFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
                        resultView.setImageBitmap(myBitmap);
                    } else {
                        resultView.setImageURI(imgFile);
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }
                    progressHandler.sendEmptyMessage(0);
                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }

    private void saveImage(String imagePath, Bitmap b) {

        try {
            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);
            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();
            b.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void dummyTLS() {

        try {

            ProviderInstaller.installIfNeeded(getApplicationContext());

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);

            SSLEngine engine = sslContext.createSSLEngine();

            String[] supportedProtocols = engine.getSupportedProtocols();

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

    private class AsyncVA extends AsyncTask<Void, Void, Void> {
        private Boolean getVA = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        String nama = "", alamat = "", rekening = "", telepon = "";
        String kodebmt = "";
        long novaint;
        String novastr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbarVA.setVisibility(View.VISIBLE);
            tvTapVA.setVisibility(View.GONE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(NewMainActivity.this).decrypt(getResources().getString(R.string.urlGetVA)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi));

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
                getVA = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");

                telepon = (String) jsonObject.get("telepon");
                nama = (String) jsonObject.get("nama");
                alamat = (String) jsonObject.get("alamat");
                rekening = (String) jsonObject.get("rekening");
                novaint = (long) jsonObject.get("nova");
                novastr = String.format("%06d", novaint);

                jwtpub = (String) jsonObject.get("jwt");

            } catch (Exception ex) {
                ex.printStackTrace();
                getVA = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pbarVA.setVisibility(View.GONE);

            if (getVA) {
                kodebmt = getResources().getString(R.string.kodebmt);
                String finalnova = "888080" + kodebmt + rekening.substring(0, 3) + novastr;
                llVA.setVisibility(View.VISIBLE);
                tvVA.setText(finalnova);
            } else {
                tvTapVA.setVisibility(View.VISIBLE);
                tvVA.setVisibility(View.GONE);
                String msga = "#" + ket + "\n";
                Toast.makeText(NewMainActivity.this, msga, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String toJsonString(String nokartu, String imsi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);

        return obj.toString();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
            return true;
        }
        return false;
    }

}
