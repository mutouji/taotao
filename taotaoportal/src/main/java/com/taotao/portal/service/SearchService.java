package com.taotao.portal.service;

import com.taotao.portal.pojo.SearchResult;

/**
 * Created by HuHaifan on 2017/5/2.
 */
public interface SearchService {

    SearchResult search(String queryString, int page);
}
