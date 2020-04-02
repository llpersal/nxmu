package com.nxmu.service;

import com.nxmu.common.enums.RoleEnum;
import com.nxmu.common.utils.FormatUtil;
import com.nxmu.common.utils.PasswordUtil;
import com.nxmu.common.utils.SecurityUtil;
import com.nxmu.exception.ResultException;
import com.nxmu.mapper.PasswordResetMapper;
import com.nxmu.mapper.UserMapper;
import com.nxmu.model.PasswordReset;
import com.nxmu.model.User;
import com.nxmu.model.vo.LoginInfo;
import com.nxmu.model.vo.LoginUser;
import com.nxmu.model.vo.PwdReset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.UUID;

import static com.nxmu.common.Constant.EMAIL_SUBJECT;
import static com.nxmu.common.Constant.USER_INFO;
import static com.nxmu.common.Constant.USER_TOKEN;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordResetMapper passwordResetMapper;

    public User getUser(int id) {
        User user = userMapper.selectByPrimaryKey(id);
        user.setRoleDesc(RoleEnum.getRoleDesc(user.getRoleId()));

        return user;
    }

    public LoginUser login(LoginInfo loginInfo, HttpServletRequest request, HttpServletResponse response) {
        if (loginInfo == null || StringUtils.isEmpty(loginInfo.getUserName()) || StringUtils.isEmpty(loginInfo.getPassword())) {
            throw new ResultException("用户名密码不能为空！");
        }

        //验证验证码是否正确
        HttpSession session = request.getSession();
        verifyService.verifyCode(loginInfo.getVerifyCode(), session);

        //对密码进行MD5加密
        String password = SecurityUtil.getMD5(loginInfo.getPassword());

        //数据库比对用户名密码是否正确
        User user = userMapper.login(loginInfo.getUserName(), password);
        if (user == null) {
            throw new ResultException("用户名密码错误，请重新输入！");
        }

        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser);
        loginUser.setUserId(user.getId());
        loginUser.setRoleDesc(RoleEnum.getRoleDesc(loginUser.getRoleId()));

        //把用户信息写到session
        loginUser.setToken(UUID.randomUUID().toString().replaceAll("-",""));
        session.setAttribute(USER_INFO, loginUser);
        session.setMaxInactiveInterval(3600);

        //把token写到cookie
        Cookie cookie = new Cookie(USER_TOKEN, loginUser.getToken());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);

        return loginUser;
    }

    public int register(LoginInfo loginInfo, HttpServletRequest request) {
        if (loginInfo == null || StringUtils.isEmpty(loginInfo.getUserName()) || StringUtils.isEmpty(loginInfo.getPassword())
              || StringUtils.isEmpty(loginInfo.getEmail())) {
            throw new ResultException("用户名密码、邮箱不能为空！");
        }

        if (loginInfo.getRoleId() == null) {
            loginInfo.setRoleId(RoleEnum.USER.getId());
        }

        //验证验证码是否正确
        HttpSession session = request.getSession();
        verifyService.verifyCode(loginInfo.getVerifyCode(), session);

        //校验用户名格式
        if (!FormatUtil.checkUserName(loginInfo.getUserName())) {
            throw new ResultException("用户名由字母数字下划线组成且开头必须是字母，不能少于3位，不能超过31位！");
        }

        //校验密码格式
        if (!FormatUtil.checkPassword(loginInfo.getPassword())) {
            throw new ResultException("密码由字母和数字构成，不能少于6位，不能超过31位！");
        }

        //校验邮箱格式
        if(!FormatUtil.checkEmail(loginInfo.getEmail())) {
            throw new ResultException("您输入的邮箱有误，请重新输入！");
        }

        //验证用户名是否已经存在
        User user = userMapper.getUserByName(loginInfo.getUserName());
        if (user != null) {
            throw new ResultException("用户名已存在，请重新输入或者直接登录！");
        }

        //把用户注册到数据库
        user = new User();
        BeanUtils.copyProperties(loginInfo, user);
        //对密码进行MD5加密
        user.setPassword(SecurityUtil.getMD5(loginInfo.getPassword()));
        userMapper.register(user);

        //生成工号 年份+角色ID+用户ID
        user = new User().perfectUser(user.getId(), loginInfo.getRoleId());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    public int perfectUser(User user) {
        if (user == null || StringUtils.isEmpty(user.getUserName())) {
            throw new ResultException("用户名缺失，需根据其完善个人信息！");
        }

        //校验邮箱格式
        if (!StringUtils.isEmpty(user.getEmail()) && !FormatUtil.checkEmail(user.getEmail())) {
            throw new ResultException("请输入正确的邮箱！");
        }

        //校验qq格式
        if (!StringUtils.isEmpty(user.getQq()) && !FormatUtil.checkQQ(user.getQq())) {
            throw new ResultException("请输入正确的QQ号！");
        }

        //校验微信格式
        if (!StringUtils.isEmpty(user.getWechat()) && !FormatUtil.checkWechat(user.getWechat())) {
            throw new ResultException("请输入正确的微信号！");
        }

        return userMapper.updateByUserName(user);
    }

    public int changePassword(PwdReset pwdReset) {
        if (pwdReset == null || StringUtils.isEmpty(pwdReset.getUserName())
                || StringUtils.isEmpty(pwdReset.getNewPassword())) {
            throw new ResultException("用户名或者新密码缺失！");
        }

        //校验修改密码时间是否已过期
        PasswordReset passwordReset = passwordResetMapper.selectByUserName(pwdReset.getUserName());
        if(passwordReset == null || passwordReset.getOutTime() <= System.currentTimeMillis()){
            throw new ResultException("链接已经过期,请重新申请找回密码！");
        }

        if(!passwordReset.getSid().equals(pwdReset.getSid())) {
            throw new ResultException("链接不正确,请重新申请！");
        }

        //校验新密码格式
        if (!StringUtils.isEmpty(pwdReset.getNewPassword())
                && !FormatUtil.checkPassword(pwdReset.getNewPassword())) {
            throw new ResultException("密码由字母和数字构成，不能少于6位，不能超过31位！");
        }

        //对新密码进行加密
        String newPassword = SecurityUtil.getMD5(pwdReset.getNewPassword());
        User user = new User(passwordReset.getUserName(), newPassword);

        //删除重置密码链接
        passwordReset.setIsDel(true);
        passwordResetMapper.delPasswordReset(passwordReset);

        //重置密码
        return userMapper.updatePasswordByUserName(user);
    }

    public void logoff(HttpServletRequest request, HttpServletResponse response) {
        //清空session
        HttpSession session = request.getSession();
        session.invalidate();

        //清空cookie
        Cookie cookie = new Cookie(USER_TOKEN,null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void forgetPassword(HttpServletRequest request, String email) {
        if (StringUtils.isEmpty(email)) {
            throw new ResultException("请输入邮箱！");
        }

        if (!FormatUtil.checkEmail(email)) {
            throw new ResultException("您输入的邮箱有误，请重新输入！");
        }

        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new ResultException("不存在该邮箱用户，请先注册或联系管理员！");
        }

        //生成忘记密码链接
        String secretKey = UUID.randomUUID().toString(); // 密钥
        Timestamp outDate = new Timestamp(System.currentTimeMillis() + 30 * 60 * 1000);// 30分钟后过期
        long date = outDate.getTime() / 1000 * 1000;// 忽略毫秒数  mySql 取出时间是忽略毫秒数的

        String key = user.getUserName() + "$" + date + "$" + secretKey;
        String digitalSignature = PasswordUtil.getMD5String(key);// 数字签名

        //保存到数据库
        PasswordReset passwordReset = new PasswordReset(user.getUserName(), digitalSignature, date);
        passwordResetMapper.initPasswordReset(passwordReset);

        String path = request.getContextPath();
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
        String resetPassHref = basePath + "checkLink?sid="
                + digitalSignature + "&userName=" + user.getUserName();
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href="
                + resetPassHref + " target='_BLANK'>" + resetPassHref
                + "</a>  或者    <a href=" + resetPassHref
                + " target='_BLANK'>点击我重新设置密码</a>"
                + "<br/>tips:本邮件超过30分钟,链接将会失效，需要重新申请'找回密码'" + key
                + "\t" + digitalSignature;

        //发送忘记密码邮件
        try {
            mailService.sendHtmlMail(email, EMAIL_SUBJECT, emailContent);
        } catch (Exception e) {
            log.error("forgetPassword Error email:{} ", email, e);
            throw new ResultException("邮件发送失败，请联系管理员！");
        }
    }
}
