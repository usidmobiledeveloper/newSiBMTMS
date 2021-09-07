package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer;

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

public class DaftarAntarBank extends AppCompatActivity {
    private SysDB dbsys;
    private TextView tvKosong;
    private ListView lv;
    ArrayList<String> kdbank = new ArrayList<String>();
    ArrayList<String> nmkdbank = new ArrayList<String>();
    ArrayList<String> rekening = new ArrayList<String>();
    ArrayList<String> nama = new ArrayList<String>();
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_rekening);
        lv = (ListView) findViewById(R.id.listtem);
        tvKosong = (TextView) findViewById(R.id.kosong);
        dbsys = new SysDB(DaftarAntarBank.this);
        dbsys.open();
        dbsys.CreateTableTransferAB();
        int jumla = 0;
        Cursor cur1 = dbsys.cekTrfDataAB();
        if (cur1.moveToFirst()) {
            jumla = cur1.getInt(0);
        }
        cur1.close();
        if (jumla == 0) {
            tvKosong.setVisibility(View.VISIBLE);
        } else {
            tvKosong.setVisibility(View.GONE);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.custom_row_view_bank, new String[]{"nama", "kdbank", "rekening"}, new int[]{R.id.text1, R.id.text2, R.id.text3});
        lv.setAdapter(adapter);
        list.clear();
        rekening.clear();
        kdbank.clear();
        nmkdbank.clear();
        nama.clear();
        Cursor cur = dbsys.daftarRekeningTabunganAB();
        if (cur.moveToFirst()) {
            do {
                HashMap<String, String> temp = new HashMap<String, String>();
                temp.put("nama", "Nama : " + cur.getString(3));
                temp.put("kdbank", "Bank : " + cur.getString(1));
                temp.put("rekening", "No. Rek. : " + cur.getString(2));
                list.add(temp);
                rekening.add(cur.getString(2));
                nama.add(cur.getString(3));
                kdbank.add(cur.getString(0));
                nmkdbank.add(cur.getString(1));
            } while (cur.moveToNext());
        } cur.close();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                String strkdbank = kdbank.get(position);
                String strnmkdbank = nmkdbank.get(position);
                String strnorek = rekening.get(position);
                Intent intent = new Intent();
                intent.putExtra("KDBANK", strkdbank);
                intent.putExtra("NMKDBANK", strnmkdbank);
                intent.putExtra("REKENING", strnorek);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
                showAlert("KONFIRMASI HAPUS", "Anda yakin untuk menghapus data?\nNo. Rek. : " + nmkdbank.get(position) + " - " + rekening.get(position) + "\nNama : " + nama.get(position) + "\n", kdbank.get(position), rekening.get(position));
                return true;
            }
        });
    }

    private void showAlert(String title, String message, final String kdbank, final String rek) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SysDB dbsys = new SysDB(DaftarAntarBank.this);
                dbsys.open();
                dbsys.DeleteDataAB(kdbank, rek);
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
