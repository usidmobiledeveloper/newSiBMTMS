package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasipembiayaan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.usid.mobilebmt.mandirisejahtera.R;

import java.util.ArrayList;
import java.util.List;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;


public class PembiayaanAdapter extends RecyclerView.Adapter<PembiayaanAdapter.MyViewHolder> {
    List<Pembiayaan> list = new ArrayList<>();
    Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvTgl, tvFaktur, tvTotal;
        public TextView tvPokok, tvMargin/*, tvDenda*/;
        public TextView tvKet;

        public LinearLayout llBottom, llMutasi;
        public ImageButton ibShow, ibHide;
        View mainView;

        public MyViewHolder(View v) {
            super(v);
            tvTgl = v.findViewById(R.id.tv_tgl);
            tvFaktur = v.findViewById(R.id.tv_faktur);
            tvTotal = v.findViewById(R.id.tv_total);

            tvPokok = v.findViewById(R.id.tv_pokok);
            tvMargin = v.findViewById(R.id.tv_margin);
            //tvDenda = v.findViewById(R.id.tv_denda);
            tvKet = v.findViewById(R.id.tv_ket);

            llBottom = v.findViewById(R.id.ll_bottom);
            llMutasi = v.findViewById(R.id.ll_mutasi_pembiayaan);

            ibShow = v.findViewById(R.id.plus);
            ibHide = v.findViewById(R.id.minus);

            mainView = v;

        }
    }

    public PembiayaanAdapter(Context context, List<Pembiayaan> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mutasi_pembiayaan, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Pembiayaan pembiayaan = list.get(position);

        holder.tvTgl.setText(pembiayaan.getTgl());
        holder.tvFaktur.setText(pembiayaan.getFaktur());
        holder.tvTotal.setText("Rp. " + pembiayaan.getTotal());

        holder.tvPokok.setText("Rp. " + pembiayaan.getPokok());
        holder.tvMargin.setText("Rp. " + pembiayaan.getMargin());
        //holder.tvDenda.setText("Rp. " + pembiayaan.getDenda());

        holder.tvKet.setText(pembiayaan.getKeterangan());

        if (position % 2 == 1) {
            holder.mainView.setBackgroundColor(Color.parseColor("#FFD1F8E2"));
        } else {
            holder.mainView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.ibShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                enableViews(holder.ibHide, holder.llBottom);
                disableViews(holder.ibShow);

            }
        });


        holder.ibHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableViews(holder.ibShow);
                disableViews(holder.ibHide, holder.llBottom);

            }
        });

        final boolean[] isClick = {false};

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.llBottom.isShown()) {
                    enableViews(holder.ibShow);
                    disableViews(holder.ibHide, holder.llBottom);
                } else {
                    enableViews(holder.ibHide, holder.llBottom);
                    disableViews(holder.ibShow);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
