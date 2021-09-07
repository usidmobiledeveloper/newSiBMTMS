package com.usid.mobilebmt.mandirisejahtera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.ViewImageActivity;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class InfoActivity2 extends Activity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private View infobiayaView, progressView;
    private TextView mTextError;
    private List<String> img = new ArrayList<>();
    private Button btLanjut;
    SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_2);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        sliderLayout = (SliderLayout) findViewById(R.id.slider);
        mTextError = (TextView) findViewById(R.id.txtError);
        infobiayaView = findViewById(R.id.info_form);
        progressView = findViewById(R.id.info_progress);

        showProgress(true);
        new asyncInfo().execute();

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

    private class asyncInfo extends AsyncTask<String, Void, Boolean> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 29000;
        public static final int CONNECTION_TIMEOUT = 30000;
        public Boolean status = false;
        public String keterangan = "404 Error koneksi terputus!!\nSilahkan coba lagi.", inputLine;

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                URL myUrl = new URL(new NumSky(InfoActivity2.this).decrypt(getResources()
                        .getString(R.string.urlInfoPromo)) + getResources().getString(R.string.appurlnameLower));
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
                    String uris = MyVal.URL_BASE_CONTENT() + new NumSky(InfoActivity2.this).decrypt(getResources().getString(R.string.urlInfoSplash));
                    for (int i = 0; i < arr.length(); i++) {
                        String imga = arr.getString(i);
                        img.add(uris + imga);
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
            showProgress(false);
            if (result) {
                initiateSlider();
                mTextError.setVisibility(View.GONE);
            } else {
                sliderLayout.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), NewMainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }
    }

    private void initiateSlider() {
        for (String i : img) {
            TextSliderView textSliderView = new TextSliderView(InfoActivity2.this);
            textSliderView.image(i).setScaleType(BaseSliderView.ScaleType.FitCenterCrop).setOnSliderClickListener(InfoActivity2.this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", i);

            sliderLayout.addSlider(textSliderView);
            sliderLayout.stopAutoCycle();
        }

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.addOnPageChangeListener(InfoActivity2.this);
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        String path = "" + slider.getBundle().get("extra");

        Intent i = new Intent(InfoActivity2.this, ViewImageActivity.class);
        i.putExtra("path", path);
        startActivity(i);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
    }
}
