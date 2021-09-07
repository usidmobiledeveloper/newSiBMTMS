package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.dashboard.AkunFragment;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityAkunDetailBinding;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class AkunDetailActivity extends AppCompatActivity {

    ActivityAkunDetailBinding binding;
    String imsi = "", nocard = "";
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAkunDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + getString(R.string.path2)+"data";

        config = getSharedPreferences("config", 0);

        SnHp telpMan = new SnHp(this);
        if (5 != telpMan.telephonyManager().getSimState()) {
            imsi = "TIDAK ADA KARTU";
        } else {
            imsi = Utility.getIMSIRead(this);
        }
        NumSky nmsk = new NumSky(this);
        try {
            nocard = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new LoadImage().execute();

    }

    String dirPath = "";
    String fileName = "fp_mobilebmt.jpg";

    class LoadImage extends AsyncTask<Uri, Void, Void> {

        @Override
        protected void onPreExecute() {

            enableViews(binding.pbar);
            disableViews(binding.scrAkundetail);

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
                        binding.imgProfile.setImageBitmap(myBitmap);
                    }

                }
            });
            return null;
        }

        protected void onPostExecute(Void result) {

            new AsyncVA().execute();
            // here
        }
    }

    private class AsyncVA extends AsyncTask<Void, Void, Void> {
        private Boolean getVA = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        String nama = "", alamat = "", rekening = "", telepon = "";
        String kodebmt = "";
        long novaint;
        String novastr = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(AkunDetailActivity.this).decrypt(getResources().getString(R.string.urlGetVA)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(nocard), Utility.md5(imsi));

                Log.d("AYIK", "va-strcek " + strCek);

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
                getVA = (Boolean) jsonObject.get("status");
                ket = (String) jsonObject.get("keterangan");

                telepon = (String) jsonObject.get("telepon");
                nama = (String) jsonObject.get("nama");
                alamat = (String) jsonObject.get("alamat");
                rekening = (String) jsonObject.get("rekening");
                novaint = (long) jsonObject.get("nova");
                novastr = String.format("%06d", novaint);

               /* jwtpub = (String) jsonObject.get("jwt");
                Utility2.setPrefsAuthToken(jwtpub);*/

            } catch (Exception ex) {
                ex.printStackTrace();
                getVA = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            disableViews(binding.pbar);
            enableViews(binding.scrAkundetail);

            if (getVA) {
                kodebmt = getResources().getString(R.string.kodebmt);
                String finalnova = "888080" + kodebmt + rekening.substring(0, 3) + novastr;

                binding.edNorek.setText(nama);
                binding.edNova.setText(finalnova);
                binding.edNama.setText(nama);
                binding.edAlamat.setText(alamat);
                binding.edTelp.setText(telepon);

            } else {
                String msga = "#" + ket + "\n";
                Toast.makeText(AkunDetailActivity.this, msga, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String toJsonString(String nokartu, String imsi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);

        return obj.toString();
    }
}