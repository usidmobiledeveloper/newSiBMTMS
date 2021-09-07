package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import java.io.InputStream;
import java.net.URL;

public class InfoBiaya extends AppCompatActivity {
    private Bitmap bmp;
    private ImageView imageView;

    private View infobiayaView, progressView;
    private TextView mTextError;
    private SharedPreferences config;
    private String ket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_info_biaya);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        config = getSharedPreferences("config", 0);
        ket = config.getString("IMAGE", "");

        imageView = (ImageView) findViewById(R.id.imgInfo);

        mTextError = (TextView) findViewById(R.id.txtError);
        infobiayaView = findViewById(R.id.info_form);
        progressView = findViewById(R.id.info_progress);

        showProgress(true);
        new AsyncInfoBiaya().execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            infobiayaView.setVisibility(show ? View.GONE : View.VISIBLE);
            infobiayaView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    infobiayaView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            infobiayaView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class AsyncInfoBiaya extends AsyncTask<Void, Void, Void> {
        //ProgressDialog pdLoading = new ProgressDialog(InfoBiaya.this);
        Boolean stats = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*pdLoading.setCancelable(true);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                InputStream in = new URL(MyVal.URL_BASE_CONTENT() + new NumSky(InfoBiaya.this).decrypt(getResources().getString(R.string.urlInfoSplash)) + ket).openStream();

                bmp = BitmapFactory.decodeStream(in);
                stats = true;
            } catch (Exception ex) {
                stats = false;
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            //pdLoading.dismiss();
            showProgress(false);
            if (stats) {
                if (bmp != null) {
                    imageView.setImageBitmap(bmp);
                }
            }
        }
    }
}
