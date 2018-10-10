package com.mall_boot.service;

import com.mall_boot.common.ServerResponse;
import com.mall_boot.pojo.User;

/**
 * Created by jiangxiao on 2018/10/2.
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<User> getInformation(Integer userId);

    ServerResponse<User> updateInformation(User user);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken);

    ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user);

}
