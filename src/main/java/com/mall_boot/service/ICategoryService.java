package com.mall_boot.service;

import com.mall_boot.common.ServerResponse;
import com.mall_boot.pojo.Category;

import java.util.List;

/**
 * Created by ${aimimi2015} on 2017/6/13.
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}
