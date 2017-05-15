package com.taotao.search.service;

import com.taotao.search.pojo.SearchResult;

/**
 * Created by HuHaifan on 2017/4/28.
 */
public interface SearchService {

    SearchResult search(String queryString, int page, int rows) throws Exception;
}

