package com.mmall.pojo;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    //为什么这些字段要设为包装类型,如果有一个字段你不想保存内容，
    // 而你把它定义成int，也就是说这个字段值为0，而不是空，把0保存进去后可就不是你想要结果了！

    private Integer id;

    private Integer userId;

    private Integer productId;

    private Integer quantity;

    private Integer checked;

    private Date createTime;

    private Date updateTime;


}