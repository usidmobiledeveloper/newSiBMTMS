package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasipembiayaan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityMutasiPembiayaanBinding;
import com.usid.mobilebmt.mandirisejahtera.utils.MyVal;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility2;

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
import java.util.List;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;


public class MutasiPembiayaan extends AppCompatActivity {

    private ActivityMutasiPembiayaanBinding binding;
    private String strNorek = "", strNokartu = "", strImsi = "";
    private int jmltrx = 0;
    private ProgressDialog mProgressDialog;
    private SharedPreferences config;

    //private String sampleResponse = SAMPLE_RESPONSE;

    List<Pembiayaan> list = new ArrayList<>();
    private PembiayaanAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMutasiPembiayaanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (intent != null) {
            strNorek = intent.getStringExtra("norek");
            jmltrx = intent.getIntExtra("jmltrx", 0);
        }

        mProgressDialog = new ProgressDialog(MutasiPembiayaan.this);

        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
            strImsi = Utility.getIMSIRead(MutasiPembiayaan.this);

        }
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        binding.rvMutasipembiayaan.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        binding.rvMutasipembiayaan.setLayoutManager(layoutManager);

        mAdapter = new PembiayaanAdapter(this, list);
        binding.rvMutasipembiayaan.setAdapter(mAdapter);

        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncGetMutasiPembiayaan().execute();
        }

    }

    private class AsyncGetMutasiPembiayaan extends AsyncTask<Void, Void, Void> {
        private Boolean isGetMutasiPembiayaan = false;
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setTitle("Memproses");
            mProgressDialog.setMessage("Tunggu...");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL obj = new URL(MyVal.URL_BASE() +new NumSky(MutasiPembiayaan.this).decrypt(getResources().getString(R.string.urlCekMutasiPembiayaan)));

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());

                String strCek = toJSONString(Utility.md5(strNokartu), Utility.md5(strImsi), strNorek, jmltrx);

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
                isGetMutasiPembiayaan = (Boolean) jsonObject.get("status");

                if (isGetMutasiPembiayaan) {
                    JSONArray datas = (JSONArray) jsonObject.get("data");

                    String faktur = "", tgl = "", keterangan = "", pokok = "0", margin = "0", denda = "0", total = "0";

                    for (int i = 0; i < (datas != null ? datas.size() : 0); i++) {
                        JSONObject objx = (JSONObject) datas.get(i);

                        faktur = (String) objx.get("faktur");
                        tgl = (String) objx.get("tgl");
                        keterangan = (String) objx.get("keterangan");
                        pokok = (String) objx.get("dk");
                        margin = (String) objx.get("trxid");
                        denda = (String) objx.get("jenis");
                        total = (String) objx.get("jumlah");

                        Pembiayaan pembiayaan = new Pembiayaan();
                        pembiayaan.setFaktur(faktur);
                        pembiayaan.setTgl(tgl);
                        pembiayaan.setKeterangan(keterangan);
                        pembiayaan.setPokok(pokok);
                        pembiayaan.setMargin(margin);
                        pembiayaan.setDenda(denda);
                        pembiayaan.setTotal(total);

                        list.add(pembiayaan);

                    }

                } else ket = (String) jsonObject.get("keterangan");


            } catch (Exception ex) {
                ex.printStackTrace();
                isGetMutasiPembiayaan = false;

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

            if (isGetMutasiPembiayaan) {
                mAdapter.notifyDataSetChanged();
            } else {
                String msga = "#" + ket + "\n";
                showExt("GAGAL", msga);
            }
        }
    }

    private String toJSONString(String nokartu, String imsi, String rekening, int jmltrx) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("rekening", rekening);
        obj.put("jmltransaksi", String.valueOf(jmltrx));

        return obj.toString();
    }

    private void showExt(String title, String message) {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!Utility2.errorImsi(message)){
                            finish();
                        }else {
                            Toast.makeText(MutasiPembiayaan.this, "SILAHKAN RE-AKTIVASI", Toast.LENGTH_SHORT).show();

                            Utility2.reAktivasi(MutasiPembiayaan.this);
                            finish();
                        }

                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}