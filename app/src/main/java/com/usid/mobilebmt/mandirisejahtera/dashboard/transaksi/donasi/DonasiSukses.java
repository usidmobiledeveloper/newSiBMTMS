package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.donasi;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.LayoutTransition;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.BuildConfig;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityDonasiSuksesBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.addRowTrf;

public class DonasiSukses extends AppCompatActivity {

    ActivityDonasiSuksesBinding binding;
    String judul, darirek;
    ArrayList<String> arr = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDonasiSuksesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                judul = null;
                darirek = null;
                arr = null;
            } else {
                judul = extras.getString("judul");
                darirek = extras.getString("darirek");
                arr = extras.getStringArrayList("arr");
            }
        } else {
            judul = (String) savedInstanceState.getSerializable("judul");
            arr = (ArrayList<String>) savedInstanceState.getSerializable("arr");
        }

        binding.tvJudul.setText(judul);

        binding.mKontenStruk.removeAllViews();
        Collections.reverse(arr);
        addRowTrf(arr, this, binding.mKontenStruk);

        binding.btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                finish();
            }
        });

        binding.tvKet.setText(darirek);

        Typeface face = Typeface.createFromAsset(getAssets(), "cour.ttf");
        binding.tvJudul.setTypeface(face, Typeface.BOLD);
        binding.tvKet.setTypeface(face, Typeface.BOLD);
    }

    public static LinearLayout addRow(ArrayList<String> z, Context ctx, LinearLayout mKonten) {
        String token = "";
        for (String item : z) {
            String[] subs = item.split("#");
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View addView = layoutInflater.inflate(R.layout.struk_item_pembelian, null);
            final TextView mKiri = addView.findViewById(R.id.mKiri);
            if (subs[0].startsWith("OUTLET"))
                continue;

            mKiri.setText(subs[0]);
            final TextView mTgh = addView.findViewById(R.id.mTengah);
            final TextView mTitik2 = addView.findViewById(R.id.mTitik2);
            final TextView mRp = addView.findViewById(R.id.mRp);
            final TextView mKanan = addView.findViewById(R.id.mKanan);

            Typeface face = Typeface.createFromAsset(ctx.getAssets(), "cour.ttf");
            mKiri.setTypeface(face, Typeface.BOLD);
            mTgh.setTypeface(face, Typeface.BOLD);
            mTitik2.setTypeface(face, Typeface.BOLD);
            mRp.setTypeface(face, Typeface.BOLD);
            mKanan.setTypeface(face, Typeface.BOLD);


            final ImageButton btnSalin = addView.findViewById(R.id.btn_salin);

            if (subs[0].startsWith("TOKEN")) {
                btnSalin.setVisibility(View.VISIBLE);
            } else {
                btnSalin.setVisibility(View.INVISIBLE);
            }

            token = z.get(0).replace(" ", "");
            token = token.replace("TOKEN:", "");

            StringBuilder s;
            s = new StringBuilder(token);

            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            token = s.toString();

            final String finalToken = token;

            btnSalin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager manager = (ClipboardManager) ctx.getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Salin token", finalToken);
                    assert manager != null;
                    manager.setPrimaryClip(clipData);

                    Toast.makeText(ctx, "Salin token", Toast.LENGTH_SHORT).show();
                }
            });

            try {
               /* if (subs[1].contains("Rp")) {
                    mTitik2.setVisibility(View.GONE);
                    mKanan.setVisibility(View.GONE);
                    mTgh.setText(": Rp");
                    if (z.get(0).equals(item)) {
                        //mRp.setTypeface(face, Typeface.BOLD);
                        mRp.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        //mKiri.setTypeface(face, Typeface.BOLD);
                        mKiri.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                        //mTgh.setTypeface(face, Typeface.BOLD);
                        mTgh.setTextColor(ctx.getResources().getColor(R.color.ijotuakop1));
                    }
                    mRp.setText(subs[1].replace(" ", "").replace("Rp", ""));
                    mRp.setGravity(Gravity.RIGHT);
                } else {*/
                mRp.setVisibility(View.GONE);
                mTgh.setVisibility(View.GONE);
                mKanan.setText(subs[1]);
                mKanan.setGravity(Gravity.LEFT);
                /* }*/

            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            LayoutTransition transition = new LayoutTransition();
            mKonten.setLayoutTransition(transition);
            mKonten.addView(addView, 0);
        }
        return mKonten;
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

    private void takeScreenshot() {
        try {
            String sDCard =/* Environment.getExternalStorageDirectory().getAbsolutePath()*/"";
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
            Toast.makeText(DonasiSukses.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

    String now;
    private SimpleDateFormat sdf4 = new SimpleDateFormat("yyyyMMdd-HHmmss");

    private void share() {
        try {
            Date naw = new Date();
            now = sdf4.format(naw);
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
            Toast.makeText(DonasiSukses.this, "Gagal menyimpan screenshoot", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void sendScreenshoot(File imageFile) {
//        Uri uri = Uri.parse("file://" + imageFile.getAbsolutePath());
        Uri uri = FileProvider.getUriForFile(DonasiSukses.this, BuildConfig.APPLICATION_ID + ".provider", imageFile);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        startActivity(Intent.createChooser(sendIntent, "Share images to..."));
    }
}