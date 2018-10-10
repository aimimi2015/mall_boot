package com.mall_boot.service;

import com.github.pagehelper.PageInfo;
import com.mall_boot.common.ServerResponse;
import com.mall_boot.pojo.Shipping;

/**
 * Created by ${aimimi2015} on 2017/6/20.
 */
public interface IShippingService {
    ServerResponse add(Integer userId, Shipping shipping);
    ServerResponse<String> del(Integer userId, Integer shippingId);
    ServerResponse update(Integer userId, Shipping shipping);
    ServerResponse<Shipping> select(Integer userId, Integer shippingId);
    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
