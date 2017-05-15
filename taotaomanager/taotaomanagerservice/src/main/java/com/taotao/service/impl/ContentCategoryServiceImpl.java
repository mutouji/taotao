package com.taotao.service.impl;

import com.taotao.common.pojo.EUTreeNode;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内容管理
 * Created by HuHaifan on 2017/4/24.
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    @Override
    public List<EUTreeNode> getCategoryList(long parentid) {
        //根据parentId查询节点列表
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentid);
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        List<EUTreeNode> resultlist = new ArrayList<>();
        for (TbContentCategory tbContentCategory: list) {
            //创建一个节点
            EUTreeNode node = new EUTreeNode();
            node.setId(tbContentCategory.getId());
            node.setText(tbContentCategory.getName());
            node.setState(tbContentCategory.getIsParent()?"closed":"open");

            resultlist.add(node);
        }

        return resultlist;
    }

    @Override
    public TaotaoResult insertContentCategory(long parentid, String name) {
        TbContentCategory contentCategory = new TbContentCategory();
        contentCategory.setParentId(parentid);
        contentCategory.setName(name);
        contentCategory.setIsParent(false);
        //1为正常，2为删除
        contentCategory.setStatus(1);
        contentCategory.setSortOrder(1);
        contentCategory.setCreated(new Date());
        contentCategory.setUpdated(new Date());
        //添加记录
        contentCategoryMapper.insert(contentCategory);
        //查看父节点的isParent是否为true
        TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentid);
        //判断是否为true
        if (!parentCat.getIsParent()) {
            parentCat.setIsParent(true);
            //更新父节点
            contentCategoryMapper.updateByPrimaryKey(parentCat);
        }
        //返回结果
        return TaotaoResult.ok(contentCategory);
    }

    @Override
    public TaotaoResult deleteContentCategory(long parentid, long id) {
        //删除记录
        contentCategoryMapper.deleteByPrimaryKey(id);
        //删除后判断父节点是否还有子节点
        TbContentCategoryExample example = new TbContentCategoryExample();
        TbContentCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andParentIdEqualTo(parentid);
        List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
        if (list == null || list.isEmpty()) {
            TbContentCategory parentCat = contentCategoryMapper.selectByPrimaryKey(parentid);
            parentCat.setIsParent(false);
            //更新父节点
            contentCategoryMapper.updateByPrimaryKey(parentCat);
        }
        return TaotaoResult.ok();
    }

    @Override
    public TaotaoResult updateContentCategory(long id, String name) {
        TbContentCategory tcc = contentCategoryMapper.selectByPrimaryKey(id);

        TbContentCategory  contentCategory=new TbContentCategory();
        contentCategory.setCreated(tcc.getCreated());
        contentCategory.setId(id);
        contentCategory.setIsParent(tcc.getIsParent());
        contentCategory.setName(name);
        contentCategory.setParentId(tcc.getParentId());
        contentCategory.setSortOrder(tcc.getSortOrder());
        contentCategory.setStatus(tcc.getStatus());
        contentCategory.setUpdated(new Date());

        contentCategoryMapper.updateByPrimaryKey(contentCategory);
        return TaotaoResult.ok(contentCategory);
    }
}
