package com.usid.mobilebmt.mandirisejahtera.dashboard.transaksi.pembelian;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;

import java.util.ArrayList;
import java.util.HashMap;

public class DaftarPulsa extends AppCompatActivity {
    private SysDB dbsys;
    private TextView tvKosong;
    private ListView lv;
    ArrayList<String> rekening = new ArrayList<String>();
    ArrayList<String> nama = new ArrayList<String>();
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rekening);
        lv = (ListView) findViewById(R.id.listtem);
        tvKosong = (TextView) findViewById(R.id.kosong);
        dbsys = new SysDB(DaftarPulsa.this);
        dbsys.open();
        dbsys.CreateTablePulsa();
        int jumla = 0;
        Cursor cur1 = dbsys.cekPulsaData();
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();
        if (jumla == 0) {
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            tvKosong.setVisibility(View.GONE);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.custom_row_view, new String[]{"oprt", "nohp"}, new int[]{R.id.text1, R.id.text2});
        lv.setAdapter(adapter);
        list.clear();
        rekening.clear();
        nama.clear();
        Cursor cur = dbsys.daftarPulsa();
        if (cur.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("oprt", "Nama : " + cur.getString(1));
                temp.put("nohp", "No. HP : " + cur.getString(0));
                list.add(temp);
                rekening.add(cur.getString(0));
                nama.add(cur.getString(1));
            } while (cur.moveToNext());
        }
        cur.close();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                String strnorek = rekening.get(position);
                Intent intent = new Intent();
                intent.putExtra("NOHP", strnorek);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                showAlert("KONFIRMASI HAPUS", "Anda yakin untuk menghapus data?\nNo. HP : " + rekening.get(position) + "\nNama : " + nama.get(position) + "\n", rekening.get(position));
                return true;
            }
        });
    }

    private void showAlert(String title, String message, final String rek) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SysDB dbsys = new SysDB(DaftarPulsa.this);
                dbsys.open();
                dbsys.DeletePulsa(rek);
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
}
