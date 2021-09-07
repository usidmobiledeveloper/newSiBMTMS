package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.databinding.ActivityDaftarRekeningBinding;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;
import com.usid.mobilebmt.mandirisejahtera.utils.NumSky;
import com.usid.mobilebmt.mandirisejahtera.utils.SnHp;
import com.usid.mobilebmt.mandirisejahtera.utils.Utility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

public class DaftarRekening extends AppCompatActivity {
    private SysDB dbsys;
    private TextView tvKosong;
    private ListView lv;
    ArrayList<String> rekening = new ArrayList<String>();
    ArrayList<String> nama = new ArrayList<String>();
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    ProgressDialog mProgressDialog;
    String strNokartu = "", strImsi = "", trxID = "";
    private SharedPreferences config;
    ActivityDaftarRekeningBinding binding;
    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_daftar_rekening);
        binding = ActivityDaftarRekeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mProgressDialog = new ProgressDialog(DaftarRekening.this);

        Intent intent = getIntent();
        if (intent != null)
            data = intent.getStringExtra("datas");

        lv = (ListView) findViewById(R.id.listtem);
        tvKosong = (TextView) findViewById(R.id.kosong);
        dbsys = new SysDB(DaftarRekening.this);
        dbsys.open();
        dbsys.CreateTableTransfer();
        int jumla = 0;
        Cursor cur1 = dbsys.cekTrfData();
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();

        if (jumla == 0) {
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            tvKosong.setVisibility(View.GONE);
        }


        list.clear();
        rekening.clear();
        nama.clear();
        Cursor cur = dbsys.daftarRekeningTabungan();
        if (cur.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("nama", "Nama : " + cur.getString(1).toUpperCase());
                temp.put("rekening", "No. Rek. : " + cur.getString(0));

                list.add(temp);
                rekening.add(cur.getString(0));
                nama.add(cur.getString(1).toUpperCase());
            } while (cur.moveToNext());

            initAdapter();
        }
        cur.close();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                String strnorek = "";
                //strnorek = rekening.get(position);

                if (isFilter && filteredList != null) {
                    strnorek = filteredList.get(+position).get("rekening");

                } else {
                    strnorek = uniqueList.get(+position).get("rekening");

                }

                Intent intent = new Intent();
                intent.putExtra("REKENING", strnorek);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        config = getApplicationContext().getSharedPreferences("config", 0);
        SnHp telpMan = new SnHp(getApplicationContext());
        if (5 != telpMan.telephonyManager().getSimState()) {
            strImsi = "TIDAK ADA KARTU";
        } else {
            strImsi = Utility.getIMSIRead(DaftarRekening.this);

        }
        NumSky nmsk = new NumSky(getApplicationContext());
        try {
            strNokartu = nmsk.decrypt(config.getString("3D0k", ""));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (!strImsi.equals("TIDAK ADA KARTU")) {
            new AsyncGetNorek().execute();
        }


    }

    private void showAlert(String title, String message, final String rek) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SysDB dbsys = new SysDB(DaftarRekening.this);
                dbsys.open();
                dbsys.DeleteData(rek);
                dbsys.close();
                finish();
                dialog.dismiss();
            }
        }).setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbsys.close();
    }

    JSONArray datas = null;

    private class AsyncGetNorek extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                JSONParser parser = new JSONParser();
                if (data != null) {
                    Object objects = parser.parse(data);
                    datas = (JSONArray) objects;
                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (datas != null)
                initAutoComplete("");
        }
    }

    private void initAutoComplete(String s) {
        String namanorek = "";
        for (int i = 0; i < datas.size(); i++) {
            JSONObject objx = (JSONObject) datas.get(i);
            namanorek = (String) objx.get("nama");
            assert namanorek != null;

            namanorek = namanorek.replace("[", "# ").replace("]", "");
            String[] x = namanorek.split(" # ");
            String namax = x[0];
            String norekx = x[1];

            HashMap<String, String> temp = new HashMap<String, String>();
            temp.put("nama", "Nama : " + namax.toUpperCase());
            temp.put("rekening", "No. Rek. : " + norekx);
            list.add(temp);
            rekening.add(norekx);
            nama.add(namax);

        }

        initAdapter();
    }


    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();
    ArrayList<HashMap<String, String>> uniqueList = new ArrayList<>();
    boolean isFilter = false;

    private void initAdapter() {

        for (HashMap<String, String> prod : list) {
            HashMap<String, String> foundProd = null;
            for (HashMap<String, String> uniqueProd : uniqueList) {
                if (uniqueProd.get("rekening").equals(prod.get("rekening"))) {
                    foundProd = uniqueProd;
                    break;
                }
            }
            if (foundProd == null) {
                uniqueList.add(prod);
            } else {
                // Do something if the product already existed (maybe update qty)
            }
        }

        if (uniqueList.size() <= 0) {
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            tvKosong.setVisibility(View.GONE);
        }

        adapter = new SimpleAdapter(this, uniqueList, R.layout.custom_row_view, new String[]{"nama", "rekening"}, new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        binding.edSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                isFilter = true;

                query = query.toString().toUpperCase();
                filteredList = new ArrayList<HashMap<String, String>>();

                for (HashMap<String, String> model : uniqueList) {
                    String text1 = model.get("nama");
                    String text2 = model.get("rekening");

                    if (text1.replace("Nama : ", "").contains(query) ||
                            text2.replace("No. Rek. : ", "").contains(query)) {

                        model.put("nama", text1);
                        model.put("rekening", text2);
                        filteredList.add(model);
                    }
                }

                adapter = new SimpleAdapter(DaftarRekening.this, filteredList, R.layout.custom_row_view, new String[]{"nama", "rekening"}, new int[]{R.id.text1, R.id.text2});
                lv.setAdapter(adapter);

                adapter.notifyDataSetChanged();

            }
        });
    }

}