package com.nchu.anti_japan_history.webpage.service;

import com.nchu.anti_japan_history.webpage.entity.Periodical;

import java.util.List;

public interface PeriodicalService {
    //查询期刊all
    List<Periodical> periodicalListAll();
    //期刊信息的保存
    int periodicalSave(Periodical periodical);
}
