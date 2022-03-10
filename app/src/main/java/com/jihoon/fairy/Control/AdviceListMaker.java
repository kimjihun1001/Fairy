package com.jihoon.fairy.Control;

import android.widget.Toast;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AdviceListMaker {

    int random = 0;
    String advice = null;
    // 기분 좋을 때
    public String GoodAdvice(){
        ArrayList<String> goodAdvice = new ArrayList<>();

        goodAdvice.add("요즘 기분이 좋아보이네요!");
        goodAdvice.add("지금 느끼는 감정으로 오늘 하루도 활기차게 보내요!");
        goodAdvice.add("당신이 느끼는 행복이 주변 사람들도 기분 좋게 만들어 줄 것 같아요!");

        random = (int)(Math.random()*goodAdvice.size());

        advice = goodAdvice.get(random);

        return advice;
    }


    // 평범할 때 (작은 행동으로 기분을 더 좋게 만들어보면 좋을 듯)
    public String NormalAdvice(){
        ArrayList<String> nomalAdvice = new ArrayList<>();

        nomalAdvice.add("잠깐 시간내서 산책을 해보는건 어떠세요?");
        nomalAdvice.add("오늘 날씨는 어떤가요? 주변 사람들과 날씨에 대해 이야기를 해봐요!");
        nomalAdvice.add("오늘은 조금 시간을 내서 취미생활을 해보는 건 어떠세요?");
        nomalAdvice.add("기지개를 켜보고 스트레칭을 한번 해보세요!");
        nomalAdvice.add("친구들과 시시콜콜한 이야기를 나눠보세요");

        random = (int)(Math.random()*nomalAdvice.size());

        advice = nomalAdvice.get(random);

        return advice;
    }

    // 슬플 때
    public String SadAdvice(){
        ArrayList<String> sadAdvice = new ArrayList<>();

        sadAdvice.add("오늘은 잠깐 내려놔도 좋을 것 같아요");
        sadAdvice.add("한 줄 일기를 써보는건 어떠세요?");
        sadAdvice.add("완벽하지 않아도 괜찮아요");
        sadAdvice.add("지금 그대로의 당신도 괜찮아요");

        random = (int)(Math.random()*sadAdvice.size());

        advice = sadAdvice.get(random);

        return advice;
    }
}
