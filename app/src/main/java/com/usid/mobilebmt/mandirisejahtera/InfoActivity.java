package com.usid.mobilebmt.mandirisejahtera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.GeneralSecurityException;

import static com.usid.mobilebmt.mandirisejahtera.utils.MyVal.MYTAG;

public class InfoActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btLanjut;
    private ProgressDialog pdLoading;
    private Bitmap bmp;
    private SharedPreferences config;
    private Boolean dl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lama);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        imageView = (ImageView) findViewById(R.id.imgInfo);
        btLanjut = (Button) findViewById(R.id.btnLanjut);
        btLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        config = getSharedPreferences("config", 0);

        if (!config.getString("IMAGE", "").equals(config.getString("DOWNLOAD", ""))) dl = true;
        pdLoading = new ProgressDialog(InfoActivity.this);
        pdLoading.setCancelable(false);
        pdLoading.setIndeterminate(false);
        pdLoading.setMessage("Tunggu...");
        if (dl) {
            pdLoading.show();
            DownloadImage(config.getString("IMAGE", ""));
            Handler handler = new Handler();
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    if (pdLoading.isShowing()) progressHandler.sendEmptyMessage(0);
                }
            };
            handler.postDelayed(runnableCode, 2000);
        } else {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            loadImageFromStorage(directory.getAbsolutePath());
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (pdLoading.isShowing()) pdLoading.dismiss();
                    break;
            }
        }
    };

    private void DownloadImage(String img) {

        try {
            InputStream in = new URL(MyVal.URL_BASE_CONTENT()+new NumSky(InfoActivity.this).decrypt(getResources().getString(R.string.urlInfoSplash)) + img).openStream();

            bmp = BitmapFactory.decodeStream(in);
            if (bmp != null) {
                imageView.setImageBitmap(bmp);
                saveToInternalStorage(bmp);
                SharedPreferences.Editor editor = config.edit();
                editor.putString("DOWNLOAD", img);
                editor.commit();
            } else {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                loadImageFromStorage(directory.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "imgsplash.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "imgsplash.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    public void onBackPressed() {
    }
}
/*
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class InfoActivity extends Activity {
    private ImageView imageView;
    private Button btLanjut;
    private ProgressDialog pdLoading;
    private Bitmap bmp;
    private SharedPreferences config;
    private Boolean dl = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_lama);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        imageView = (ImageView) findViewById(R.id.imgInfo);
        btLanjut = (Button) findViewById(R.id.btnLanjut);
        btLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainLamaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        config = getSharedPreferences("config", 0);
        if (!config.getString("IMAGE", "").equals(config.getString("DOWNLOAD", ""))) dl = true;
        pdLoading = new ProgressDialog(InfoActivity.this);
        pdLoading.setCancelable(false);
        pdLoading.setIndeterminate(false);
        pdLoading.setMessage("Tunggu...");
        if (dl) {
            pdLoading.show();
            DownloadImage(config.getString("IMAGE", ""));
            Handler handler = new Handler();
            Runnable runnableCode = new Runnable() {
                @Override
                public void run() {
                    if (pdLoading.isShowing()) progressHandler.sendEmptyMessage(0);
                }
            };
            handler.postDelayed(runnableCode, 2000);
        } else {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            loadImageFromStorage(directory.getAbsolutePath());
        }
    }

    private Handler progressHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    pdLoading.dismiss();
                    break;
            }
        }
    };

    private void DownloadImage(String img) {
        try {
            InputStream in = new URL(new NumSky(InfoActivity.this).decrypt(getResources().getString(R.string.urlInfoSplash)) + img).openStream();
            bmp = BitmapFactory.decodeStream(in);
            if (bmp != null) {
                imageView.setImageBitmap(bmp);
                saveToInternalStorage(bmp);
                SharedPreferences.Editor editor = config.edit();
                editor.putString("DOWNLOAD", img);
                editor.commit();
            } else {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                loadImageFromStorage(directory.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImageFromStorage(String path) {
        try {
            File f = new File(path, "imgsplash.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "imgsplash.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    public void onBackPressed() {
    }
}
*/
