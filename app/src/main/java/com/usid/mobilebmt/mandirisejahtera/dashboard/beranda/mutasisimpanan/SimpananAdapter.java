package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.mutasisimpanan;

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


public class SimpananAdapter extends RecyclerView.Adapter<SimpananAdapter.MyViewHolder> {
    List<Simpanan> list = new ArrayList<>();
    Context context;


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tvTgl, tvFaktur, tvTotal;
        public TextView tvPokok, tvMargin/*, tvDenda*/, tvDebit, tvKredit;
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

            tvDebit = v.findViewById(R.id.tv_debit);
            tvKredit = v.findViewById(R.id.tv_kredit);

            llBottom = v.findViewById(R.id.ll_bottom);
            llMutasi = v.findViewById(R.id.ll_mutasi_pembiayaan);

            ibShow = v.findViewById(R.id.plus);
            ibHide = v.findViewById(R.id.minus);

            mainView = v;

        }
    }

    public SimpananAdapter(Context context, List<Simpanan> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_mutasi_simpanan, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Simpanan simpanan = list.get(position);

        holder.tvTgl.setText(simpanan.getTgl());
        holder.tvFaktur.setText(simpanan.getFaktur());
        holder.tvTotal.setText("Rp. " + simpanan.getTotal());

        //holder.tvPokok.setText("Rp. " + simpanan.getPokok());
        holder.tvMargin.setText("Rp. " + simpanan.getMargin());

        holder.tvKet.setText(simpanan.getKeterangan());

        if (position % 2 == 1) {
            holder.mainView.setBackgroundColor(Color.parseColor("#FFD1F8E2"));
        } else {
            holder.mainView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        if (simpanan.getPokok().equalsIgnoreCase("D")) {
            holder.tvDebit.setVisibility(View.VISIBLE);
            holder.tvKredit.setVisibility(View.GONE);
            holder.tvTotal.setTextColor(Color.parseColor("#990000"));
        } else {
            holder.tvDebit.setVisibility(View.GONE);
            holder.tvKredit.setVisibility(View.VISIBLE);
            holder.tvTotal.setTextColor(Color.parseColor("#00923F"));
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
