package com.taotao.service;

import com.taotao.common.pojo.EUTreeNode;
import com.taotao.common.pojo.TaotaoResult;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by HuHaifan on 2017/4/24.
 */
public interface ContentCategoryService {

    List<EUTreeNode> getCategoryList(long parentid) ;

    TaotaoResult insertContentCategory(long parentid, String name);

    TaotaoResult deleteContentCategory(long parentid, long id);

    TaotaoResult updateContentCategory(long id, String name);
}
