package com.taotao.search.mapper;

import com.taotao.search.pojo.Item;

import java.util.List;

/**
 * Created by HuHaifan on 2017/4/28.
 */
public interface ItemMapper {

    /**
     * 查询solr需要的记录 多表查询
     * @return
     */
    List<Item> getItemList();
}
