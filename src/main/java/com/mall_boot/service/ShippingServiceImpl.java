package com.mall_boot.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mall_boot.common.ServerResponse;
import com.mall_boot.dao.ShippingMapper;
import com.mall_boot.pojo.Shipping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by ${aimimi2015} on 2017/6/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    private final ShippingMapper shippingMapper;

    @Autowired
    public ShippingServiceImpl(ShippingMapper shippingMapper) {
        this.shippingMapper = shippingMapper;
    }

    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
//        shipping中没有id属性,但是插入式会自动的生成,因为主键自增长,但是这个Shipping对象中还是没有,虽然数据库中有了,所以在数据库中使用useGeneratedKeys="true" keyProperty="id"
        if(rowCount>0){
            Map<String, Integer> result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ServerResponse.createBySuccessMessage("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deleteByPrimaryKey(shippingId);
        if (resultCount>0){
            return ServerResponse.createBySuccess("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse<String> update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);    //再重新赋值一下,防止横向越权,因为userid也可以模拟
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount>0){
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping==null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccessMessage("查询地址成功",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }

}

