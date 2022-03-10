package com.jihoon.fairy.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.Model.ModelEmotions;
import com.jihoon.fairy.R;

import java.util.ArrayList;

public class PhotoHistoryListViewAdapter extends BaseAdapter {
    private ArrayList<ModelEmotions> listViewItemList = new ArrayList<ModelEmotions>();

    public PhotoHistoryListViewAdapter() {

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
            convertView = inflater.inflate(R.layout.item_photohistory, parent, false);
        }

        ImageView photoImageView = (ImageView) convertView.findViewById(R.id.history_Photo) ;
        TextView registrationDateTime = (TextView) convertView.findViewById(R.id.history_RegistrationDateTime) ;
        TextView happinessDegree = (TextView) convertView.findViewById(R.id.history_HappinessDegree) ;
        TextView sadnessDegree = (TextView) convertView.findViewById(R.id.history_SadnessDegree) ;
        TextView neutralDegree = (TextView) convertView.findViewById(R.id.history_NeutralDegree) ;

        ModelEmotions modelEmotions = listViewItemList.get(position);

        Bitmap savedImageBitmap = BitmapFactory.decodeFile(modelEmotions.getImagePath());

        photoImageView.setImageBitmap(savedImageBitmap);
        registrationDateTime.setText(modelEmotions.getRegistrationDateTime().toString().replace("T", " ").substring(0, 18));
        happinessDegree.setText(Const.ConvertDoubleToPercentage(modelEmotions.getHappinessDegree()));
        sadnessDegree.setText(Const.ConvertDoubleToPercentage(modelEmotions.getSadnessDegree()));
        neutralDegree.setText(Const.ConvertDoubleToPercentage(modelEmotions.getNeutralDegree()));

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
        listViewItemList.add(item);
    }

    // 날짜 버튼 선택할 때마다 리스트 초기화하도록 메쏘드 하나 만들었음.
    public void clearItem() {
        listViewItemList.clear();
    }
}
