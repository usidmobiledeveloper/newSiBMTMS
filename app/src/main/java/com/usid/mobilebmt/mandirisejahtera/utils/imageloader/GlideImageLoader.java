package com.usid.mobilebmt.mandirisejahtera.utils.imageloader;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yyydjk.library.BannerLayout;

public class GlideImageLoader implements BannerLayout.ImageLoader {
    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path)/*.error(context.getResources().getDrawable(R.drawable.ic_image_empty))*/.centerCrop().into(imageView);
    }
}
