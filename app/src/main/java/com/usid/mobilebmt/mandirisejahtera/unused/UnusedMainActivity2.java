//package com.usid.mobilebmt.mandirisejahtera.unused;
//
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.media.ExifInterface;
//import android.net.ConnectivityManager;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.provider.MediaStore;
//import androidx.annotation.NonNull;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import androidx.fragment.app.Fragment;
//import androidx.core.content.FileProvider;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.soundcloud.android.crop.Crop;
//import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
//import com.usid.mobilebmt.mandirisejahtera.R;
//import com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused.AkunFragment;
//import com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused.BerandaFragment;
//import com.usid.mobilebmt.mandirisejahtera.unusedfragment.unused.TransaksiFragment;
//import com.usid.mobilebmt.mandirisejahtera.notifications.Config;
//import com.usid.mobilebmt.mandirisejahtera.notifications.NotificationUtils;
//import com.usid.mobilebmt.mandirisejahtera.utils.AuthLogin2;
//import com.usid.mobilebmt.mandirisejahtera.utils.Ctd;
//import com.usid.mobilebmt.mandirisejahtera.utils.IAsyncHandler;
//import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
//import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//import static android.Manifest.permission.READ_CONTACTS;
//import static android.Manifest.permission.READ_PHONE_STATE;
//import static android.Manifest.permission.SEND_SMS;
//import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//
//public class UnusedMainActivity2 extends AppCompatActivity implements IAsyncHandler {
//    private ImageView iv_status;
//    boolean doubleBackToExitPressedOnce = false;
//    private static final int REQUEST_READ_PHONE_STATE = 0;
//    private SharedPreferences config;
//    PackageInfo pInfo;
//    private int appversi;
//    private String imsi = "";
//    private BroadcastReceiver mRegistrationBroadcastReceiver;
//    public static String jwtpub;
//    private int sts = 0;
//    private boolean stats = false;
//    private Ctd cnd;
//    private NetworkChangeReceiver2 receiver;
//    public static boolean finisa = true;
//    private String dirPath = "", fileName = "fp_mobilebmt.jpg";
//    private CircleImageView resultView;
//    private TextView tvnama, tvnohp;
//    private Uri fileUri;
//    static final int REQUEST_IMAGE_CAPTURE = 8888;
//    private ProgressDialog progress_dialog;
//
//    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            Fragment fragment = null;
//            switch (item.getItemId()) {
//                case R.id.navigation_home:
//                    fragment = new BerandaFragment();
//                    tvnohp.setVisibility(View.GONE);
//                    tvnama.setVisibility(View.GONE);
//                    resultView.setVisibility(View.GONE);
//                    break;
//                case R.id.navigation_transaksi:
//                    fragment = new TransaksiFragment();
//                    tvnohp.setVisibility(View.GONE);
//                    tvnama.setVisibility(View.GONE);
//                    resultView.setVisibility(View.GONE);
//                    break;
//                case R.id.navigation_account:
//                    fragment = new AkunFragment();
//                    tvnohp.setVisibility(View.VISIBLE);
//                    tvnama.setVisibility(View.VISIBLE);
//                    resultView.setVisibility(View.VISIBLE);
//                    break;
//            }
//            return loadFragment(fragment);
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//        setContentView(R.layout.activity_main2);
//
//
//            dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+getString(R.string.path2) + "data";
//
//
//        BottomNavigationView navigation = findViewById(R.id.navigation);
//        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        tvnama = findViewById(R.id.fakun_nama);
//        tvnohp = findViewById(R.id.fakun_telp);
//        config = getSharedPreferences("config", 0);
//        tvnama.setText(config.getString("NAMA", ""));
//        tvnohp.setText(config.getString("NOHP", ""));
//        iv_status = toolbar.findViewById(R.id.img_indikator);
//        resultView = findViewById(R.id.img_profile);
//        resultView.setVisibility(View.GONE);
//        tvnohp.setVisibility(View.GONE);
//        tvnama.setVisibility(View.GONE);
//
//        progress_dialog = new ProgressDialog(this);
//        progress_dialog.setCancelable(false);
//        progress_dialog.setIndeterminate(false);
//        progress_dialog.setMessage("Memproses Cropping...");
//        populate();
//        try {
//            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
//            appversi = pInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        SnHp telpMan = new SnHp(this);
//        if (5 != telpMan.telephonyManager().getSimState()) {
//            imsi = "TIDAK ADA KARTU";
//        } else {
//           /* try {
//                imsi = telpMan.telephonyManager().getSimSerialNumber();
//            } catch (Exception e) {
//            }*/
//            imsi = Utility.getIMSIRead(this);
//        }
//        String versis = config.getString("MOBILEBMTUPDATE", "0");
//        if ((Integer.parseInt(versis) > appversi)) {
//            showStatusV("Update Aplikasi", "Mohon update Aplikasi " + getString(R.string.app_name) + " Versi " + versis.substring(0, 1) + "." + versis.substring(1) + "\nBuka Play Store Sekarang?");
//        } else if (imsi.equals("TIDAK ADA KARTU")) {
//            showExt("ERROR", "Masukkan SIM CARD!");
//        }
//        regFirebase();
//        cnd = new Ctd(UnusedMainActivity2.this);
//        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//        receiver = new NetworkChangeReceiver2();
//        registerReceiver(receiver, filter);
//        if (!stats) {
//            cnd.countD.cancel();
//            jwtpub = "0";
//            AuthLogin2 task = new AuthLogin2(UnusedMainActivity2.this, UnusedMainActivity2.this);
//            task.execute();
//            stats = true;
//            sts = 1;
//        }
//        resultView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(UnusedMainActivity2.this);
//                alertDialogBuilder.setTitle("Foto Akun");
//                final String[] items = {"Ambil Foto", "Galeri"};
//                alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        String action = items[i];
//                        if (action.equalsIgnoreCase("ambil foto")) {
//                            camera();
//                        } else {
//                            Crop.pickImage(UnusedMainActivity2.this);
//                        }
//                    }
//                });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
//            }
//        });
//
//        //ayik baru
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            // Create channel to show notifications.
//            String channelId = getString(R.string.default_notification_channel_id);
//            String channelName = getString(R.string.default_notification_channel_name);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));
//        }
//
//        if (getIntent().getExtras() != null) {
//            for (String key : getIntent().getExtras().keySet()) {
//                Object value = getIntent().getExtras().get(key);
//                //Toast.makeText(this, "Key: " + key + " Value: " + value, Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        loadFragment(new BerandaFragment());
//    }
//
//    private boolean loadFragment(Fragment fragment) {
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
//            getSupportFragmentManager().popBackStackImmediate("main", 0);
//        } else {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                finish();
//                return;
//            }
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Klik tombol kembali lagi untuk keluar", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 1500);
//        }
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
//    @Override
//    public void onResume() {
//        super.onResume();
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.KEY_COUNTDOWNTIMER));
//        //TAMBAHAN AYIK=============================================================================
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
//        NotificationUtils.clearNotifications(getApplicationContext());
//        //==========================================================================================
//        // tambahan baru ayik==
//        File file = new File(dirPath, ".nomedia");
//        if (!file.exists()) {
//            File dir = new File(dirPath);
//            if (!dir.exists()) dir.mkdirs();
//            File files = new File(dirPath, ".nomedia");
//            try {
//                FileOutputStream fOut = new FileOutputStream(files);
//                fOut.flush();
//                fOut.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        File imgFile = new File(dirPath + "/" + fileName);
//        if (imgFile.exists()) {
//            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            //Drawable d = new BitmapDrawable(getResources(), myBitmap);
//            resultView.setImageBitmap(myBitmap);
//        }
//        if (stats && sts > 1) {
//            cnd.countD.cancel();
//            jwtpub = "0";
//            iv_status.setImageResource(R.drawable.ic_action_status_yellow);
//            AuthLogin2 task = new AuthLogin2(UnusedMainActivity2.this, UnusedMainActivity2.this);
//            task.execute();
//        }
//    }
//
//    private void populate() {
//        if (!mayRequest()) {
//            return;
//        }
//    }
//
//    private boolean mayRequest() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            return true;
//        }
//        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(SEND_SMS) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
//            return true;
//        }
//        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
//            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS}, REQUEST_READ_PHONE_STATE);
//        } else {
//            requestPermissions(new String[]{READ_PHONE_STATE, SEND_SMS, WRITE_EXTERNAL_STORAGE, READ_CONTACTS}, REQUEST_READ_PHONE_STATE);
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_READ_PHONE_STATE) {
//            if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
//            } else {
//                finish();
//            }
//        }
//    }
//
//    private void regFirebase() {
//        //TAMBAHAN AYIK=============================================================================
//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                // checking for type intent filter
//                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
//                    // gcm successfully registered
//                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//                    String message = intent.getStringExtra("message");
//                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//                }
//                if (intent.getAction().equals(Config.KEY_COUNTDOWNTIMER)) {
//                    if (finisa) finish();
//                    else {
////                        Toast.makeText(MainLamaActivity.this, "Waktu koneksi habis, pastikan indikator koneksi berwarna biru, silahkan restart aplikasi", Toast.LENGTH_SHORT).show();
//                        cnd.countD.cancel();
//                        jwtpub = "0";
//                        iv_status.setImageResource(R.drawable.ic_action_status_yellow);
//                        AuthLogin2 task = new AuthLogin2(UnusedMainActivity2.this, UnusedMainActivity2.this);
//                        task.execute();
//                    }
//                }
//            }
//        };
//        //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
//        displayFirebaseRegId();
//        //==========================================================================================
//    }
//
//    //TAMBAHAN AYIK=================================================================================
//    private void displayFirebaseRegId() {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
//        String regId = pref.getString("regId", null);
//    }
//
//    @Override
//    protected void onPause() {
//        // TAMBAHAN AYIK============================================================================
//        super.onPause();
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
//        //==========================================================================================
//        // tambahan baru ayik==
//        cnd.countD.cancel();
//    }
//
//    //tambahan baru ayik==
//    @Override
//    public void onPostExec(Boolean status, String jwt) {
//        if (status) {
//            iv_status.setImageResource(R.drawable.ic_action_status_blue);
//            jwtpub = jwt;
//            cnd.countD.start();
//        } else {
//            iv_status.setImageResource(R.drawable.ic_action_status_red);
//            jwtpub = jwt;
//        }
//        sts = sts + 1;
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
//        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
//            beginCrop(result.getData());
//        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            new ResultPhoto().execute();
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            handleCrop(resultCode, result);
//        }
//    }
//
//    private void beginCrop(Uri source) {
//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Crop.of(source, destination).asSquare().start(this);
//    }
//
//    private void handleCrop(int resultCode, Intent result) {
//        if (resultCode == RESULT_OK) {
//            progress_dialog.show();
//            Uri img = Crop.getOutput(result);
//            new Save().execute(img);
//        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void camera() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File newfile = createFile("SrcFilePhoto");
//            fileUri = FileProvider.getUriForFile(UnusedMainActivity2.this, BuildConfig.APPLICATION_ID + ".provider", newfile);
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        } else {
//            Toast.makeText(this, "No Camera", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private File createFile(String fileName) {
//        File dir = new File(dirPath);
//        if (!dir.exists()) dir.mkdirs();
//        File newfile = new File(dir, fileName + ".jpg");
//        try {
//            newfile.createNewFile();
//        } catch (IOException e) {
//        }
//        return newfile;
//    }
//
//    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {
//        ExifInterface ei;
//        try {
//            ei = new ExifInterface(path);
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    bitmap = rotateImage(bitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    bitmap = rotateImage(bitmap, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    bitmap = rotateImage(bitmap, 270);
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    private Bitmap rotateImage(Bitmap source, float angle) {
//        Bitmap bitmap = null;
//        Matrix matrix = new Matrix();
//        matrix.setRotate(angle);
//        try {
//            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//        } catch (OutOfMemoryError err) {
//            err.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    //ayik 2019-01-07
//    @SuppressLint("HandlerLeak")
//    private Handler progressHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 0:
//                    if (progress_dialog.isShowing()) progress_dialog.dismiss();
//                    break;
//                case 1:
//                    break;
//            }
//        }
//    };
//
//    class ResultPhoto extends AsyncTask<Void, Void, Void> {
//        private ProgressDialog mProgressDialog = new ProgressDialog(UnusedMainActivity2.this);
//
//        @Override
//        protected void onPreExecute() {
//            //mProgressDialog.setTitle("Whatever title");
//            mProgressDialog.setMessage("Memproses Foto...");
//            mProgressDialog.show();
//        }
//
//        protected Void doInBackground(Void... params) {
//            File newfile = new File(dirPath + "/" + "SrcFilePhoto.jpg");
//            Bitmap bitmap = BitmapFactory.decodeFile(dirPath + "/" + "SrcFilePhoto.jpg");
//            bitmap = imageOreintationValidator(bitmap, newfile.getAbsolutePath());
//            saveImage(newfile.getAbsolutePath(), bitmap);
//            Uri imgFile = FileProvider.getUriForFile(UnusedMainActivity2.this, BuildConfig.APPLICATION_ID + ".provider", newfile);
//            beginCrop(imgFile);
//            return null;
//        }
//
//        protected void onPostExecute(Void result) {
//            mProgressDialog.dismiss();
//            mProgressDialog = null;
//        }
//    }
//
//    class Save extends AsyncTask<Uri, Void, Void> {
//        private ProgressDialog mProgressDialog = new ProgressDialog(UnusedMainActivity2.this);
//
//        @Override
//        protected void onPreExecute() {
//            mProgressDialog.setMessage("Memproses Cropping...");
//            mProgressDialog.show();
//        }
//
//        protected Void doInBackground(Uri... imageUri) {
//            Bitmap bmp = null;
//            try {
//                bmp = MediaStore.Images.Media.getBitmap(UnusedMainActivity2.this.getContentResolver(), imageUri[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            File dir = new File(dirPath);
//            if (!dir.exists()) dir.mkdirs();
//            File file = new File(dirPath, fileName);
//            try {
//                FileOutputStream fOut = new FileOutputStream(file);
//                bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
//                fOut.flush();
//                fOut.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    File newFile = new File(dirPath + "/" + fileName);
//                    Uri imgFile = FileProvider.getUriForFile(UnusedMainActivity2.this, BuildConfig.APPLICATION_ID + ".provider", newFile);
//                    if (newFile.exists()) {
//                        Bitmap myBitmap = BitmapFactory.decodeFile(newFile.getAbsolutePath());
//                        resultView.setImageBitmap(myBitmap);
//                    } else {
//                        resultView.setImageURI(imgFile);
//                    }
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                        mProgressDialog.dismiss();
//                        mProgressDialog = null;
//                    }
//                    progressHandler.sendEmptyMessage(0);
//                }
//            });
//            return null;
//        }
//
//        protected void onPostExecute(Void result) {
//            if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                mProgressDialog.dismiss();
//                mProgressDialog = null;
//            }
//        }
//    }
//
//    private void saveImage(String imagePath, Bitmap b) {
//
//        try {
//            FileOutputStream fOut = new FileOutputStream(imagePath);
//            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
//            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);
//            FileOutputStream out = new FileOutputStream(imagePath);
//            if (imageType.equalsIgnoreCase("png")) {
//                b.compress(Bitmap.CompressFormat.PNG, 100, out);
//            } else if (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpg")) {
//                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            }
//            fOut.flush();
//            fOut.close();
//            b.recycle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }
//
//    public class NetworkChangeReceiver2 extends BroadcastReceiver {
//        @Override
//        public void onReceive(final Context context, final Intent intent) {
//            if (Utility.isNetworkAvailable(context)) ;
//            onResume();
//        }
//    }
//}
