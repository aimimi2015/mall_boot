package com.mall_boot.service;

import com.mall_boot.common.Const;
import com.mall_boot.common.ServerResponse;
import com.mall_boot.dao.UserMapper;
import com.mall_boot.pojo.User;
import com.mall_boot.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by jiangxiao on 2018/10/2.
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public ServerResponse<User> login(String username, String password) {


        int count = userMapper.checkUsername(username);
        if (count == 0) {
            return ServerResponse.createByErrorMessage("该用户名尚未注册");
        }
        String MD5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("登录失败，密码或用户名不正确");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccessMessage("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse<String> response = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!response.isSuccess()) {
            return response;
        }
        response = this.checkValid(user.getUsername(), Const.EMAIL);
        if (!response.isSuccess()) {
            return response;
        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");

        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {

        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.equals(type, Const.USERNAME)) {
                int count = userMapper.checkUsername(str);
                if (count > 0) {
                    return ServerResponse.createByErrorMessage("该用户名已被注册");
                }
            }
            if ((Const.EMAIL.equals(type))) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
            return ServerResponse.createBySuccess();

        } else {
            return ServerResponse.createByErrorMessage("参数错误");
        }
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行校验,校验新的email是不是已经存在,并且存在的email相同的话,不能使我们当前这个用户的
        int resultcount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultcount > 0) {
            return ServerResponse.createByErrorMessage("邮箱已被注册");

        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());


        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count > 0) {
            return ServerResponse.createBySuccessMessage("更新成功", updateUser);

        } else {
            return ServerResponse.createByErrorMessage("更新失败");
        }

    }

    /**
     * 查找用户的密保问题
     */
    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //说明用户不存在 因为用户不存在时return ServerResponse.creatBySuccessMessage("校验成功"); 和28-30行有区别
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccessMessage(question);
        }
        return ServerResponse.createByErrorMessage("找回密码问题是空的");

    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();
//            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);
//
//            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
//            return ServerResponse.createBySuccess(forgetToken);

        }
        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误,token需要传递");

        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //说明用户不存在 因为用户不存在时return ServerResponse.creatBySuccessMessage("校验成功"); 和28-30行有区别
            return ServerResponse.createByErrorMessage("用户不存在");
        }
//        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
//        System.out.println(token + "   :token");
//
//        if (StringUtils.isBlank(token)) {
//            return ServerResponse.createByErrorMessage("token无效或者过期");
//        }
//        if (StringUtils.equals(forgetToken, token)) {
//
//            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
//            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
//            if (rowCount > 0) {
//                return ServerResponse.createBySuccessMessage("修改密码成功");
//            }
//        } else {
//            return ServerResponse.createByErrorMessage("token错误,请重新获取重置密码的token");
//        }
        return ServerResponse.createByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要是这个用户,我们会查询一个count(1),如果不指定，那么结果就是true了,count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {

        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.createByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY); //这个地方把密码置空是因为传到前端的个人信息不会有密码的,数据库中的密码并未置空,因为没有操作userMapper的方法修改数据库
        return ServerResponse.createBySuccess(user);
    }
}

