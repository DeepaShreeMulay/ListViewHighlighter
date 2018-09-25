package com.vritti.yard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Admin-3 on 11/11/2016.
 */

public class BusTimeTableItemListAdapter extends BaseAdapter {
    private ArrayList<BusTimeTableItemBean> arrayList;

    private Context parent;
    private LayoutInflater mInflater;
    private ViewHolder holder = null;

    private int selectedItem = -1; // no item selected by default

    public BusTimeTableItemListAdapter(Context context,
                                       ArrayList<BusTimeTableItemBean> list
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

    public void setSelectedItem(int selectedItem) {
        this.selectedItem = selectedItem;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final int pos = position;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.custom_bus_details, null);
            holder = new ViewHolder();
            holder.txttime = (TextView) convertView.findViewById(R.id.txttime);
            holder.txtfrom = (TextView) convertView.findViewById(R.id.txtfrom);
            holder.txtto = (TextView) convertView.findViewById(R.id.txtto);
            holder.txtVia = (TextView) convertView.findViewById(R.id.txtVia);
            holder.txtFalat = (TextView) convertView.findViewById(R.id.txtFalat);
            holder.txtBusType = (TextView) convertView.findViewById(R.id.txtBusType);
            holder.txtDivisionName = (TextView) convertView.findViewById(R.id.txtDivisionName);
            //holder.txtLateReason = (TextView) convertView.findViewById(R.id.txtLateReason);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        highlightItem(position, convertView);

        holder.txttime.setText(arrayList.get(position).getTV());
        holder.txtfrom.setText(arrayList.get(position).getStartingStation());
        holder.txtto.setText(arrayList.get(position).getDestinationStation());
        holder.txtVia.setText(arrayList.get(position).getViaStation());
        holder.txtFalat.setText(arrayList.get(position).getPlatformMasterId());
        holder.txtBusType.setText(arrayList.get(position).getBusTypeName());
        holder.txtDivisionName.setText(arrayList.get(position).getDivisionName());
        //holder.txtLateReason.setText(arrayList.get(position).getReason());

        return convertView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void highlightItem(int position, View result) {
        if(position == selectedItem) {
            // you can define your own color of selected item here
            result.setBackgroundColor(Color.parseColor("#FF4081"));
        } else {
            // you can define your own default selector here
            result.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    private static class ViewHolder {
        TextView txttime;
        TextView txtfrom;
        TextView txtto;
        TextView txtVia ;
        TextView txtFalat ;
        TextView txtBusType ;
        TextView txtDivisionName ;
        TextView txtLateReason;
    }


}