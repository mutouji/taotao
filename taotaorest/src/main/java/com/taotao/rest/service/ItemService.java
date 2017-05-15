package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * Created by HuHaifan on 2017/5/2.
 */
public interface ItemService {

    TaotaoResult getItemBaseInfo(long itemId);

    TaotaoResult getItemDesc(long itemId);

    TaotaoResult getItemParam(long itemId);
}
