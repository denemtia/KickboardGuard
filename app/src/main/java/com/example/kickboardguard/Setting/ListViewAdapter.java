package com.example.kickboardguard.Setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kickboardguard.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    public ListViewAdapter(){}
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_myload,parent,false);
        }

        TextView nameTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView emailTextView = (TextView) convertView.findViewById(R.id.textView2);
        TextView phoneTextView = (TextView) convertView.findViewById(R.id.textView3);
        TextView myloadTextView = (TextView) convertView.findViewById(R.id.textView4);

        ListViewItem listViewItem = listViewItemList.get(position);

        nameTextView.setText(listViewItem.getname());
        emailTextView.setText(listViewItem.getemail());
        phoneTextView.setText(listViewItem.getphone());
        myloadTextView.setText(String.format("%f",listViewItem.getMyload()));

        return convertView;
    }
    public void addItem(String name, String email, String phone, float myload) {
        ListViewItem item = new ListViewItem();

        item.setname(name);
        item.setemail(email);
        item.setphone(phone);
        item.setMyload(myload);

        listViewItemList.add(item);
    }
}
