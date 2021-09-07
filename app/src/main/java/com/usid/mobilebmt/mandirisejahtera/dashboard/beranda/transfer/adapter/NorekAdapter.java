package com.usid.mobilebmt.mandirisejahtera.dashboard.beranda.transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.usid.mobilebmt.mandirisejahtera.R;

import java.util.ArrayList;

public class NorekAdapter extends ArrayAdapter<Norek> {
    private ArrayList<Norek> items;
    private ArrayList<Norek> itemsAll;
    private ArrayList<Norek> suggestions;
    private int viewResourceId;
    private int itemsize;
    private String sfilter;

    public NorekAdapter(Context context, int viewResourceId, ArrayList<Norek> items, int itemsize, String sfilter) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<Norek>) items.clone();
        this.suggestions = new ArrayList<Norek>();
        this.viewResourceId = viewResourceId;
        this.itemsize = itemsize;
        this.sfilter = sfilter;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        Norek customer = items.get(position);

        if (customer != null) {
            TextView tvname = (TextView) v.findViewById(R.id.tv_nama);
            TextView tvnorek = (TextView) v.findViewById(R.id.tv_norek);
            TextView tvbank = (TextView) v.findViewById(R.id.tv_nmbank);

            if (tvname != null) {
                tvname.setText(customer.getNama());
            }
            if (tvnorek != null) {
                tvnorek.setText(customer.getNorek());
            }

            if (tvbank != null) {
                tvbank.setText(customer.getNamabank());
            }

        }
        return v;
    }

    @Override
    public Filter getFilter() {

        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Norek) (resultValue)).getNorek();

            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();

                for (Norek customer : itemsAll) {
                    if (customer.getNorek().replace(".", "").contains(constraint.toString().replace(".", ""))) {

                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();

                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Norek> filteredList = (ArrayList<Norek>) results.values;

            if (results != null && results.count > 0) {
                clear();
                for (Norek c : filteredList) {
                    add(c);

                }
                notifyDataSetChanged();
            }
        }
    };

}