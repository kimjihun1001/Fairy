package com.jihoon.fairy.DB;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.jihoon.fairy.Const.ConstModelList;
import com.jihoon.fairy.Const.ConstSQL;
import com.jihoon.fairy.Model.ModelEmotions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        String sqlInsert = ConstSQL.SQL_INSERT_TBL_EMOTIONS +
                " (" +
                "'" + RegistrationDateTime + "', " +
                "" + happinessDegree + ", " +
                "" + sadnessDegree + ", " +
                "" + neutralDegree + ", " +
                "'" + imagePath + "', " +
                "'" + imageName + "', " +
                ")" ;

        db.execSQL(sqlInsert) ;
    }

    // 데이터 조회(SELECT)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void load_values(FairyDBHelper fairyDBHelper) {

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-mm-dd HH:mm:ss.SSS");
            LocalDateTime registrationDateTime = LocalDateTime.parse(registrationDateTime_String, formatter);

            ModelEmotions modelEmotions = new ModelEmotions();

            modelEmotions.setRegistrationDateTime(registrationDateTime);
            modelEmotions.setHappinessDegree(happinessDegree);
            modelEmotions.setSadnessDegree(sadnessDegree);
            modelEmotions.setNeutralDegree(neutralDegree);
            modelEmotions.setImagePath(imagePath);
            modelEmotions.setImageName(imageName);

            ConstModelList.List_ModelEmotions.add(modelEmotions);
        }
    }
}
