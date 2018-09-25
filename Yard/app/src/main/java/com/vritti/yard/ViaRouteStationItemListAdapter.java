package com.vritti.yard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin-3 on 11/11/2016.
 */

public class ViaRouteStationItemListAdapter extends BaseAdapter {
    private ArrayList<ViaRouteStnItemBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    public ViaRouteStationItemListAdapter(Context context,
                                          ArrayList<ViaRouteStnItemBean> list
    ) {
        parent = context;
        arrayList = list;
        mInflater = LayoutInflater.from(parent);

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_viastationname, null);
            holder = new ViewHolder();
            holder.textviastationname = (TextView) convertView.findViewById(R.id.textviastationname);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textviastationname.setText(arrayList.get(position).getStationName());

        return convertView;
    }

    private static class ViewHolder {
        TextView textviastationname;
    }


}