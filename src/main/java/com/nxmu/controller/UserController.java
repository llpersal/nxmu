package com.nxmu.controller;

import com.nxmu.exception.ApiResponse;
import com.nxmu.model.request.*;
import com.nxmu.model.vo.LoginUserVO;
import com.nxmu.service.UserService;
import com.nxmu.service.VerifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.nxmu.common.Route.*;

@RestController
@Api(tags = "用户管理接口", description = "用户管理接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerifyService verifyService;

    @RequestMapping(value = USER_INFO, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息")
    public ApiResponse userInfo(@RequestParam(defaultValue = "0") Integer userId) {
        return ApiResponse.success(userService.getUser(userId));
    }

    @RequestMapping(value = USER_LIST, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "获取用户列表")
    public ApiResponse userList(@RequestParam(defaultValue = "1") Integer roleId) {
        return ApiResponse.success(userService.findUserList(roleId));
    }

    @RequestMapping(value = USER_VERIFY_CODE, method = RequestMethod.GET)
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    public ApiResponse verifyCode(HttpServletRequest request, HttpServletResponse response) {
        //verifyService.getVerifyCode(request, response);
        return ApiResponse.success(verifyService.generateVerifyCode(request));
    }

    @RequestMapping(value = USER_LOGIN, method = RequestMethod.POST)
    @ApiOperation(value = "登录", notes = "登录")
    public ApiResponse userLogin(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        LoginUserVO user = userService.login(loginRequest, request, response);
        return ApiResponse.success(user);
    }

    @RequestMapping(value = USER_LOGOFF, method = RequestMethod.GET)
    @ApiOperation(value = "登出", notes = "登出")
    public ApiResponse userLoginOff(HttpServletRequest request, HttpServletResponse response) {
        userService.logoff(request, response);
        return ApiResponse.success();
    }

    @RequestMapping(value = USER_REGISTER, method = RequestMethod.POST)
    @ApiOperation(value = "注册", notes = "注册")
    public ApiResponse userRegister(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        return ApiResponse.success(userService.register(registerRequest, request));
    }

    @RequestMapping(value = USER_PERFECT, method = RequestMethod.POST)
    @ApiOperation(value = "完善用户信息", notes = "根据用户名完善用户信息")
    public ApiResponse userPerfect(@RequestBody UserModifyRequest user) {
        return ApiResponse.success(userService.perfectUser(user));
    }

    @RequestMapping(value = USER_FORGET_PASSWORD, method = RequestMethod.POST)
    @ApiOperation(value = "忘记密码发送邮件", notes = "忘记密码发送邮件")
    public ApiResponse userRegister(@RequestParam String email, HttpServletRequest request) {
        userService.forgetPassword(request, email);
        return ApiResponse.success();
    }

    @RequestMapping(value = USER_CHANGE_PASSWORD, method = RequestMethod.POST)
    @ApiOperation(value = "修改密码", notes = "修改密码")
    public ApiResponse modifyPassword(@RequestBody PwdResetRequest pwdResetRequest) {
        userService.changePassword(pwdResetRequest);
        return ApiResponse.success();
    }

    @RequestMapping(value = USER_OPERATION_ADD, method = RequestMethod.POST)
    @ApiOperation(value = "添加操作员", notes = "添加操作员")
    public ApiResponse addOperation(@RequestBody OperationAddRequest operationAddRequest,
                                    HttpServletRequest request) {
        return ApiResponse.success(userService.addOperation(operationAddRequest, request));
    }

    @RequestMapping(value = USER_DELETE, method = RequestMethod.POST)
    @ApiOperation(value = "刪除用戶", notes = "逻辑刪除用戶")
    public ApiResponse deleteUser(@RequestParam Integer id) {
        return ApiResponse.success(userService.deleteUser(id));
    }

    @RequestMapping(value = USER_COPY, method = RequestMethod.POST)
    @ApiOperation(value = "复制用户", notes = "复制用户")
    public ApiResponse copyUser(@RequestBody UserCopyRequest userCopyRequest, HttpServletRequest request) {
        return ApiResponse.success(userService.copyUser(userCopyRequest.getFromUserName(),
                userCopyRequest.getToUserName(), request));
    }
}
