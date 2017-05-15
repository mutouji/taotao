package com.taotao.rest.service;

import com.taotao.common.pojo.TaotaoResult;

/**
 * Created by HuHaifan on 2017/4/27.
 */
public interface RedisService {

    TaotaoResult syncContent(long contentCid);
}
