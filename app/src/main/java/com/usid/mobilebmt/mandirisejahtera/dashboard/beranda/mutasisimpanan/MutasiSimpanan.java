package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasisimpanan;

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


public class MutasiSimpanan extends AppCompatActivity {

    private ActivityMutasiPembiayaanBinding binding;
    private String strNorek = "", strNokartu = "", strImsi = "";
    private int jmltrx = 0;
    private ProgressDialog mProgressDialog;
    private SharedPreferences config;

    List<Simpanan> list = new ArrayList<>();
    private SimpananAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    String start = "", end = "";

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

            start = intent.getStringExtra("tglmulai");
            end = intent.getStringExtra("tglsampai");
        }

        mProgressDialog = new ProgressDialog(MutasiSimpanan.this);

        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
            strImsi = Utility.getIMSIRead(MutasiSimpanan.this);

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

        mAdapter = new SimpananAdapter(this, list);
        binding.rvMutasipembiayaan.setAdapter(mAdapter);

        if (strImsi.equals("TIDAK ADA KARTU")) {
            showExt("ERROR", "Masukkan SIM CARD!");
        } else {
            new AsyncGetMUtasi().execute();
        }

    }

    private class AsyncGetMUtasi extends AsyncTask<Void, Void, Void> {
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
                URL obj = new URL(MyVal.URL_BASE() + new NumSky(MutasiSimpanan.this).decrypt(getResources().getString(R.string.urlCekMutasiSimpananPeriod)));
                //obj = new URL("http://192.168.1.88:8080/bmtservice2/inforekening/cekmutasipembiayaan");

                HttpURLConnection conJ = (HttpURLConnection) obj.openConnection();
                conJ.setRequestMethod("POST");
                conJ.setRequestProperty("Content-Type", "application/json");
                conJ.setRequestProperty("Authorization", getPrefsAuthToken());

                /*strNokartu = "1234123412341234";
                strImsi = "8962101075137022391";*/

                String strCek = toJSONString(Utility.md5(strNokartu), Utility.md5(strImsi), strNorek, jmltrx, getResources().getString(R.string.kodebmt));

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

                //Log.d("AYIK", "response simpanan "+ response.toString());

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

                        Simpanan simpanan = new Simpanan();
                        simpanan.setFaktur(faktur);
                        simpanan.setTgl(tgl);
                        simpanan.setKeterangan(keterangan);
                        simpanan.setPokok(pokok);
                        simpanan.setMargin(margin);
                        simpanan.setDenda(denda);
                        simpanan.setTotal(total);

                        list.add(simpanan);

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

    private String toJSONString(String nokartu, String imsi, String rekening, int jmltrx, String kodebmt) {
        JSONObject obj = new JSONObject();
        obj.put("nokartu", nokartu);
        obj.put("imsi", imsi);
        obj.put("kodebmt", kodebmt);
        obj.put("rekening", rekening);
        obj.put("jmltransaksi", String.valueOf(jmltrx));

        obj.put("kodebmt", getString(R.string.kodebmt));
        obj.put("tglmulai", start);
        obj.put("tglsampai", end);

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
                        if (!Utility2.errorImsi(message)) {
                            finish();
                        } else {
                            Toast.makeText(MutasiSimpanan.this, "SILAHKAN RE-AKTIVASI", Toast.LENGTH_SHORT).show();

                            Utility2.reAktivasi(MutasiSimpanan.this);
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