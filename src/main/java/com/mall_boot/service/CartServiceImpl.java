package com.mall_boot.service;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mall_boot.common.Const;
import com.mall_boot.common.ResponseCode;
import com.mall_boot.common.ServerResponse;
import com.mall_boot.dao.CartMapper;
import com.mall_boot.dao.ProductMapper;
import com.mall_boot.pojo.Cart;
import com.mall_boot.pojo.Product;
import com.mall_boot.service.ICartService;
import com.mall_boot.util.BigDecimalUtil;
import com.mall_boot.util.PropertiesUtil;
import com.mall_boot.vo.CartProductVo;
import com.mall_boot.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.stereotype.Service;

import java.awt.image.RescaleOp;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ${aimimi2015} on 2017/6/19.
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Autowired
    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);

        if (cart == null) {
            //这个产品不在这个购物车里,需要新增一个这个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);

        } else {
            //这个产品已经在购物车里了.
            //如果产品已存在,数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);

        }

        return this.list(userId);
    }

    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {

        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }


    public ServerResponse<CartVo> deleteProduct(Integer userId, String productIds) {
        //把productids转化为数组
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(productList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAl_ARGUMENT.getCode(), ResponseCode.ILLEGAl_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId, productList);
        return this.list(userId);
    }

    public ServerResponse<CartVo> list(Integer userId) {

        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);

    }

    public ServerResponse<CartVo> selectOrUnselect(Integer userId, Integer productId, Integer checked) {

        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return this.list(userId);

    }

    public ServerResponse<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    //    private CartVo getCartVoLimit(Integer userId) {
//        CartVo cartVo = new CartVo();
//        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
//        List<CartProductVo> cartProductVoList = Lists.newArrayList();
//
//        BigDecimal cartTotalPrice = new BigDecimal("0");
//
//        if (org.apache.commons.collections.CollectionUtils.isEmpty(cartList)) {
//            for (Cart cartItem : cartList) {
//                CartProductVo cartProductVo = new CartProductVo();
//                cartProductVo.setId(cartItem.getId());
//                cartProductVo.setUserId(cartItem.getUserId());
//                cartProductVo.setProductId(cartItem.getProductId());
//
//                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
//                if (product != null) {
//                    cartProductVo.setProductMainImage(product.getMainImage());
//                    cartProductVo.setProductName(product.getName());
//                    cartProductVo.setProductSubtitle(product.getSubtitle());
//                    cartProductVo.setProductStatus(product.getStatus());
//                    cartProductVo.setProductPrice(product.getPrice());
//                    cartProductVo.setProductStock(product.getStock());
//
//                    //判断库存
//                    int buyLimitCount = 0;
//                    if (product.getStock() >= cartItem.getQuantity()) {
//                        //库存充足的时候
//                        buyLimitCount = cartItem.getQuantity();
//
//                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
//                    } else {
//                        buyLimitCount = product.getStock();
//                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
//
//                        //更新该产品在购物车中的有效库存
//                        Cart cartForQuantity = new Cart();
//                        cartForQuantity.setId(cartItem.getId());
//                        cartForQuantity.setQuantity(buyLimitCount);
//                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
//
//                    }
//                    cartProductVo.setQuantity(buyLimitCount);
//                    //计算价格 这个产品的数量和单价相乘
//                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
//                    cartProductVo.setProductChecked(cartItem.getChecked());
//                }
//
//                if (cartItem.getChecked() == Const.Cart.CHECKED) {
//                    //如果已经勾选,重新加入到我们的总价当中
//                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
//                }
//                cartProductVoList.add(cartProductVo);
//
//            }
//        }
//        cartVo.setCartTotalPrice(cartTotalPrice);
//        cartVo.setCartProductVoList(cartProductVoList);
//        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
//        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
//
//        return cartVo;
//    }
//
//    private boolean getAllCheckedStatus(Integer userId){
//        if (userId == null){
//            return false;
//        }
//        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0;
//    }
    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cartItem : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cartItem.getQuantity()) {
                        //库存充足的时候
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cartItem.getChecked());
                }

                if (cartItem.getChecked() == Const.Cart.CHECKED) {
                    //如果已经勾选,增加到整个的购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

    }


}

