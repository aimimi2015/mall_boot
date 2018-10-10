package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import com.mmall.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by ${aimimi2015} on 2017/6/9.
 * Impl结尾代表接口的实现类
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.creatByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.creatByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccessMessage("登陆成功", user);

    }

    @Override
    public ServerResponse<String> register(User user) {

//        int resultCount = userMapper.checkUsername(user.getUsername());
//        if (resultCount>0){
//            return ServerResponse.creatByErrorMessage("用户名已存在");
//        }
        //有了校验的方法,之前的上面的方法可以不用了
        ServerResponse<String> vaildResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!vaildResponse.isSuccess()) {
            return vaildResponse;
        }

        vaildResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!vaildResponse.isSuccess()) {
            return vaildResponse;
        }

//        resultCount = userMapper.checkUsername(user.getEmail());
//        if (resultCount>0){
//            return ServerResponse.creatByErrorMessage("email已存在");
//        }

        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5明文加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.creatByErrorMessage("注册失败");

        }
        return ServerResponse.creatBySuccessMessage("注册成功");

    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) {
            //开始校验
            if ((Const.USERNAME.equals(type))) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("用户名已存在");
                }
            }
            if ((Const.EMAIL.equals(type))) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.creatByErrorMessage("email已存在");
                }
            }
        } else {
            return ServerResponse.creatByErrorMessage("参数错误");

        }
        return ServerResponse.creatBySuccessMessage("校验成功");
    }

    @Override
    //查找用户的密保问题
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //说明用户不存在 因为用户不存在时return ServerResponse.creatBySuccessMessage("校验成功"); 和28-30行有区别
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.creatBySuccessMessage(question);
        }
        return ServerResponse.creatByErrorMessage("找回密码问题是空的");

    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //说明问题及问题答案是这个用户的,并且是正确的
            String forgetToken = UUID.randomUUID().toString();
            RedisShardedPoolUtil.setEx(Const.TOKEN_PREFIX+username,forgetToken,60*60*12);

            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.creatBySuccess(forgetToken);

        }
        return ServerResponse.creatByErrorMessage("问题答案错误");
    }

    @Override
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        System.out.println(forgetToken + "   :forgettoken");
        if (StringUtils.isBlank(forgetToken)) {
            return ServerResponse.creatByErrorMessage("参数错误,token需要传递");

        }
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //说明用户不存在 因为用户不存在时return ServerResponse.creatBySuccessMessage("校验成功"); 和28-30行有区别
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        String token = RedisShardedPoolUtil.get(Const.TOKEN_PREFIX+username);
        System.out.println(token + "   :token");

        if (StringUtils.isBlank(token)) {
            return ServerResponse.creatByErrorMessage("token无效或者过期");
        }
        if (StringUtils.equals(forgetToken, token)) {

            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.creatBySuccessMessage("修改密码成功");
            }
        } else {
            return ServerResponse.creatByErrorMessage("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.creatByErrorMessage("重置密码失败");
    }

    @Override
    public ServerResponse<String> resetPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权,要校验一下这个用户的旧密码,一定要是这个用户,我们会查询一个count(1),如果不指定，那么结果就是true了,count>0
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(passwordOld), user.getId());
        if (resultCount == 0) {
            return ServerResponse.creatByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 0) {
            return ServerResponse.creatBySuccessMessage("密码更新成功");
        }
        return ServerResponse.creatByErrorMessage("密码更新失败");

    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        //username是不能被更新的
        //email也要进行校验,校验新的email是不是已经存在,并且存在的email相同的话,不能使我们当前这个用户的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            return ServerResponse.creatByErrorMessage("email已经存在,请更换email再次尝试");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());


        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) {
            return ServerResponse.creatBySuccessMessage("更新个人信息成功", updateUser);
        }
        return ServerResponse.creatByErrorMessage("更熊个人信息失败");


    }

    @Override
    public ServerResponse<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            return ServerResponse.creatByErrorMessage("找不到当前用户");
        }
        user.setPassword(StringUtils.EMPTY); //这个地方把密码置空是因为传到前端的个人信息不会有密码的,数据库中的密码并未置空,因为没有操作userMapper的方法修改数据库
        return ServerResponse.creatBySuccess(user);
    }


    //backnend


    public ServerResponse checkAdminRole(User user) {
        /**
          * @description 
          * @params [user]
          * @return com.mmall.common.ServerResponse
          * @author 姜晓
          * @date 2017/8/25 下午8:43
          */
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) {
            return ServerResponse.creatBySuccess();

        }
        return ServerResponse.creatByError();
    }

}

