package com.jihoon.fairy.Control;

import android.graphics.drawable.Drawable;

import com.jihoon.fairy.Model.Advertisement;
import com.jihoon.fairy.R;

import java.util.ArrayList;

public class AdvertisementListMaker {
    int random = 0;
    Advertisement selectedAdvertisement;
    public Advertisement GetAdvertisement() {
        ArrayList<Advertisement> advertisements = new ArrayList<>();

        Advertisement advertisement = new Advertisement();
        advertisement.setLogo(R.drawable.miuq_logo);
        advertisement.setTitle("제주복합마음공간, 미유크");
        advertisement.setDescription(
                "삶에 도움이 되고 영감을 주는\n심리학 기반 컨텐츠를\n만들어 갑니다");
        advertisement.setLink("https://link.inpock.co.kr/miuq");
        advertisements.add(advertisement);

        Advertisement advertisement2 = new Advertisement();
        advertisement2.setLogo(R.drawable.leancode_logo);
        advertisement2.setTitle("누구나 쉽게 앱 만들기, 린코드");
        advertisement2.setDescription(
                "가장 빠르고 효율적으로\n나만의 앱을 제작한다");
        advertisement2.setLink("https://leancode.kr/");
        advertisements.add(advertisement2);

        random = (int)(Math.random()*advertisements.size());
        selectedAdvertisement = advertisements.get(random);

        return selectedAdvertisement;
    }
}
