package com.usid.mobilebmt.mandirisejahtera.notifications;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.usid.mobilebmt.mandirisejahtera.R;

import static com.usid.mobilebmt.mandirisejahtera.R.id.txtTime;
import static com.usid.mobilebmt.mandirisejahtera.R.id.txtTitle;

/**
 * Created by AHMAD AYIK RIFAI on 3/7/2017.
 */

public class NotificationsAdapter extends BaseAdapter {

    private final Context mContext;
    private final SparseBooleanArray mCollapsedStatus;
    private final String[] datas;


    public NotificationsAdapter(Context context, String[] data) {
        mContext = context;
        datas = data;
        mCollapsedStatus = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        String[] dataArr = datas[position].split("#");
        String title = dataArr[1].toUpperCase();
        String message = dataArr[2];
        String time = dataArr[0];

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_notifications, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.txtTitle = (TextView)convertView.findViewById(txtTitle);
            viewHolder.txtTime = (TextView)convertView.findViewById(txtTime);
            viewHolder.expandableTextView = (ExpandableTextView) convertView.findViewById(R.id.expand_text_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtTitle.setText(title);
        viewHolder.txtTime.setText(time);
        viewHolder.expandableTextView.setText(message, mCollapsedStatus, position);
        return convertView;
    }


    private static class ViewHolder {
        ExpandableTextView expandableTextView;
        public TextView txtTitle;
        public TextView txtTime;
    }
}