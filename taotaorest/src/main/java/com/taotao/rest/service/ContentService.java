package com.taotao.rest.service;

import com.taotao.pojo.TbContent;

import java.util.List;

/**
 * Created by HuHaifan on 2017/4/26.
 */
public interface ContentService {

    List<TbContent> getContentList(long contentCid) ;
}
