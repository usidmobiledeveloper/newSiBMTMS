package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AHMAD AYIK RIFAI on 8/28/2017.
 */
public class ViewImageActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = ViewImageActivity.class.getSimpleName();
    private ImageView imgPreview;
    private String path, namepath;
    private int pos;
    GestureDetector mGestureDetector;
    private List<String> imgPath = new ArrayList<>();
    ProgressBar pbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        setContentView(R.layout.activity_image_view);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent i = getIntent();
        path = i.getStringExtra("path");
        pos = i.getIntExtra("position", 0);

        String[] bits = path.split("/");
        namepath = bits[bits.length - 1];

        imgPreview = findViewById(R.id.imgViewFull);
        pbar = findViewById(R.id.pbar);
        ImageButton ibclose = findViewById(R.id.ib_close);
        ibclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       /* BitmapDrawable bmd = (BitmapDrawable) getDrawableFromUrl(path);
        Bitmap bitmap = bmd.getBitmap();
        imgPreview.setImageBitmap(bitmap);*/

        //new asyncPromo().execute();
        loadDetail();

    }

    public Drawable getDrawableFromUrl(String imgUrl) {
        if (imgUrl == null || imgUrl.equals("")) return null;
        try {
            URL url = new URL(imgUrl);
            InputStream in = url.openStream();
            Drawable d = Drawable.createFromStream(in, imgUrl);
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private class asyncPromo extends AsyncTask<String, Void, Boolean> {
        private static final String REQUEST_METHOD = "GET";
        private static final int READ_TIMEOUT = 29000;
        private static final int CONNECTION_TIMEOUT = 30000;
        private Boolean status = false;
        private String keterangan = "404 Error koneksi terputus!!\nSilahkan coba lagi.", inputLine;
        String uris = "";

        @Override
        protected Boolean doInBackground(String... params) {

            try {

                URL myUrl = new URL(MyVal.URL_BASE() + new NumSky(ViewImageActivity.this).decrypt(getResources().getString(R.string.urlInfoPromo))
                        + getResources().getString(R.string.appurlnameLower));

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();
                keterangan = connection.getResponseCode() + " " + connection.getResponseMessage();

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();

                JSONObject obj = new JSONObject(stringBuilder.toString());
                status = obj.getBoolean("status");

                if (status) {
                    keterangan = obj.getString("keterangan");
                    JSONArray arr = (JSONArray) obj.get("keterangan");
                    uris = MyVal.URL_BASE_CONTENT() + new NumSky(ViewImageActivity.this).decrypt(getResources().getString(R.string.urlInfoSplash));
                    for (int i = 0; i < arr.length(); i++) {
                        String imga = arr.getString(i);
                        imgPath.add(uris + imga);

                    }
                } else keterangan = obj.getString("keterangan");
            } catch (IOException e) {
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();

            }
            return status;
        }

        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            pbar.setVisibility(View.GONE);

            if (result) {
                try {
                   /* Collections.sort(imgPath, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });*/

                    String urlPath = /*imgPath.get(pos);*/"";

                    namepath = namepath.replace("banner", "promo");
                    urlPath = uris + namepath;

                    Glide.with(ViewImageActivity.this)
                            .load(urlPath)
                            .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter())
                            .into(imgPreview);
                } catch (Exception e) {
                    Toast.makeText(ViewImageActivity.this, "Tidak ada gambar detail", Toast.LENGTH_SHORT).show();
                }


               /* initiateSlider();
                mTextError.setVisibility(View.GONE);*/
            } else {
               /* sliderLayout.setVisibility(View.GONE);
                mTextError.setText("Gagal, " + keterangan);*/
                Toast.makeText(ViewImageActivity.this, "Gagal, " + keterangan, Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    private void loadDetail() {
        String urlPath = "";
        String uris = null;
        try {
            uris = MyVal.URL_BASE_CONTENT() + new NumSky(ViewImageActivity.this).decrypt(getResources().getString(R.string.urlInfoSplash));

            namepath = namepath.replace("banner", "promo");
            urlPath = uris + namepath;
            Glide.with(ViewImageActivity.this)
                    .load(urlPath)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter())
                    .into(imgPreview);

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}