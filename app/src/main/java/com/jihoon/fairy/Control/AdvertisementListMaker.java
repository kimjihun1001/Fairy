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
                "\uD83D\uDD39삶에 도움이 되고 영감을\n 주는 심리학 기반 컨텐츠를\n 만들어 갑니다");
        advertisement.setLink("https://link.inpock.co.kr/miuq");

        advertisements.add(advertisement);

        random = (int)(Math.random()*advertisements.size());
        selectedAdvertisement = advertisements.get(random);

        return selectedAdvertisement;
    }
}
