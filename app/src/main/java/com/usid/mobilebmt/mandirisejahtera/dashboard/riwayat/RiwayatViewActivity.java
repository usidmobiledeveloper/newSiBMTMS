package com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityRiwayatViewBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;

public class RiwayatViewActivity extends AppCompatActivity {

    ActivityRiwayatViewBinding binding;
    String faktur = "", jumlah = "", keterangan = "", trxid = "", jenis = "", produk = "", tujuan = "", tgl = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRiwayatViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        faktur = extras.getString("faktur");
        tgl = extras.getString("tgl");
        jumlah = extras.getString("jumlah");
        keterangan = extras.getString("keterangan");

        trxid = extras.getString("trxid");
        jenis = extras.getString("jenis");
        produk = extras.getString("produk");
        tujuan = extras.getString("tujuan");

        //binding.tvTujuan.setText("Ke "+tujuan);

        binding.tvFaktur.setText(faktur);
        binding.tvTgl.setText(tgl);
        binding.tvJumlah.setText(jumlah);

        binding.tvKet.setText(keterangan);

        if (jenis.equalsIgnoreCase("TRANSFER")
                || jenis.equalsIgnoreCase("TRANSFER BANK")) {
            enableViews(binding.llTrf);
            disableViews(binding.llPpob, binding.llDonasi);
            binding.tvJudul.setText(jenis + " " + produk + " " + tujuan);
        } else if (jenis.equalsIgnoreCase("DONASI")) {
            enableViews(binding.llDonasi);
            disableViews(binding.llPpob, binding.llTrf);
            binding.tvJudul.setText(produk + " " + tujuan);
        } else {
            enableViews(binding.llPpob);
            disableViews(binding.llTrf, binding.llDonasi);

            if (keterangan.contains(" ")) {
                String[] x = keterangan.split(" ");
                String y = x[0];
                binding.tvJudul.setText(y + " " + produk);
                binding.tvPpob.setText(y);
            }

        }

        binding.fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
            }
        });


        Typeface face = Typeface.createFromAsset(getAssets(), "cour.ttf");

        binding.lbDetail.setTypeface(face, Typeface.BOLD);
        binding.tvJudul.setTypeface(face, Typeface.BOLD);

        binding.lbFaktur.setTypeface(face, Typeface.BOLD);
        binding.lbTitik2a.setTypeface(face, Typeface.BOLD);
        binding.tvFaktur.setTypeface(face, Typeface.BOLD);

        binding.lbTgl.setTypeface(face, Typeface.BOLD);
        binding.lbTitik2b.setTypeface(face, Typeface.BOLD);
        binding.tvTgl.setTypeface(face, Typeface.BOLD);

        binding.lbJumlah.setTypeface(face, Typeface.BOLD);
        binding.lbTitik2c.setTypeface(face, Typeface.BOLD);
        binding.lbRp.setTypeface(face, Typeface.BOLD);
        binding.tvJumlah.setTypeface(face, Typeface.BOLD);

        binding.lbKet.setTypeface(face, Typeface.BOLD);
        binding.tvKet.setTypeface(face, Typeface.BOLD);

    }

    private String now;
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");

    private void takeScreenshot() {
        try {
            Date naw = new Date();
            now = sdf4.format(naw);
            //String sDCard = Environment.getExternalStorageDirectory().getAbsolutePath();
            String sDCard = ""/*Environment.getExternalStorageDirectory().getAbsolutePath()*/;
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
            Toast.makeText(RiwayatViewActivity.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendScreenshoot(File imageFile) {
//        Uri uri = Uri.parse("file://" + imageFile.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(RiwayatViewActivity.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        startActivity(Intent.createChooser(sendIntent, "Share images to..."));
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.riwayat_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_riwayat) {
            takeScreenshot();
        }

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}