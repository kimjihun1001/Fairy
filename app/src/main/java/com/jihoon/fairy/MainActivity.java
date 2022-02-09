package com.jihoon.fairy;

import androidx.annotation.RequiresApi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.content.res.AssetFileDescriptor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.jihoon.fairy.Const.Const;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorOperator;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.time.LocalDate;
import java.time.LocalTime;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView_result;
    TextView textView_date;
    TextView textView_time;

    String imgName = "jihoon.png";

    SQLiteDatabase sqliteDB;
    FairyDBHelper fairyDBHelper;

    protected Interpreter tflite;
    private TensorImage inputImageBuffer;
    private  int imageSizeX;
    private  int imageSizeY;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private static final float IMAGE_MEAN = 0.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private Bitmap imgBitmap;
    private List<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView_result = findViewById(R.id.textView_result);
        textView_date = findViewById(R.id.textView_date);
        textView_time = findViewById(R.id.textView_time);
//
//        sqliteDB = init_database();
//        init_tables(); // 테이블 생성
//
//        load_values() ; // 데이터 조회

        try{
            tflite=new Interpreter(loadmodelfile(MainActivity.this));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Click_button_upload(View view) {    // 이미지 선택 누르면 실행됨 이미지 고를 갤러리 오픈
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Click_button_result(View view) {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();

        String LocalDate_String = localDate.toString();
        String LocalTime_String = localTime.toString();

        textView_date.setText(LocalDate_String);
        textView_time.setText(LocalTime_String);

        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        inputImageBuffer = loadImage(imgBitmap);

        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());
        showresult();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(fileUri);

                    imgBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);

//                  지훈님꺼
                    Bitmap imgBitmap1 = BitmapFactory.decodeStream(instream);
                    imageView.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                    instream.close();   // 스트림 닫아주기
                    saveBitmapToJpeg(imgBitmap1);    // 내부 저장소에 저장
                    Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                    textView_result.setText("사진이 업로드되었습니다. 결과 확인을 눌러주세요.");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void saveBitmapToJpeg(Bitmap imgBitmap) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void bt2(View view) {    // 이미지 삭제
        try {
            File file = getCacheDir();  // 내부저장소 캐시 경로를 받아오기
            File[] flist = file.listFiles();
            for (int i = 0; i < flist.length; i++) {    // 배열의 크기만큼 반복
                if (flist[i].getName().equals(imgName)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
                    flist[i].delete();  // 파일 삭제
                    Toast.makeText(getApplicationContext(), "파일 삭제 성공", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 삭제 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private SQLiteDatabase init_database() {
        SQLiteDatabase db = null;

        File file = new File (getFilesDir(), "contact.db");

        System.out.println("PATH : " + file.toString());

        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null) ;
        } catch (SQLiteException e) {
            e.printStackTrace() ;
        }

        if (db == null) {
            System.out.println("DB creation failed." + file.getAbsolutePath());
        }

        return db;
    }

    private void init_tables() {
        fairyDBHelper = new FairyDBHelper(this);
    }

    private void load_values() {
        SQLiteDatabase db = fairyDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(Const.SQL_SELECT_TBL_EMOTIONS, null);

        if (cursor.moveToFirst()) {
            // COL_NO 값 가져오기
//            LocalDateTime RegistrationTime = cursor.get(1);
            Double angerDegree = cursor.getDouble(2);
            Double contemptDegree = cursor.getDouble(3);
            Double disgustDegree = cursor.getDouble(4);
            Double fearDegree = cursor.getDouble(5);
            Double happinessDegree = cursor.getDouble(6);
            Double neutralDegree = cursor.getDouble(7);
            Double sadnessDegree = cursor.getDouble(8);
            Double surpriseDegree = cursor.getDouble(9);
        }
    }
    private TensorImage loadImage(final Bitmap imgBitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(imgBitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(imgBitmap.getWidth(), imgBitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(getPreprocessNormalizeOp())
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }

    private MappedByteBuffer loadmodelfile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor=activity.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startoffset = fileDescriptor.getStartOffset();
        long declaredLength=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startoffset,declaredLength);
    }

    private TensorOperator getPreprocessNormalizeOp() {
        return new NormalizeOp(IMAGE_MEAN, IMAGE_STD);
    }
    private TensorOperator getPostprocessNormalizeOp(){
        return new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD);
    }


    private void showresult(){

        try{
            labels = FileUtil.loadLabels(MainActivity.this,"labels.txt");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
            //if (entry.getValue()==maxValueInMap) {
//            String[] label = labeledProbability.keySet().toArray(new String[0]);
            Float[] label_probability = labeledProbability.values().toArray(new Float[0]);


            textView_result.setText("기쁨 : " + label_probability[0] + " 슬픔 : " + label_probability[1] + " 무표정 : " + label_probability[2]    );
        }
    }
}