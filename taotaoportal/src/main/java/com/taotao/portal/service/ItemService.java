package com.taotao.portal.service;

import com.taotao.portal.pojo.ItemInfo;

/**
 * Created by HuHaifan on 2017/5/2.
 */
public interface ItemService {

    ItemInfo getItemById(Long itemId);

    String getItemDescById(Long itemId);

    String getItemParam(Long itemId);
}
