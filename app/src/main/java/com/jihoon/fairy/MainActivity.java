package com.jihoon.fairy;
// Ver 0.2
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import android.content.res.AssetFileDescriptor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.tabs.TabLayout;
import com.jihoon.fairy.Adapter.HistoryRecyclerViewAdapter;
import com.jihoon.fairy.Adapter.PhotoHistoryListViewAdapter;
import com.jihoon.fairy.Const.Const;
import com.jihoon.fairy.DB.FairyDBHelper;
import com.jihoon.fairy.DB.FairyDBManager;
import com.jihoon.fairy.Model.HistoryRecyclerItem;
import com.jihoon.fairy.Model.ModelEmotions;

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
import java.io.OutputStream;
import java.time.LocalDateTime;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView_result;
    TextView textView_dateTime;
    ImageView imageView_savedImage;

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
    private Bitmap bmRotated;
    private List<String> labels;

    ListView history_ListView;
    PhotoHistoryListViewAdapter history_Adapter;

    // 그래프 그리기
    LineChart chart;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView_result = findViewById(R.id.textView_result);
        textView_dateTime = findViewById(R.id.textView_dateTime);

        // DB 받아줄 변수 설정
        sqliteDB = init_database();
        init_tables(); // 테이블 생성

        // DB 불러오기 싱글톤 변경 필요함
        ModelEmotions modelEmotions;
        FairyDBManager fairyDBManager = new FairyDBManager();
        fairyDBManager.load_values(fairyDBHelper);// 데이터 조회

        // TODO : API 호출 코드인가?
        try{
            tflite=new Interpreter(loadmodelfile(MainActivity.this));
        }catch (Exception e) {
            e.printStackTrace();
        }

        // 앱이 켜지면 홈 화면 탭이 선택된 상태로 만들기
        // 홈 화면 탭에 대한 FrameLayout 내의 화면은 XML에서 visible로 설정되어있음.
        // 왜인지 모르겠지만 이 코드로는 탭 선택이 안됨. 오류 발생
        // 그래서 그냥 홈, 기록, 설정 순서로 탭 순서 변경.
        // TabItem tabItem_home = (TabItem) findViewById(R.id.tabItem_home);
        // tabItem_home.setSelected(true);

        // 탭 선택 이벤트 핸들러 설정하기
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                // 선택한 탭에 대한 화면을 표시하는 메쏘드
                ChangeView(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // 그래프 그리기
        chart = (LineChart) findViewById(R.id.chart);
        List<Entry> entries = new ArrayList<Entry>();

        for (ModelEmotions modelEmotions1 : Const.List_ModelEmotions) {
            String second_str = modelEmotions1.getRegistrationDateTime().toString().substring(11, 18).replace(":","");
            float second = Integer.parseInt(second_str);
            float happy = modelEmotions1.getHappinessDegree().floatValue();
            entries.add(new Entry(second, happy));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextColor(Color.BLUE);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh -> 안됨 ...
    }

    // 탭 선택 시, 표시 화면 변경하기
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void ChangeView(int index) {
        LinearLayout layout_home = findViewById(R.id.layout_home);
        ScrollView scrollView_history = findViewById(R.id.scrollView_history);
        LinearLayout layout_photoHistory = findViewById(R.id.layout_photoHistory);
        LinearLayout layout_setting = findViewById(R.id.layout_setting);
        switch (index) {
            case 0 :
                layout_home.setVisibility(View.VISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);
                break;
            case 1 :
                // TODO : 그래프 새로고침

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.VISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);
                break;
            case 2 :
                // 리사이클러뷰 아이템 생성
                ArrayList<ModelEmotions> Sort_Date_List_ModelEmotions = new ArrayList<ModelEmotions>();
                FairyDBManager fairyDBManager = new FairyDBManager();
                fairyDBManager.load_sort_values(fairyDBHelper, Sort_Date_List_ModelEmotions);

                ArrayList<String> list = new ArrayList<>();
                for (int i = 0; i < Sort_Date_List_ModelEmotions.size(); i++) {
                    String tempString;
                    tempString = Sort_Date_List_ModelEmotions.get(i).getRegistrationDateTime().format(DateTimeFormatter.ofPattern("yyyy년\nMM월\ndd일"));

                    int tempNum = 0;
                    if (list.size() > 0) {
                        for (String temp : list) {
                            if (temp == tempString) {
                                tempNum++;
                            }
                        }
                        if (tempNum != 0) {
                            list.add(tempString);
                        }
                    }
                    else {
                        list.add(tempString);
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.recyclerView_historyDate);
                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

                HistoryRecyclerViewAdapter adapter = new HistoryRecyclerViewAdapter(list);
                recyclerView.setAdapter(adapter);


//                // 기록 탭 리스트뷰와 어뎁터 연결하기
//                history_Adapter = new PhotoHistoryListViewAdapter();
//                history_Adapter.notifyDataSetChanged();     // 변화 생기면 업데이트되도록 함
//                history_ListView = (ListView)findViewById(R.id.listView_historyPhoto);
//                history_ListView.setAdapter(history_Adapter);
//                for (int i = 0; i < Const.List_ModelEmotions.size(); i++) {
//                    history_Adapter.addItem(Const.List_ModelEmotions.get(i)) ;
//                }

                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.VISIBLE);
                layout_setting.setVisibility(View.INVISIBLE);
                break;
            case 3 :
                layout_home.setVisibility(View.INVISIBLE);
                scrollView_history.setVisibility(View.INVISIBLE);
                layout_photoHistory.setVisibility(View.INVISIBLE);
                layout_setting.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 이미지 선택 누르면 실행됨 이미지 고를 갤러리 오픈
    public void Click_button_upload(View view) {
        Toast.makeText(this, "업로드 버튼 클릭", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101);
    }

    // 갤러리에서 이미지를 선택하면 요청이 전송됨
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();

                // 사진파일 실제 주소 얻기.
                String realPath = createCopyAndReturnRealPath(this,fileUri);

                //회전 여부 알기
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(realPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);


                try {
                    Bitmap imgBitmap = MediaStore.Images.Media.getBitmap(resolver, fileUri);

                    //이미지 회전시키기
                    bmRotated = rotateBitmap(imgBitmap, orientation);

                    imageView.setImageBitmap(bmRotated);    // 선택한 이미지 이미지뷰에 표시
                    // Toast.makeText(getApplicationContext(), "사진 불러오기 성공", Toast.LENGTH_SHORT).show();
                    textView_result.setText("사진이 업로드되었습니다. 결과 확인을 눌러주세요.");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "사진 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public String SaveImage(Bitmap imgBitmap, String imgName) {   // 선택한 이미지 내부 저장소에 저장
        File tempFile = new File(getCacheDir(), imgName);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫아주기
            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
            // 파일 경로 반환
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
        return getCacheDir() + "/" + imgName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void Click_button_result(View view) {
        // 객체 생성
        ModelEmotions currentModelEmotions = new ModelEmotions();

        // 현재 날짜, 시간 측정
        LocalDateTime localDateTime = LocalDateTime.now();

        // 객체에 생성 날짜, 시간 부여
        currentModelEmotions.setRegistrationDateTime(localDateTime);

        // 객체에 이름 부여
        currentModelEmotions.setImageName(localDateTime.toString());

        // 사진 로컬(내부 저장소)에 저장
        // 이미지 경로 반환받아서 객체에 이미지 경로 저장
        String imgPath = SaveImage(bmRotated, currentModelEmotions.getImageName());
        currentModelEmotions.setImagePath(imgPath);

        // API 호출
        int imageTensorIndex = 0;
        int[] imageShape = tflite.getInputTensor(imageTensorIndex).shape(); // {1, height, width, 3}
        imageSizeY = imageShape[1];
        imageSizeX = imageShape[2];
        DataType imageDataType = tflite.getInputTensor(imageTensorIndex).dataType();

        //
        int probabilityTensorIndex = 0;
        int[] probabilityShape =
                tflite.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
        DataType probabilityDataType = tflite.getOutputTensor(probabilityTensorIndex).dataType();

        inputImageBuffer = new TensorImage(imageDataType);
        outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);
        probabilityProcessor = new TensorProcessor.Builder().add(getPostprocessNormalizeOp()).build();

        inputImageBuffer = loadImage(bmRotated);

        tflite.run(inputImageBuffer.getBuffer(),outputProbabilityBuffer.getBuffer().rewind());

        try{
            labels = FileUtil.loadLabels(MainActivity.this,"labels.txt");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        //Map자료구조를 이용하여 key - value 형태로 구성
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();

        for (Map.Entry<String, Float> entry : labeledProbability.entrySet()) {
//            if (entry.getValue()==maxValueInMap) {
//                String[] label = labeledProbability.keySet().toArray(new String[0]);
//            }
            Float[] label_probability = labeledProbability.values().toArray(new Float[0]);

            // 객체에 감정 분석 결과 값 부여
            currentModelEmotions.setHappinessDegree(Double.valueOf(label_probability[0]));
            currentModelEmotions.setSadnessDegree(Double.valueOf(label_probability[1]));
            currentModelEmotions.setNeutralDegree(Double.valueOf(label_probability[2]));
        }

        // DB 저장
        FairyDBManager fairyDBManager = new FairyDBManager();
        fairyDBManager.save_values(fairyDBHelper, currentModelEmotions);

        // 화면에 표시하기
        ShowResult(currentModelEmotions);
    }

    private void ShowResult(ModelEmotions modelEmotions) {

        textView_result.setText("기쁨: " + Const.ConvertDoubleToPercentage(modelEmotions.getHappinessDegree()) + "슬픔: " + Const.ConvertDoubleToPercentage(modelEmotions.getSadnessDegree()) + "무표정: " + Const.ConvertDoubleToPercentage(modelEmotions.getNeutralDegree()));
        textView_dateTime.setText(modelEmotions.getRegistrationDateTime().toString());

    }
//    public void bt2(View view) {    // 이미지 삭제
//        try {
//            File file = getCacheDir();  // 내부저장소 캐시 경로를 받아오기
//            File[] flist = file.listFiles();
//            for (int i = 0; i < flist.length; i++) {    // 배열의 크기만큼 반복
//                if (flist[i].getName().equals(imgName)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
//                    flist[i].delete();  // 파일 삭제
//                    Toast.makeText(getApplicationContext(), "파일 삭제 성공", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 삭제 실패", Toast.LENGTH_SHORT).show();
//        }
//    }

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

    //TensorImage에 처리할 이미지를 추가하는 함수
    //이미지 학습에 사용한 이미지의 사이즈에 따라 사이즈 조절 코드.
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

    //모델을 읽어오는 함수
    //모델 파일을 MappedByteBuffer 바이트 버퍼형식으로 메모리 로딩해서 Interperter 객체에 전달하면 모델 해설 가능
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

    //파일 절대 경로를 알아오는 코드
    public static String createCopyAndReturnRealPath(@NonNull Context context, @NonNull Uri uri){
        final ContentResolver contentResolver = context.getContentResolver();

        if (contentResolver == null)
            return null;

        //파일 경로를 만듬
        String filePath = context.getApplicationInfo().dataDir + File.separator
                + System.currentTimeMillis();

        File file = new File(filePath);
        try{
            //매개변수로 받은 uri 를 통해 이미지에 필요한 데이터를 불러옴
            InputStream inputStream = contentResolver.openInputStream(uri);
            if(inputStream == null)
                return null;

            //이미지 데이터를 다시 내보내면서 file객체에 만들었던 경로 이용
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0)
                outputStream.write(buf, 0, len);
            outputStream.close();
            inputStream.close();
        }catch (IOException ignore){
            return null;
        }
        return  file.getAbsolutePath();
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        //돌아간 정도를 얻고 그만큼 다시 돌리는 코
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
}