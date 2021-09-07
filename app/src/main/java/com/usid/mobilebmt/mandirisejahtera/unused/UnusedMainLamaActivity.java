//package com.usid.mobilebmt.mandirisejahtera.unused;
//
//import android.content.BroadcastReceiver;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import android.view.Menu;
//
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
//import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
//

//public class UnusedMainLamaActivity extends AppCompatActivity implements IAsyncHandler {
//    private Toolbar toolbar = null;
//    private static final int REQUEST_READ_PHONE_STATE = 0;
//    private SharedPreferences config;
//    PackageInfo pInfo;
//    private String imsi = "";
//    private int appversi;
//    boolean doubleBackToExitPressedOnce = false;
//
//    //TAMBAHAN AYIK=================================================================================
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    //==============================================================================================
//
//    // tambahan baru ayik==
//    private Menu statusMenu;
////    public static String jwtpub;
//    private int sts = 0;
//    private boolean stats = false;
//    private Ctd cnd;
////    private NetworkChangeReceiver receiver;
////    public static boolean finisa = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_lama);
////        config = getSharedPreferences("config", 0);
////        try {
////            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
////            appversi = pInfo.versionCode;
////        } catch (PackageManager.NameNotFoundException e) {
////            e.printStackTrace();
////        }
////        SnHp telpMan = new SnHp(this);
////        if (5 != telpMan.telephonyManager().getSimState()) {
////            imsi = "TIDAK ADA KARTU";
////        } else {
////            try {
////                imsi = telpMan.telephonyManager().getSimSerialNumber();
////            } catch (Exception e) {
////            }
////        }
////        String versis = config.getString("MOBILEBMTUPDATE", "0");
////        populate();
////        MainFragment fragment = new MainFragment();
////        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
////        fragmentTransaction.replace(R.id.fragment_container, fragment, "main").addToBackStack("main");
////        fragmentTransaction.commit();
////        toolbar = (Toolbar) findViewById(R.id.toolbar);
////        setSupportActionBar(toolbar);
////        if ((Integer.parseInt(versis) > appversi)) {
////            showStatusV("Update Aplikasi", "Mohon update Aplikasi Versi " + versis.substring(0, 1) + "." + versis.substring(1) + "\nBuka Play Store Sekarang?");
////        } else if (imsi.equals("TIDAK ADA KARTU")) {
////            showExt("ERROR", "Masukkan SIM CARD!");
////        }
////        // tambahan baru ayik
////        regFirebase();
////        cnd = new Ctd(MainLamaActivity.this);
////        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
////        receiver = new NetworkChangeReceiver();
////        registerReceiver(receiver, filter);
//    }
//
////    @Override
////    public boolean onCreateOptionsMenu(Menu menu) {
////        getMenuInflater().inflate(R.menu.main, menu);
////
////        // tambahan baru ayik==
////        menu.findItem(R.id.action_status).setEnabled(false);
////        statusMenu = menu;
////        if (!stats) {
////            cnd.countD.cancel();
////            jwtpub = "0";
////            AuthLogin2 task = new AuthLogin2(MainLamaActivity.this, MainLamaActivity.this);
////            task.execute();
////            stats = true;
////            sts = 1;
////        }
////
////        return true;
////    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        int id = item.getItemId();
////        if (id == R.id.action_logout) {
////            finish();
////            return true;
////        }
////        return super.onOptionsItemSelected(item);
////    }
////
////    @Override
////    public void onBackPressed() {
////        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
////            getSupportFragmentManager().popBackStackImmediate("main", 0);
////        } else {
////            if (doubleBackToExitPressedOnce) {
////                super.onBackPressed();
////                finish();
////                return;
////            }
////            this.doubleBackToExitPressedOnce = true;
////            Toast.makeText(this, "Klik tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT).show();
////            new Handler().postDelayed(new Runnable() {
////                @Override
////                public void run() {
////                    doubleBackToExitPressedOnce = false;
////                }
////            }, 2000);
////        }
////    }
////
////    @Override
////    public boolean onPrepareOptionsMenu(Menu menu) {
////        menu.findItem(R.id.action_logout).setVisible(true);
////        return super.onPrepareOptionsMenu(menu);
////    }
////
////    private void showStatusV(String title, String message) {
////        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                try {
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.usid.")));
////                } catch (android.content.ActivityNotFoundException anfe) {
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.usid.")));
////                }
////                finish();
////                dialog.dismiss();
////            }
////        }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                finish();
////                dialog.dismiss();
////            }
////        }).show();
////    }
////
////    private void showExt(String title, String message) {
////        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                finish();
////                dialog.dismiss();
////            }
////        }).show();
////    }
////
////    public void setActionBarTitle(String title) {
////        getSupportActionBar().setTitle(title);
////    }
////
////    @Override
////    public void onResume() {
////        super.onResume();
////        // Set title
////        setActionBarTitle(getString(R.string.app_name));
////        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
////        //TAMBAHAN AYIK=============================================================================
////        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
////        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
////        NotificationUtils.clearNotifications(getApplicationContext());
////        //==========================================================================================
////        // tambahan baru ayik==
////        if (stats && sts > 1) {
////            cnd.countD.cancel();
////            jwtpub = "0";
////            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
////            AuthLogin2 task = new AuthLogin2(MainLamaActivity.this, MainLamaActivity.this);
////            task.execute();
////        }
////    }
////
////    private void populate() {
////        if (!mayRequest()) {
////            return;
////        }
////    }
////
////    private boolean mayRequest() {
////        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
////            return true;
////        }
////        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
////            return true;
////        }
////        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
////            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS}, REQUEST_READ_PHONE_STATE);
////        } else {
////            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS}, REQUEST_READ_PHONE_STATE);
////        }
////        return false;
////    }
////
////    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        if (requestCode == REQUEST_READ_PHONE_STATE) {
////            if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
////            } else {
////                finish();
////            }
////        }
////    }
////
////    private void regFirebase() {
////        //TAMBAHAN AYIK=============================================================================
////        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
////            @Override
////            public void onReceive(Context context, Intent intent) {
////                // checking for type intent filter
////                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
////                    // gcm successfully registered
////                    // now subscribe to `global` topic to receive app wide notifications
////                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
////                    displayFirebaseRegId();
////
////                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
////                    // new push notification is received
////                    String message = intent.getStringExtra("message");
////                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
////                }
////                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
////                    if (finisa) finish();
////                    else {
//////                        Toast.makeText(MainLamaActivity.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
////                        cnd.countD.cancel();
////                        jwtpub = "0";
////                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
////                        AuthLogin2 task = new AuthLogin2(MainLamaActivity.this, MainLamaActivity.this);
////                        task.execute();
////                    }
////                }
////            }
////        };
////        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
////        displayFirebaseRegId();
////        //==========================================================================================
////    }
////
////    //TAMBAHAN AYIK=================================================================================
////    private void displayFirebaseRegId() {
////        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
////        String regId = pref.getString("regId", null);
////    }
////
////    @Override
////    protected void onPause() {
////        // TAMBAHAN AYIK============================================================================
////        super.onPause();
////        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
////        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
////        //==========================================================================================
////        // tambahan baru ayik==
////        cnd.countD.cancel();
////    }
////    //tambahan baru ayik==
////    @Override
//    public void onPostExec(Boolean status, String jwt) {
//        if (status) {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
////            jwtpub = jwt;
//            cnd.countD.start();
//        } else {
//            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
////            jwtpub = jwt;
//        }
//        sts = sts + 1;
//    }
////
////    @Override
////    protected void onDestroy() {
////        super.onDestroy();
////        unregisterReceiver(receiver);
////    }
////
////    public class NetworkChangeReceiver extends BroadcastReceiver {
////        @Override
////        public void onReceive(final Context context, final Intent intent) {
////            if (Utility.isNetworkAvailable(context)) ;
////            onResume();
////        }
////    }
//}
