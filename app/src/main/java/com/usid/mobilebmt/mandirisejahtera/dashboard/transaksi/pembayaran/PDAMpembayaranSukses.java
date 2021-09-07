package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembayaran;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityPdampembayaranSuksesBinding;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class PDAMpembayaranSukses extends AppCompatActivity {
    private Button btnOK;
    private Bundle extras;
    private LinearLayout mKonten;
    private TextView tvInfo, tvJudul;
    private String strJudul = "", strImsi = "", strIsi = "";
    ScrollView scrollView;
    ActivityPdampembayaranSuksesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdampembayaranSuksesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            extras = getIntent().getExtras();
            if (extras == null) {
                strJudul = "";
                strIsi = "";
            } else {
                strJudul = extras.getString("JUDUL");
                strIsi = extras.getString("ISI");
            }
        } else {
            strJudul = (String) savedInstanceState.getSerializable("JUDUL");
            strIsi = (String) savedInstanceState.getSerializable("ISI");
        }
        mKonten = (LinearLayout) findViewById(R.id.mKontenStruk);
        scrollView = findViewById(R.id.Scr);
        btnOK = (Button) findViewById(R.id.btnOK);
        tvJudul = (TextView) findViewById(R.id.tvJudul);
        tvInfo = (TextView) findViewById(R.id.tvInfo);
        tvJudul.setText(strJudul);
        tvInfo.setText("Sedang memproses...");
        btnOK.setVisibility(View.GONE);
        mKonten.setVisibility(View.GONE);
        Utility.playNotificationSound(PDAMpembayaranSukses.this);
        SnHp telpMan = new SnHp(PDAMpembayaranSukses.this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {

            strImsi = Utility.getIMSIRead(PDAMpembayaranSukses.this);

        }
        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            String[] parts = strIsi.split("\n");
            ArrayList<String> arr = new ArrayList<>();
            for (String part : parts) {
                if (part.contains(":") && !part.trim().endsWith(":") && !part.trim().endsWith(": ")) {
                    arr.add(part);
                }
            }
            mKonten.removeAllViews();
            Collections.reverse(arr);
            Utility.addRow(arr, PDAMpembayaranSukses.this, mKonten);
            tvInfo.setText("Transaksi Pembayaran PDAM Berhasil");
            btnOK.setVisibility(View.VISIBLE);
            mKonten.setVisibility(View.VISIBLE);
        }
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                finish();
            }
        });

        Typeface face = Typeface.createFromAsset(getAssets(), "cour.ttf");
        tvInfo.setTypeface(face, Typeface.BOLD);
    }

    private void takeScreenshot() {
        try {
            String sDCard = /*Environment.getExternalStorageDirectory().getAbsolutePath()*/"";
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            /*} else {
                sDCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            }*/
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
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            //Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());

            Bitmap bitmap = getBitmapFromView(binding.zv, binding.zv.getChildAt(0).getHeight(), binding.zv.getChildAt(0).getWidth());

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
            Toast.makeText(PDAMpembayaranSukses.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /*private static Bitmap mark(Bitmap src, String watermark) {
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
    }*/

    private Bitmap getBitmapFromView(View view, int height, int width) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return bitmap;
    }

    private Bitmap mark(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setAlpha(1);
        paint.setColor(getResources().getColor(R.color.trans));
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        canvas.drawText(watermark, 50, h * 99 / 100, paint);

        return result;
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(PDAMpembayaranSukses.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(PDAMpembayaranSukses.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.riwayat_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_riwayat) {
            share();
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    String now;
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");

    private void share() {
        try {
            Date naw = new Date();
            now = sdf4.format(naw);
            String sDCard = /*Environment.getExternalStorageDirectory().getAbsolutePath()*/"";
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            sDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
           /* } else {
                sDCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            }*/
            File newFolder = new File(sDCard + getString(R.string.path1));
            if (!newFolder.exists()) {
                newFolder.mkdirs();
            }
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = sDCard + getString(R.string.path2) + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
//            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            Bitmap bitmap = getBitmapFromView(binding.zv, binding.zv.getChildAt(0).getHeight(), binding.zv.getChildAt(0).getWidth());

            bitmap = mark(bitmap, getString(R.string.watermark));
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);
            try {
                FileOutputStream outputStream = new FileOutputStream(imageFile);
                int quality = 100;
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            sendScreenshoot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            Toast.makeText(PDAMpembayaranSukses.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendScreenshoot(File imageFile) {
//        Uri uri = Uri.parse("file://" + imageFile.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(PDAMpembayaranSukses.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        startActivity(Intent.createChooser(sendIntent, "Share images to..."));
    }
}
