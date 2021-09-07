package com.usid.mobilebmt.mandirisejahtera.dashboard.akun;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;
import com.usid.mobilebmt.mandirisejahtera.model.SysDB;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InboxList extends AppCompatActivity {
    private TextView tvInfo;
    private ListView lv1;
    static final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    ArrayList<String> strTgl = new ArrayList<String>();
    ArrayList<String> ket = new ArrayList<String>();

    @BindView(R.id.llbottom)
    LinearLayout llBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_info_mutasi);
        ButterKnife.bind(this);

        llBottom.setVisibility(View.GONE);

        tvInfo = (TextView) findViewById(R.id.tvInfo);
        lv1 = (ListView) findViewById(android.R.id.list);
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long id) {
                Intent intent = new Intent(InboxList.this, Inbox.class);
                intent.putExtra("tgl", strTgl.get(position));
                intent.putExtra("ket", ket.get(position));
                startActivity(intent);
            }
        });
        lv1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                showAlert("KONFIRMASI HAPUS", "Anda yakin untuk menghapus data Inbox?\n" + strTgl.get(pos) + "\n", strTgl.get(pos));
                return true;
            }
        });
        defaultAdapter();
    }

    private void showAlert(String title, String message, final String tgl) {
        new AlertDialog.Builder(this).setIconAttribute(android.R.attr.alertDialogIcon).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("YA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    SysDB dbsys = new SysDB(getApplicationContext());
                    dbsys.open();
                    dbsys.CreateTableSys();
                    dbsys.DeleteDataInbox(tgl);
                    dbsys.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                defaultAdapter();
                dialog.dismiss();
            }
        }).setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();
    }

    private void defaultAdapter() {
        list.clear();
        strTgl.clear();
        ket.clear();
        lv1.setVisibility(View.VISIBLE);
        tvInfo.setVisibility(View.GONE);
        SimpleAdapter adapter = new SimpleAdapter(this, list, R.layout.list_inbox, new String[]{"tgl", "ket"}, new int[]{R.id.text1, R.id.text2});
        try {
            SysDB dbsys = new SysDB(getApplicationContext());
            dbsys.open();
            dbsys.CreateTableSys();
            Cursor cur = dbsys.cekSysData();
            if (cur.moveToFirst()) {
                do {
                    HashMap<String, String> temp = new HashMap<String, String>();
                    temp.put("tgl", cur.getString(0));
                    temp.put("ket", cur.getString(1).replace("\n", " "));
                    list.add(temp);
                    strTgl.add(cur.getString(0));
                    ket.add(cur.getString(1));
                } while (cur.moveToNext());
            } else {
                lv1.setVisibility(View.GONE);
                tvInfo.setVisibility(View.VISIBLE);
                tvInfo.setText("Data Inbox kosong!");
            }
            dbsys.close();
        } catch (Exception e) {
            e.printStackTrace();
            lv1.setVisibility(View.GONE);
            tvInfo.setVisibility(View.VISIBLE);
            tvInfo.setText("Data Inbox kosong!");
        }
        lv1.setAdapter(adapter);
    }
}
