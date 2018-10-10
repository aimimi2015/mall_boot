package com.mall_boot.controller.protal;

import com.mall_boot.Exception.RegisterException;
import com.mall_boot.common.Const;
import com.mall_boot.common.ResponseCode;
import com.mall_boot.common.ResultEnum;
import com.mall_boot.common.ServerResponse;
import com.mall_boot.pojo.User;
import com.mall_boot.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by jiangxiao on 2018/9/28.
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "用户登录接口", description = "用户登录，返回状态信息")
public class UserController {


    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ApiOperation(value = "注册", notes = "用户注册")
    public ServerResponse<String> register(@Valid User user, BindingResult bindingResult) {
        //        BindingResult 表单验证后返回的一个对象
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, user={}", user);
            throw new RegisterException(ResultEnum.PARAM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }

        return userService.register(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiOperation(value = "登录", notes = "获取登录信息")
    public ServerResponse<User> login(HttpSession session, String username, String password) {

        ServerResponse<User> response = userService.login(username, password);
        if (response.isSuccess()) {

            session.setAttribute(Const.CURRENT_USER, response.getData());

//            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2String(response.getData()), Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        return response;
    }


    @RequestMapping(value = "/check_valid", method = RequestMethod.POST)
    @ApiOperation(value = "校验", notes = "根据type校验str，目前支持email和username")
    public ServerResponse<String> checkValid(String str, String type) {
        return userService.checkValid(str, type);
    }


    @RequestMapping(value = "/get_user_info", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) {
            return ServerResponse.createBySuccess(user);
        }
        return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
    }


    @RequestMapping(value = "/forget_get_question", method = RequestMethod.POST)
    @ApiOperation(value = "获取重置密码问题", notes = "根据用户名获取用户的重置密码问题")
    public ServerResponse<String> forgetGetQuestion(String username) {
        return userService.selectQuestion(username);
    }


    @RequestMapping(value = "/forget_check_answer", method = RequestMethod.POST)
    @ApiOperation(value = "校验答案", notes = "校验获取用户问题的答案正确性")
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer) {
        return userService.checkAnswer(username, question, answer);
    }


    @RequestMapping(value = "/forget_reset_password", method = RequestMethod.POST)
    @ApiOperation(value = "重置密码", notes = "忘记密码状态下")
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {
        return userService.forgetResetPassword(username, passwordNew, forgetToken);
    }


    @RequestMapping(value = "/reset_password", method = RequestMethod.POST)
    @ApiOperation(value = "重置密码", notes = "登录状态下")
    public ServerResponse<String> resetPassword(HttpSession session, String passwordOld, String passwordNew) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return userService.resetPassword(passwordOld, passwordNew, user);
    }


    @RequestMapping(value = "/update_information", method = RequestMethod.POST)
    @ApiOperation(value = "更新信息", notes = "更新用户信息")
    public ServerResponse<User> update_information(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response = userService.updateInformation(user);
        if (response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    @RequestMapping(value = "/get_information", method = RequestMethod.POST)
    @ApiOperation(value = "获取信息", notes = "获取用户信息")
    public ServerResponse<User> get_information(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录status=10");
        }
        return userService.getInformation(currentUser.getId());
    }


}
