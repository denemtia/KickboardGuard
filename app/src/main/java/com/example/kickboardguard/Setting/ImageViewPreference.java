package com.example.kickboardguard.Setting;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;

import com.example.kickboardguard.R;

public class ImageViewPreference extends Preference {

    private ImageView imageView;
    private Bitmap imageBitmap;
    View.OnClickListener imageClickListener;

    public ImageViewPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        imageView = (ImageView)holder.findViewById(R.id.image);
        imageView.setClickable(true);
        imageView.setOnClickListener(imageClickListener);
        imageView.setImageBitmap(imageBitmap);
    }

    public void setImageClickListener(View.OnClickListener onClickListener)
    {
        imageClickListener = onClickListener;
        //Log.e("로그","로그남김");
    }

    public void setBitmap(Bitmap bitmap)
    {
        imageBitmap = bitmap;

        //Log.e("로그","로그남김");
    }


}
