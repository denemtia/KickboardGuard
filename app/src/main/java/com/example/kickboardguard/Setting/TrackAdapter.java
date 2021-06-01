package com.example.kickboardguard.Setting;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.kickboardguard.R;

import java.text.DecimalFormat;

public class TrackAdapter extends CursorAdapter {

    private Context mcontext;
    private int usrname;
    private int usremail;
    private int usrphone;
    private int distance;

    public TrackAdapter (Context context, Cursor cursor){
        super(context,cursor,0);
        mcontext = context;
        usrname = cursor.getColumnIndex("name");
        usremail = cursor.getColumnIndex("email");
        usrphone = cursor.getColumnIndex("phone");
        distance = cursor.getColumnIndex("distance");

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listview_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DecimalFormat decimalFormat = new DecimalFormat("#.#####");

        final String usrName = cursor.getString(usrname);
        final String usrEmail = cursor.getString(usremail);
        final String usrPhone = cursor.getString(usrphone);
        float dis = cursor.getFloat(distance);

        //레이아웃 구성에 맞게 TextView와 매칭
        TextView uusrName = (TextView) view.findViewById(R.id.Name);
        TextView uusrEmail = (TextView) view.findViewById(R.id.Email);
        TextView uusrPhone = (TextView) view.findViewById(R.id.Phone);
        TextView distanceView = (TextView) view.findViewById(R.id.Distance);

        //출력 폼 설정 코드 작성
        uusrName.setText(usrName);
        uusrEmail.setText(usrEmail);
        uusrPhone.setText(usrPhone);
        distanceView.setText(String.valueOf(decimalFormat.format(dis))+"km");
    }
}
