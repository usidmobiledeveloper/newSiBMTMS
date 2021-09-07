package com.usid.mobilebmt.mandirisejahtera.dashboard.riwayat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.usid.mobilebmt.mandirisejahtera.R;

import java.util.List;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.disableViews;
import static com.usid.mobilebmt.mandirisejahtera.utils.Utility.enableViews;


public class AdapterRiwayat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static final int TYPE_FOOTER = 2;

    Context context;

    public List<ItemRiwayat> riwayatList;


    public AdapterRiwayat(List<ItemRiwayat> itemList, Context context) {

        riwayatList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat_more, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {
        return riwayatList == null ? 0 : riwayatList.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return riwayatList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTgl, tvKet, tvJml, tvDebet, tvKredit, tvnUm, tvJudul;
        /* public ImageButton ibShow, ibHide;*/
        public ImageView ivTrf, ivPpob, ivDonate;
        public View view;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvnUm = itemView.findViewById(R.id.tv_num);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvTgl = itemView.findViewById(R.id.tv_tgl);
            tvJml = itemView.findViewById(R.id.tv_jml);
            tvDebet = itemView.findViewById(R.id.tv_ppob);
            tvKredit = itemView.findViewById(R.id.tv_transfer);
            tvKet = itemView.findViewById(R.id.tv_ket);
           /* ibShow = itemView.findViewById(R.id.plus);
            ibHide = itemView.findViewById(R.id.minus);*/

            ivTrf = itemView.findViewById(R.id.iv_trf);
            ivPpob = itemView.findViewById(R.id.iv_ppob);
            ivDonate = itemView.findViewById(R.id.iv_donate);

            view = itemView;
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.pbar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(ItemViewHolder holder, int position) {

        ItemRiwayat item = riwayatList.get(position);

        holder.tvnUm.setText(item.getNo());
        holder.tvTgl.setText(item.getTgl());
        holder.tvJml.setText("Rp" + item.getJumlah());
        holder.tvKet.setText(item.getKeterangan());

        String dk = item.getJenis();
        if (dk.equalsIgnoreCase("TRANSFER") || dk.equalsIgnoreCase("TRANSFER BANK")
        ) {
            String produk = item.getProduk();
            produk = produk.substring(0, produk.indexOf("-"));

            holder.tvJudul.setText(item.jenis + " " + produk + "\n");

            enableViews(holder.ivTrf);
            disableViews(holder.ivDonate, holder.ivPpob);
        } else if (dk.equalsIgnoreCase("DONASI")) {
            String produk = item.getProduk();
            produk = produk.substring(0, produk.indexOf("-"));
            holder.tvJudul.setText(produk + "\n");

            enableViews(holder.ivDonate);
            disableViews(holder.ivTrf, holder.ivPpob);
        } else {
            if (item.getKeterangan().contains(" ")) {
                String[] x = item.getKeterangan().split(" ");
                String y = x[0];
                holder.tvJudul.setText(y + " " + item.getProduk() + "\n");
            }

            enableViews(holder.ivPpob);
            disableViews(holder.ivTrf, holder.ivDonate);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, RiwayatViewActivity.class);
                i.putExtra("jenis", item.getJenis());
                i.putExtra("produk", item.getProduk());
                i.putExtra("tujuan", item.getTujuan());
                i.putExtra("trxid", item.getTrxid());

                i.putExtra("faktur", item.getFaktur());
                i.putExtra("tgl", item.getTgl());
                i.putExtra("jumlah", item.getJumlah());
                i.putExtra("keterangan", item.getKeterangan());

                context.startActivity(i);
            }
        });

    }
}