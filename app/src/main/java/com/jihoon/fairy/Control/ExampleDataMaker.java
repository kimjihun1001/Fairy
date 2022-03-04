package com.jihoon.fairy.Control;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.DB.FairyDBHelper;
import com.jihoon.fairy.DB.FairyDBManager;
import com.jihoon.fairy.Model.ModelEmotions;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExampleDataMaker {
    public ExampleDataMaker() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public List<ModelEmotions> MakeExampleData() {
        List<ModelEmotions> ListToReturn = new ArrayList<>();

        String[] List_exampleImageName = {
                "2022-02-05T10:30:30", "2022-02-06T10:30:30", "2022-02-07T10:30:30"
                , "2022-02-08T10:30:30", "2022-02-09T10:30:30", "2022-02-10T10:30:30"
                , "2022-02-11T10:30:30", "2022-02-12T10:30:30", "2022-02-13T10:30:30"
                , "2022-02-14T10:30:30", "2022-02-15T10:30:30", "2022-02-16T10:30:30"
                , "2022-02-17T10:30:30", "2022-02-18T10:30:30"
        };
        String[] List_exampleImagePath = new String[1];
        Double[] List_exampleHappiness = {0.1, 0.1, 0.2, 0.35, 0.4, 0.5, 0.6, 0.7, 0.8, 0.5, 0.4, 0.4, 0.3, 0.15};
        Double[] List_exampleNeutral = {0.05, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.0, 0.1, 0.15};
        Double[] List_exampleSadness = {0.8, 0.7, 0.6, 0.5, 0.4, 0.35, 0.2, 0.1, 0.1, 0.3, 0.4, 0.45, 0.5, 0.6};

        Bitmap imageBitmap = BitmapFactory.decodeFile("com/jihoon/fairy/Control/exampleImage/1.jpeg");
        List_exampleImagePath[0] = SaveImageToInternalStorage(imageBitmap, List_exampleImageName[0]);

        ModelEmotions modelEmotions = new ModelEmotions();
        modelEmotions.setImageName(List_exampleImageName[0]);
        modelEmotions.setRegistrationDateTime(LocalDateTime.parse(List_exampleImageName[0]));
        modelEmotions.setImagePath(List_exampleImagePath[0]);
        modelEmotions.setHappinessDegree(List_exampleHappiness[0]);
        modelEmotions.setNeutralDegree(List_exampleNeutral[0]);
        modelEmotions.setSadnessDegree(List_exampleSadness[0]);

        ListToReturn.add(modelEmotions);

        return ListToReturn;
    }

    public String SaveImageToInternalStorage(Bitmap imgBitmap, String imgName) {   // 선택한 이미지 내부 저장소에 저장

        File tempFile = new File(Const.InternalStorage, imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
        } catch (Exception e) {

        }
        return Const.InternalStorage + "/" + imgName;
    }
}

