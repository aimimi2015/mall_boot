package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ${aimimi2015} on 2017/6/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
//        shipping中没有id属性,但是插入式会自动的生成,因为主键自增长,但是这个Shipping对象中还是没有,虽然数据库中有了,所以在数据库中使用useGeneratedKeys="true" keyProperty="id"
        if(rowCount>0){
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.creatBySuccessMessage("新建地址成功",result);
        }
        return ServerResponse.creatByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
        if (resultCount>0){
            return ServerResponse.creatBySuccess("删除地址成功");
        }
        return ServerResponse.creatByErrorMessage("删除地址失败");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);    //再重新赋值一下,防止横向越权,因为userid也可以模拟
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            return ServerResponse.creatBySuccess("更新地址成功");
        }
        return ServerResponse.creatByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.creatByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.creatBySuccessMessage("查询地址成功",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.creatBySuccess(pageInfo);
    }

}

