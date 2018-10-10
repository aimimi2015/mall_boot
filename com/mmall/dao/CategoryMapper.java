package com.mmall.dao;

import com.mmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);/**
      * @return java.util.List<com.mmall.pojo.Category>   这种list mybatis封装高了，返回resultmap
      * @date 2017/8/25 下午8:43
      * mybatis 即使查不到list，也会返回一个空list，不会返回null
      */
}