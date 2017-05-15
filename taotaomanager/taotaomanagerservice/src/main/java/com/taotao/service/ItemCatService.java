package com.taotao.service;

import com.taotao.common.pojo.EUTreeNode;

import java.util.List;

/**
 * Created by HuHaifan on 2017/4/17.
 */
public interface ItemCatService {
    List<EUTreeNode> getCatList(long parentId);
}
