package com.jihoon.fairy.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.Control.AdvertisementListMaker;
import com.jihoon.fairy.Model.Advertisement;
import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.R;

import java.util.ArrayList;

public class AdvertisementListViewAdapter extends BaseAdapter {
    private ArrayList<Advertisement> listViewItemList = new ArrayList<Advertisement>();

    public AdvertisementListViewAdapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_advertisement, parent, false);
        }

        ImageView logoImageView = (ImageView) convertView.findViewById(R.id.ad_logo) ;
        TextView title = (TextView) convertView.findViewById(R.id.ad_Title) ;
        TextView description = (TextView) convertView.findViewById(R.id.ad_description);

        AdvertisementListMaker advertisementListMaker = new AdvertisementListMaker();
        Advertisement advertisement = advertisementListMaker.GetAdvertisement();

        logoImageView.setImageResource(advertisement.getLogo());
        title.setText(advertisement.getTitle());
        description.setText(advertisement.getDescription());
        convertView.setTag(advertisement.getLink());
//        Bitmap savedImageBitmap = BitmapFactory.decodeFile(advertisement.getLogo());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem(Advertisement item) {
        listViewItemList.add(item);
    }
}
