package com.jihoon.fairy.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter {
    private ArrayList<ModelEmotions> listViewItemList = new ArrayList<ModelEmotions>();

    public HistoryListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.item_history, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.history_Photo) ;
        TextView registrationDateTime = (TextView) convertView.findViewById(R.id.history_RegistrationDateTime) ;
        TextView happinessDegree = (TextView) convertView.findViewById(R.id.history_HappinessDegree) ;
        TextView sadnessDegree = (TextView) convertView.findViewById(R.id.history_SadnessDegree) ;
        TextView neutralDegree = (TextView) convertView.findViewById(R.id.history_NeutralDegree) ;

        ModelEmotions modelEmotions = listViewItemList.get(position);

//        iconImageView.setImageBitmap(modelEmotions.getImagePath());
        registrationDateTime.setText(modelEmotions.getRegistrationDateTime().toString());
        happinessDegree.setText(modelEmotions.getHappinessDegree().toString());
        sadnessDegree.setText(modelEmotions.getSadnessDegree().toString());
        neutralDegree.setText(modelEmotions.getNeutralDegree().toString());

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

    public void addItem(ModelEmotions item) {

//        item.setImagePath(item.getImagePath());
//        item.setRegistrationDateTime(item.getRegistrationDateTime());
//        item.setHappinessDegree(happinessDegree);
//        item.setSadnessDegree(sadnessDegree);
//        item.setNeutralDegree(neutralDegree);

        listViewItemList.add(item);
    }
}
