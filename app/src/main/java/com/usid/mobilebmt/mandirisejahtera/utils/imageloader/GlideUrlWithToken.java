package com.usid.mobilebmt.mandirisejahtera.util.imageloader;

import androidx.core.util.Preconditions;
import android.util.Log;

import com.bumptech.glide.load.model.GlideUrl;

public class GlideUrlWithToken extends GlideUrl {
    private String mSourceUrl;

    public GlideUrlWithToken(String url, String token) {
        super(new StringBuilder(url)
                .append(token) // append the token at the end of url
                .toString());

        Preconditions.checkNotNull(url);
        Preconditions.checkNotNull(token);

        mSourceUrl = url;
        Log.v("image url", url);
    }

    @Override
    public String getCacheKey() {
        return mSourceUrl;
    }

    @Override
    public String toString() {
        return super.getCacheKey();
    }
}