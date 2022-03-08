package com.jihoon.fairy.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.Const.ConstSQL;
import com.jihoon.fairy.Model.ModelEmotions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class FairyDBManager {

    // 데이터 저장(INSERT INTO)
    public void save_values(FairyDBHelper fairyDBHelper, ModelEmotions modelEmotions) {
        SQLiteDatabase db = fairyDBHelper.getWritableDatabase() ;

        String RegistrationDateTime = modelEmotions.getRegistrationDateTime().toString();
        Double happinessDegree = modelEmotions.getHappinessDegree();
        Double sadnessDegree = modelEmotions.getSadnessDegree();
        Double neutralDegree = modelEmotions.getNeutralDegree();
        String imagePath = modelEmotions.getImagePath();
        String imageName = modelEmotions.getImageName();

        // App의 Const List에 추가함.
        Const.List_ModelEmotions.add(modelEmotions);

        // DB에 추가함.
        String sqlInsert = ConstSQL.SQL_INSERT_TBL_EMOTIONS +
                "(" +
                "'" + RegistrationDateTime + "', " +
                "" + happinessDegree + ", " +
                "" + sadnessDegree + ", " +
                "" + neutralDegree + ", " +
                "'" + imagePath + "', " +
                "'" + imageName + "'" +
                ")" ;

        db.execSQL(sqlInsert) ;
    }

    // 데이터 조회(SELECT)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void load_values(FairyDBHelper fairyDBHelper) {

        // App을 종료했다가 빠르게 다시 실행할 경우, 리스트뷰에 사진이 중복되어 나타나는 문제.
        // 원인: 리스트가 초기화되지 않기 때문으로 추정됨.
        // 해결: 아래 코드로 DB 불러올 때, 리스트 초기화.
        Const.List_ModelEmotions.clear();

        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ConstSQL.SQL_SELECT_TBL_EMOTIONS, null);

        while (cursor.moveToNext()) {
            String registrationDateTime_String = cursor.getString(1);
            Double happinessDegree = cursor.getDouble(2);
            Double sadnessDegree = cursor.getDouble(3);
            Double neutralDegree = cursor.getDouble(4);
            String imagePath = cursor.getString(5);
            String imageName = cursor.getString(6);

            // 형변환
            LocalDateTime registrationDateTime = LocalDateTime.parse(registrationDateTime_String);

            ModelEmotions modelEmotions = new ModelEmotions();

            modelEmotions.setRegistrationDateTime(registrationDateTime);
            modelEmotions.setHappinessDegree(happinessDegree);
            modelEmotions.setSadnessDegree(sadnessDegree);
            modelEmotions.setNeutralDegree(neutralDegree);
            modelEmotions.setImagePath(imagePath);
            modelEmotions.setImageName(imageName);

            // App의 Const List에 추가함.
            Const.List_ModelEmotions.add(modelEmotions);
        }
    }
    // 데이터 조회(SELECT)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void load_sort_values(FairyDBHelper fairyDBHelper, ArrayList<ModelEmotions> Sort_Date_List_ModelEmotions) {

        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(ConstSQL.SQL_SELECT_TBL_EMOTIONS_SORT_DATE, null);

        while (cursor.moveToNext()) {
            String registrationDateTime_String = cursor.getString(1);
            Double happinessDegree = cursor.getDouble(2);
            Double sadnessDegree = cursor.getDouble(3);
            Double neutralDegree = cursor.getDouble(4);
            String imagePath = cursor.getString(5);
            String imageName = cursor.getString(6);

            // 형변환
            LocalDateTime registrationDateTime = LocalDateTime.parse(registrationDateTime_String);

            ModelEmotions modelEmotions = new ModelEmotions();

            modelEmotions.setRegistrationDateTime(registrationDateTime);
            modelEmotions.setHappinessDegree(happinessDegree);
            modelEmotions.setSadnessDegree(sadnessDegree);
            modelEmotions.setNeutralDegree(neutralDegree);
            modelEmotions.setImagePath(imagePath);
            modelEmotions.setImageName(imageName);

            Sort_Date_List_ModelEmotions.add(modelEmotions);
        }
    }
}
