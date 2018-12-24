package com.evanderfilipi.radiout;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Evander Filipi on 12/05/2017.
 */
public class CustomAdapter extends PagerAdapter {
    Activity activity;
    String[] imgUrl;

    public CustomAdapter(Activity activity, String[] imgUrl){
        this.activity = activity;
        this.imgUrl = imgUrl;
    }

    public Object instantiateItem(ViewGroup container, int position){
        LayoutInflater inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewItem = inflater.inflate(R.layout.image_item, container, false);
        ImageView imageView = (ImageView)viewItem.findViewById(R.id.imageView);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
        imageView.setMinimumHeight(height);
        imageView.setMinimumWidth(width);
        try {
            Picasso.with(activity.getApplicationContext())
                    .load(imgUrl[position])
                    .placeholder(R.drawable.logo_image)
                    .error(R.drawable.logo_image_error)
                    .into(imageView);
        }
        catch (Exception ex){

        }
        container.addView(viewItem);
        return  viewItem;
    }

    public int getCount(){
        return imgUrl.length;
    }

    public boolean isViewFromObject(View view, Object object){
        return view == object;
    }

    public void destroyItem(ViewGroup container, int position, Object object){
        ((ViewPager) container).removeView((View) object);
    }
}
