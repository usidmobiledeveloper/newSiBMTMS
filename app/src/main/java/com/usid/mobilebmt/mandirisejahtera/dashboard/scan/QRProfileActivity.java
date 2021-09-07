package com.usid.mobilebmt.mandirisejahtera.dashboard.scan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.usid.mobilebmt.mandirisejahtera.NewMainActivity;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.SplashActivity;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityQrprofileBinding;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Hashtable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.usid.mobilebmt.mandirisejahtera.NewMainActivity.jwtpub;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class QRProfileActivity extends AppCompatActivity implements IAsyncHandler {

    private SharedPreferences config;

    private Menu statusMenu;
    private int sts = 0;
    private boolean stats = false;
    private Ctd cnd;
    private NetworkChangeReceiver receiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean finis = true;

    private String dirPath = "", fileName = "fp_mobilebmt.jpg";
    private CircleImageView resultView;
    private ImageView imgQR, imgBarcode;
    private TextView tvnama, tvnohp, tvAlamat;
    private Uri fileUri;
    private ProgressDialog mProgressDialog;

    private String imsi = "", nocard = "", jwtlocal;
    private String telepon, nama, alamat, rekening;
    TextView tvRek;

    ActivityQrprofileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrprofileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_qrprofile);
        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + getString(R.string.path2) + "data";
        mProgressDialog = new ProgressDialog(QRProfileActivity.this);

        cnd = new Ctd(QRProfileActivity.this);

        tvnama = findViewById(R.id.fakun_nama);
        tvnohp = findViewById(R.id.fakun_telp);

        tvAlamat = findViewById(R.id.fakun_alamat);

        imgQR = findViewById(R.id.img_qrcode);
        imgBarcode = findViewById(R.id.img_barcode);

        config = getSharedPreferences("config", 0);

        resultView = findViewById(R.id.img_profile);
        tvRek = findViewById(R.id.tv_rekening);

        //tambahan baru ayik==
        mReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver, intentFilter);

        SnHp telpMan = new SnHp(QRProfileActivity.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
           /* try {
                imsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            imsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(QRProfileActivity.this);
        try {
            nocard = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new AsyncGetRekening().execute();

        imgQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QRProfileActivity.this, ZoomQRActivity.class);
                String rekening = tvRek.getText().toString();
                String nama = tvnama.getText().toString();
                i.putExtra("rekening", rekening);
                i.putExtra("nama", nama);
                startActivity(i);

            }
        });

        imgBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(QRProfileActivity.this, ZoomBarcodeActivity.class);
                String rekening = tvRek.getText().toString();
                String nama = tvnama.getText().toString();
                i.putExtra("rekening", rekening);
                i.putExtra("nama", nama);
                startActivity(i);

            }
        });
    }

    private class AsyncGetRekening extends AsyncTask<Void, Void, Void> {
        private Boolean getRekening = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            disableViews(binding.scr);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setTitle("Memproses");
            mProgressDialog.setMessage("Tunggu...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(QRProfileActivity.this).decrypt(getResources().getString(R.string.urlGetRekening)));
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
                getRekening = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");

                telepon = (String) jsonObject.get("telepon");
                nama = (String) jsonObject.get("nama");
                alamat = (String) jsonObject.get("alamat");
                rekening = (String) jsonObject.get("rekening");

                jwtpub = (String) jsonObject.get("jwt");

            } catch (Exception ex) {
                ex.printStackTrace();
                getRekening = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (getRekening) {
                tvnama.setText(nama);
                tvnohp.setText(telepon);
                tvAlamat.setText(alamat);

           /* //sementara
            rekening = "1011102688001";*/

                rekening = rekening.replace(".", "");
                tvRek.setText(rekening);

                try {
                    rekening = new NumSky(QRProfileActivity.this).encrypt(rekening);
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                new LoadImage().execute();

            } else {

                mProgressDialog.dismiss();

                String msga = "#" + ket + "\n";
                showAlert("GAGAL", msga);
            }
        }
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(QRProfileActivity.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        }).show();
    }

    private String toJsonString(String nokartu, String imsi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);

        return obj.toString();
    }

    @Override
    public void onPostExec(Boolean status, String jwt/*, String tokenid*/) {
        if (status) {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_blue);
            jwtlocal = jwt;
            cnd.countD.start();
        } else {
            statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_red);
            jwtlocal = jwt;
            if (jwtlocal.equals("401")) {
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
            AuthLogin2 task = new AuthLogin2(QRProfileActivity.this, QRProfileActivity.this);
            task.execute();
        }

    }

    class LoadImage extends AsyncTask<Uri, Void, Void> {

        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
                        resultView.setImageBitmap(myBitmap);
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            // here
            new GenerateQRCode().execute();
        }
    }

    class GenerateQRCode extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(rekening, BarcodeFormat.QR_CODE, 512, 512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        for (int x = 0; x < width; x++) {
                            for (int y = 0; y < height; y++) {
                                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                            }
                        }

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo_qr);
                        Bitmap merge = mergeBitmaps(bitmap, bmp);
                        imgQR.setImageBitmap(merge);
                        imgQR.setVisibility(View.VISIBLE);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {

            new GenerateBarcode().execute();
        }
    }


    class GenerateBarcode extends AsyncTask<Uri, Void, Void> {
        @Override
        protected void onPreExecute() {

        }

        protected Void doInBackground(Uri... imageUri) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
                        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
                        Writer codeWriter;
                        codeWriter = new Code128Writer();
                        BitMatrix byteMatrix = codeWriter.encode(rekening, BarcodeFormat.CODE_128, 1000, 200, hintMap);
                        int width = byteMatrix.getWidth();
                        int height = byteMatrix.getHeight();
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        for (int i = 0; i < width; i++) {
                            for (int j = 0; j < height; j++) {
                                bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                            }
                        }
                        imgBarcode.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {
            enableViews(binding.scr);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
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
            AuthLogin2 task = new AuthLogin2(QRProfileActivity.this, QRProfileActivity.this);
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
                        Utility2.showAlertRelogin(QRProfileActivity.this);

                    else {
                        cnd.countD.cancel();
                        jwtlocal = "0";
                        statusMenu.findItem(R.id.action_status).setIcon(R.drawable.ic_action_status_yellow);
                        AuthLogin2 task = new AuthLogin2(QRProfileActivity.this, QRProfileActivity.this);
                        task.execute();
                    }
                }
            }
        };
    }

    private void generateQrCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_notif);
            Bitmap merge = mergeBitmaps(bitmap, bmp);
            imgQR.setImageBitmap(merge);
            imgQR.setVisibility(View.VISIBLE);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void generateBarcode(String text) {

        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();
            BitMatrix byteMatrix = codeWriter.encode(text, BarcodeFormat.CODE_128, 1000, 200, hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            imgBarcode.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }
}