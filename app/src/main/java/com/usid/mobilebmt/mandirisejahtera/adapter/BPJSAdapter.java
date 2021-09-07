package com.usid.mobilebmt.mandirisejahtera.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.usid.mobilebmt.mandirisejahtera.R;

public class BPJSAdapter extends BaseAdapter {
    Context context;
    String kode[];
    String[] nama;
    LayoutInflater inflter;

    public BPJSAdapter(Context applicationContext, String[] kode, String[] nama) {
        this.context = applicationContext;
        this.kode = kode;
        this.nama = nama;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return kode.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_bpjs, null);

        TextView tvKode = view.findViewById(R.id.tv_kode);
        TextView tvNama = view.findViewById(R.id.tv_nama);

        tvKode.setText(kode[i]);
        tvNama.setText(nama[i]);
        return view;
    }
}