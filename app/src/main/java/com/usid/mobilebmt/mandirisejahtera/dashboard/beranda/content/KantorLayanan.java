package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class KantorLayanan extends AppCompatActivity {
    private ListView lv1;
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    String strNokartu, strImsi;
    private SharedPreferences config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        config = getApplicationContext().getSharedPreferences("config", 0);

        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
           /* try {
                strImsi = telpMan.telephonyManager().getSimSerialNumber();
            } catch (Exception e) {
            }*/
            strImsi = Utility.getIMSIRead(this);
        }

        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        }

        setContentView(R.layout.activity_kantor_layanan);
        lv1 = (ListView) findViewById(android.R.id.list);
        new AsyncKantorLayanan().execute();
    }

    private void defaultAdapter(String datas) {
        list.clear();
        lv1.setVisibility(View.VISIBLE);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.custom_row_kantor, new String[]{"kantor", "alamat"}, new int[]{R.id.text1, R.id.text2});
        try {
            JSONParser parser = new JSONParser();
            Object objects = parser.parse(datas);
            JSONObject jsonObject = (JSONObject) objects;
            JSONArray jsarrys = (JSONArray) jsonObject.get("data");
            int lghtjasrr = jsarrys.size();
            for (int i = 0; i < lghtjasrr; i++) {
                JSONObject jobj = (JSONObject) jsarrys.get(i);
                String alamat = (String) jobj.get("alamat");
                String nama = (String) jobj.get("nama");
                String status = (String) jobj.get("status");
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("kantor", nama.toUpperCase() + " (" + status.toUpperCase() + ")");
                temp.put("alamat", alamat);
                list.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lv1.setAdapter(adapter);
    }

    private class AsyncKantorLayanan extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(KantorLayanan.this);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", data = "";
        Boolean stats = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(true);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {


                URL obj = new URL(MyVal.URL_BASE() + new NumSky(KantorLayanan.this).decrypt(getResources().getString(R.string.urlKantorLayanan)));
                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());
                String strCek = toJsonString(Utility.md5(strNokartu), Utility.md5(strImsi));

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
                data = jsonObject.toJSONString();
                stats = (Boolean) jsonObject.get("status");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
            if (stats) {
                defaultAdapter(data);
            } else {
                String msga = "#" + ket + "\n";
                showAlert("GAGAL Mendapatkan Data", msga);
            }
        }
    }

    private String toJsonString(String nokartu, String imsi) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        return obj.toString();
    }

    private void showAlert(String title, String message) {
        new android.app.AlertDialog.Builder(KantorLayanan.this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.dismiss();
            }
        }).show();
    }
}
