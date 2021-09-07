package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.content;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.utils.NumberTextWatcher;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class KalkulatorZakat extends AppCompatActivity {
    private EditText edUang, edInvestasi, edHutang, edKebPokok, edHargaEmas;
    private String hargaemas, uang, investasi, hutang, kebpokok;
    private Button btnCek, btnHitung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_kalkulator_zakat);
        edHargaEmas = (EditText) findViewById(R.id.edHargaEmas);
        edUang = (EditText) findViewById(R.id.edUangTunai);
        edHutang = (EditText) findViewById(R.id.edHutang);
        edKebPokok = (EditText) findViewById(R.id.edKebutuhan);
        edInvestasi = (EditText) findViewById(R.id.edInvestasi);
        btnCek = (Button) findViewById(R.id.buttonCek);
        btnHitung = (Button) findViewById(R.id.btnHitung);
        edHargaEmas.addTextChangedListener(new NumberTextWatcher(edHargaEmas));
        edUang.addTextChangedListener(new NumberTextWatcher(edUang));
        edHutang.addTextChangedListener(new NumberTextWatcher(edHutang));
        edKebPokok.addTextChangedListener(new NumberTextWatcher(edKebPokok));
        edInvestasi.addTextChangedListener(new NumberTextWatcher(edInvestasi));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        hargaemas = prefs.getString("emas", "");
        edHargaEmas.setText(hargaemas);
        btnHitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cekStrNominal();
                if (uang.equals("") || hargaemas.equals("") || investasi.equals("") || kebpokok.equals("") || hutang.equals(""))
                    showAlert("Perhatian", "Nominalnya jangan dikosongi isi saja dengan angka 0 (nol)");
                else {
                    long totalutang = 0, totalkekayaan = 0, jml_harta = 0, hargaemasdunia = 0, total_zakat = 0;
                    totalutang = Long.parseLong(hutang) + Long.parseLong(kebpokok);
                    totalkekayaan = Long.parseLong(uang) + Long.parseLong(investasi);
                    jml_harta = totalkekayaan - totalutang;
                    hargaemasdunia = 85 * Long.parseLong(hargaemas);
                    if (hargaemasdunia > jml_harta) {
                        showAlert("Hasil perhitungan", "Mohon maaf anda tidak termasuk wajib zakat!");
                    } else {
                        total_zakat = jml_harta * 25 / 1000;
                        showAlert("Hasil perhitungan", "Total zakat yang harus dibayarkan adalah sebesar Rp. " + Utility.DoubleToCurrency(total_zakat));
                    }
                }
            }
        });
        btnCek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncInquiryNew().execute();
            }
        });
    }

    private void cekStrNominal() {
        if (edHargaEmas.getText().toString().indexOf(",") != -1) hargaemas = edHargaEmas.getText().toString().replace(",", "");
        else hargaemas = edHargaEmas.getText().toString().replace(".", "");
        if (edUang.getText().toString().indexOf(",") != -1) uang = edUang.getText().toString().replace(",", "");
        else uang = edUang.getText().toString().replace(".", "");
        if (edHutang.getText().toString().indexOf(",") != -1) hutang = edHutang.getText().toString().replace(",", "");
        else hutang = edHutang.getText().toString().replace(".", "");
        if (edKebPokok.getText().toString().indexOf(",") != -1) kebpokok = edKebPokok.getText().toString().replace(",", "");
        else kebpokok = edKebPokok.getText().toString().replace(".", "");
        if (edInvestasi.getText().toString().indexOf(",") != -1) investasi = edInvestasi.getText().toString().replace(",", "");
        else investasi = edInvestasi.getText().toString().replace(".", "");
    }

    private class AsyncInquiryNew extends AsyncTask<Void, Void, String> {
        ProgressDialog pdLoading = new ProgressDialog(KalkulatorZakat.this);
        private String ket = "404 Error koneksi terputus!!\nSilahkan coba lagi", results = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.setIndeterminate(false);
            pdLoading.setTitle("Memproses");
            pdLoading.setMessage("Tunggu...");
            pdLoading.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                String uri = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.xchange%20where%20pair%20in%20(%22XAUIDR%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";
                URL obj = new URL(uri);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");
                con.setConnectTimeout(30000);
                con.setReadTimeout(29000);
                ket = con.getResponseCode() + " " + con.getResponseMessage();
                BufferedReader in;
                if (con.getResponseCode() == 200) in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                else in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine + "\n");
                }
                in.close();
                JSONParser parser = new JSONParser();
                Object objects = parser.parse(response.toString());
                JSONObject jsonObject = (JSONObject) objects;
                if (con.getResponseCode() == 200) {
                    JSONObject jsquery = (JSONObject) jsonObject.get("query");
                    JSONObject jsresults = (JSONObject) jsquery.get("results");
                    JSONObject jsrate = (JSONObject) jsresults.get("rate");
                    results = (String) jsrate.get("Rate");
                    ket = "sukses";
                } else if (con.getResponseCode() == 400) {
                    JSONObject jsresults = (JSONObject) jsonObject.get("error");
                    ket = ket + "\n" + jsresults.get("description");
                } else {
                    ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                ket = "405 Error Exception, koneksi terputus!!\nSilahkan coba lagi.";
            }
            return results;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pdLoading.isShowing()) pdLoading.dismiss();
            if (ket.equals("sukses")) {
                Double harga = Double.parseDouble(result);
                harga = harga / 28.3495;
                hargaemas = Double.toString(harga);
                hargaemas = hargaemas.substring(0, hargaemas.indexOf("."));
                edHargaEmas.setText(hargaemas);
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(KalkulatorZakat.this).edit();
                editor.putString("emas", hargaemas);
                editor.commit();
                showAlert("Sukses cek", "Harga emas dunia saat ini Rp. " + Utility.DoubleToCurrency(Long.parseLong(hargaemas)));
            } else {
                showAlert("ERROR", ket);
                Toast.makeText(getApplicationContext(), "Gagal memperbarui harga emas dunia...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }
}
