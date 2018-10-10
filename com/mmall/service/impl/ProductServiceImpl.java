package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${aimimi2015} on 2017/6/14.
 */

@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.creatBySuccessMessage("更新产品成功");
                }
                return ServerResponse.creatByErrorMessage("更新产品失败");
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.creatBySuccessMessage("新增产品成功");
                }
                return ServerResponse.creatByErrorMessage("新增产品失败");
            }
            //判断子图是不是空的
        }
        return ServerResponse.creatByErrorMessage("新增或者更新产品参数不正确");
    }

    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
//        因为要求传入的是一个对象,所以要new一个product
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.creatBySuccessMessage("修改产品销售状态成功");
        }
        return ServerResponse.creatByErrorMessage("修改产品销售状态失败");


    }

    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.creatByErrorMessage("产品已下架或者删除");
        }
//        VO对象--value object
        //pojo->bo(businessobject ->vo(view object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.creatBySuccess(productDetailVo);

    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(productDetailVo.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStock(product.getStock());

        productDetailVo.setMainImage(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.aimimi2015.cn/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);  //默认根节点
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());   //
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;

        //createTime
        //updateTime


    }


    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();

        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);  //里面需要传sql返回的集合,但是我们不需要product那么多的信息,所以又封装了一个productListVo，，，但是分页又需要原始的sql查询结果productList
        pageResult.setList(productListVoList);  //把它的list重置为我们想要的    为啥有警告,因为pageResult.setLis需要product类型的list,我们把productListVoList扔进去了
        return ServerResponse.creatBySuccess(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;

    }

    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        //isNotEmpty将空格也作为参数，isNotBlank则排除空格参数 StringUtils.isBlank(" ") = true  StringUtils.isNotEmpty(" ") = true
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
            //这么拼接是为了方便sql语句的like语法查询

        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();

        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);  //这里被我加泛型了
        pageResult.setList(productListVoList);  //为啥有警告,因为pageResult.setLis需要product类型的list,我们把productListVoList扔进去了
        return ServerResponse.creatBySuccess(pageResult);
    }

    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.creatByErrorMessage("产品已下架或者删除");
        }
        if (product.getStatus()!= Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.creatByErrorMessage("产品已下架或者删除");

        }

//        VO对象--value object
        //pojo->bo(businessobject ->vo(view object)
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.creatBySuccess(productDetailVo);
    }

    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if (StringUtils.isBlank(keyword)&&categoryId ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(),ResponseCode.ILLEGAl_ARGUMENT.getDesc());

        }
        List<Integer> categoryIdList = new ArrayList<Integer>();


        if (categoryId!=null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if(category==null&&StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有关键字,这时候返回一个空结果集,不报错
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                //pageResult.setList 不需要了
                return ServerResponse.creatBySuccess(pageInfo);

            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();

        }
        if (StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();

        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);//orderby方法穿的参数格式是price desc
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.creatBySuccess(pageInfo);
    }


}

