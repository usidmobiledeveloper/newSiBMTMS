package com.usid.mobilebmt.mandirisejahtera.utils.banner;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.library.banner.BannerLayout;
import com.usid.mobilebmt.mandirisejahtera.R;

import java.util.List;

import static com.usid.mobilebmt.mandirisejahtera.utils.Utility2.getPrefsAuthToken;

public class WebBannerAdapter extends RecyclerView.Adapter<WebBannerAdapter.MzViewHolder> {

    private Context context;
    private List<String> urlList;
    private BannerLayout.OnBannerItemClickListener onBannerItemClickListener;

    public WebBannerAdapter(Context context, List<String> urlList) {
        this.context = context;
        this.urlList = urlList;
    }

    public void setOnBannerItemClickListener(BannerLayout.OnBannerItemClickListener onBannerItemClickListener) {
        this.onBannerItemClickListener = onBannerItemClickListener;
    }

    @Override
    public WebBannerAdapter.MzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MzViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false));
    }

    @Override
    public void onBindViewHolder(WebBannerAdapter.MzViewHolder holder, final int position) {
        if (urlList == null || urlList.isEmpty())
            return;
        final int P = position % urlList.size();
        String url = urlList.get(P);
        ImageView img = (ImageView) holder.imageView;

        Glide.with(context).load(url).into(img);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerItemClickListener != null) {
                    onBannerItemClickListener.onItemClick(P);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        if (urlList != null) {
            return urlList.size();
        }
        return 0;
    }


    class MzViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        MzViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}