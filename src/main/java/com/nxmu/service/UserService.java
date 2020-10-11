package com.nxmu.service;

import com.google.common.collect.Lists;
import com.nxmu.common.enums.RoleEnum;
import com.nxmu.common.utils.FormatUtil;
import com.nxmu.common.utils.PasswordUtil;
import com.nxmu.common.utils.SecurityUtil;
import com.nxmu.common.utils.TimeUtil;
import com.nxmu.exception.ResultException;
import com.nxmu.mapper.OrganizationMapper;
import com.nxmu.mapper.PasswordResetMapper;
import com.nxmu.mapper.UserMapper;
import com.nxmu.model.*;
import com.nxmu.model.request.*;
import com.nxmu.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nxmu.common.Constant.EMAIL_SUBJECT;
import static com.nxmu.common.Constant.USER_INFO;
import static com.nxmu.common.Constant.USER_TOKEN;

@Service
@Slf4j
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordResetMapper passwordResetMapper;

    @Autowired
    private OrganizationMapper organizationMapper;

    @Autowired
    private VerifyService verifyService;

    @Autowired
    private MailService mailService;

    /**
     * 获取用户信息
     *
     * @param id
     * @return
     */
    public UserVO getUser(int id) {
        User user = userMapper.selectByPrimaryKey(id);

        if (Objects.isNull(user)) {
            throw new ResultException("用戶不存在！");
        }

        Organization organization = null;
        if (!Objects.isNull(user.getOrgId())) {
            organization = organizationMapper.selectByPrimaryKey(user.getOrgId());
        }

        User createUser = null;
        if (!Objects.isNull(user.getCreateUser())) {
            createUser = userMapper.selectByPrimaryKey(user.getCreateUser());
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        userVO.setRoleDesc(RoleEnum.getRoleDesc(user.getRoleId()));
        userVO.setOrgName(Objects.isNull(organization) ? "" : organization.getOrgName());
        userVO.setCreateTime(TimeUtil.timeToDate(user.getCreateTime().getTime()));
        userVO.setCreateUserName(Objects.isNull(createUser) ? "" : createUser.getUserName());

        return userVO;
    }

    /**
     * 获取用户列表
     *
     * @param roleId
     * @return
     */
    public List<UserVO> findUserList(int roleId) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andRoleIdEqualTo(roleId)
                .andIsDelEqualTo(Boolean.FALSE);
        userExample.setOrderByClause("id");

        List<User> userList = userMapper.selectByExample(userExample);

        if (CollectionUtils.isEmpty(userList)) {
            return Lists.newArrayList();
        }

        List<Integer> orgIds = userList
                .stream()
                .map(User::getOrgId)
                .collect(Collectors.toList());

        List<Organization> organizations = null;
        if (!CollectionUtils.isEmpty(orgIds)) {
            OrganizationExample organization = new OrganizationExample();
            organization.createCriteria().andIdIn(orgIds)
                    .andIsDelEqualTo(Boolean.FALSE);
            organizations = organizationMapper.selectByExample(organization);
        }

        List<Integer> createUserIds = userList
                .stream()
                .map(User::getCreateUser)
                .collect(Collectors.toList());

        List<User> createUserList = null;
        if (!CollectionUtils.isEmpty(createUserIds)) {
            UserExample createUserExample = new UserExample();
            createUserExample.createCriteria().andIdIn(createUserIds)
                    .andIsDelEqualTo(Boolean.FALSE);
            createUserList = userMapper.selectByExample(createUserExample);
        }

        List<UserVO> userVOS = Lists.newArrayList();

        //完善用戶信息
        for(User user : userList) {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVO.setCreateTime(TimeUtil.timeToDate(user.getCreateTime().getTime()));
            userVO.setRoleDesc(RoleEnum.getRoleDesc(user.getRoleId()));

            if (!CollectionUtils.isEmpty(organizations) && !Objects.isNull(user.getOrgId())) {
                Optional<Organization> optional = organizations
                        .stream()
                        .filter(a -> a.getId().equals(user.getOrgId()))
                        .findAny();
                userVO.setOrgName(!optional.isPresent() ? "" : optional.get().getOrgName());
            }

            if (!CollectionUtils.isEmpty(createUserList) && !Objects.isNull(user.getCreateUser())) {
                Optional<User> optional = createUserList
                        .stream()
                        .filter(a -> a.getId().equals(user.getCreateUser()))
                        .findAny();
                userVO.setCreateUserName(!optional.isPresent() ? "" : optional.get().getUserName());
            }

            userVOS.add(userVO);
        }

        return userVOS;
    }

    /**
     * 登录
     *
     * @param loginRequest
     * @param request
     * @param response
     * @return
     */
    public LoginUserVO login(LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (loginRequest == null || StringUtils.isEmpty(loginRequest.getUserName())
                || StringUtils.isEmpty(loginRequest.getPassword())) {
            throw new ResultException("用户名密码不能为空！");
        }

        //验证验证码是否正确
        HttpSession session = request.getSession();
        verifyService.verifyCode(loginRequest.getVerifyCode(), session);

        //对密码进行MD5加密
        String password = SecurityUtil.getMD5(loginRequest.getPassword());

        //数据库比对用户名密码是否正确
        User user = userMapper.login(loginRequest.getUserName(), password);
        if (user == null) {
            throw new ResultException("用户名密码错误，请重新输入！");
        }

        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        loginUserVO.setUserId(user.getId());
        loginUserVO.setRoleDesc(RoleEnum.getRoleDesc(loginUserVO.getRoleId()));

        //把用户信息写到session
        loginUserVO.setToken(UUID.randomUUID().toString().replaceAll("-",""));
        session.setAttribute(USER_INFO, loginUserVO);
        session.setMaxInactiveInterval(3600);

        //把token写到cookie
        Cookie cookie = new Cookie(USER_TOKEN, loginUserVO.getToken());
        cookie.setMaxAge(3600);
        cookie.setPath("/");
        response.addCookie(cookie);

        return loginUserVO;
    }

    /**
     * 注册
     *
     * @param registerRequest
     * @param request
     * @return
     */
    public int register(RegisterRequest registerRequest, HttpServletRequest request) {
        if (registerRequest == null || StringUtils.isEmpty(registerRequest.getUserName()) || StringUtils.isEmpty(registerRequest.getPassword())
              || StringUtils.isEmpty(registerRequest.getEmail())) {
            throw new ResultException("用户名密码、邮箱不能为空！");
        }

        if (registerRequest.getRoleId() == null) {
            registerRequest.setRoleId(RoleEnum.USER.getId());
        }

        //验证验证码是否正确
        HttpSession session = request.getSession();
        verifyService.verifyCode(registerRequest.getVerifyCode(), session);

        //校验用户名格式
        if (!FormatUtil.checkUserName(registerRequest.getUserName())) {
            throw new ResultException("用户名由字母数字下划线组成且开头必须是字母，不能少于3位，不能超过31位！");
        }

        //校验密码格式
        if (!FormatUtil.checkPassword(registerRequest.getPassword())) {
            throw new ResultException("密码由字母和数字构成，不能少于6位，不能超过31位！");
        }

        //校验邮箱格式
        if(!FormatUtil.checkEmail(registerRequest.getEmail())) {
            throw new ResultException("您输入的邮箱有误，请重新输入！");
        }

        //验证用户名是否已经存在
        User user = userMapper.getUserByName(registerRequest.getUserName());
        if (user != null) {
            throw new ResultException("用户名已存在，请重新输入或者直接登录！");
        }

        //把用户注册到数据库
        user = new User();
        BeanUtils.copyProperties(registerRequest, user);
        //对密码进行MD5加密
        user.setPassword(SecurityUtil.getMD5(registerRequest.getPassword()));
        userMapper.register(user);

        //生成工号 年份+角色ID+用户ID
        user = new User().perfectUser(user.getId(), registerRequest.getRoleId());
        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 修改/完善用户信息
     *
     * @param userModifyRequest
     * @return
     */
    public int perfectUser(UserModifyRequest userModifyRequest) {
        if (userModifyRequest == null || StringUtils.isEmpty(userModifyRequest.getUserName())) {
            throw new ResultException("用户名缺失，需根据其完善个人信息！");
        }

        //校验邮箱格式
        if (!StringUtils.isEmpty(userModifyRequest.getEmail())
                && !FormatUtil.checkEmail(userModifyRequest.getEmail())) {
            throw new ResultException("请输入正确的邮箱！");
        }

        //校验qq格式
        if (!StringUtils.isEmpty(userModifyRequest.getQq())
                && !FormatUtil.checkQQ(userModifyRequest.getQq())) {
            throw new ResultException("请输入正确的QQ号！");
        }

        //校验微信格式
        if (!StringUtils.isEmpty(userModifyRequest.getWechat())
                && !FormatUtil.checkWechat(userModifyRequest.getWechat())) {
            throw new ResultException("请输入正确的微信号！");
        }

        //校验组织
        if (!Objects.isNull(userModifyRequest.getOrgId())) {
            Organization organization = organizationMapper.selectByPrimaryKey(userModifyRequest.getOrgId());
            if (Objects.isNull(organization)) {
                throw new ResultException("组织不存在！");
            }
        }

        User user = new User();
        BeanUtils.copyProperties(userModifyRequest, user);
        return userMapper.updateByUserName(user);
    }

    /**
     * 修改密码
     *
     * @param pwdResetRequest
     * @return
     */
    public int changePassword(PwdResetRequest pwdResetRequest) {
        if (pwdResetRequest == null || StringUtils.isEmpty(pwdResetRequest.getUserName())
                || StringUtils.isEmpty(pwdResetRequest.getNewPassword())) {
            throw new ResultException("用户名或者新密码缺失！");
        }

        //校验修改密码时间是否已过期
        PasswordReset passwordReset = passwordResetMapper.selectByUserName(pwdResetRequest.getUserName());
        if(passwordReset == null || passwordReset.getOutTime() <= System.currentTimeMillis()){
            throw new ResultException("链接已经过期,请重新申请找回密码！");
        }

        if(!passwordReset.getSid().equals(pwdResetRequest.getSid())) {
            throw new ResultException("链接不正确,请重新申请！");
        }

        //校验新密码格式
        if (!StringUtils.isEmpty(pwdResetRequest.getNewPassword())
                && !FormatUtil.checkPassword(pwdResetRequest.getNewPassword())) {
            throw new ResultException("密码由字母和数字构成，不能少于6位，不能超过31位！");
        }

        //对新密码进行加密
        String newPassword = SecurityUtil.getMD5(pwdResetRequest.getNewPassword());
        User user = new User(passwordReset.getUserName(), newPassword);

        //删除重置密码链接
        passwordReset.setIsDel(true);
        passwordResetMapper.delPasswordReset(passwordReset);

        //重置密码
        return userMapper.updatePasswordByUserName(user);
    }

    /**
     * 登出
     *
     * @param request
     * @param response
     */
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

    /**
     * 忘记密码
     *
     * @param request
     * @param email
     */
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

    /**
     * 添加操作员
     *
     * @param operationAddRequest
     * @return
     */
    public int addOperation(OperationAddRequest operationAddRequest, HttpServletRequest request) {

        //校验用户名格式
        if (!FormatUtil.checkUserName(operationAddRequest.getUserName())) {
            throw new ResultException("用户名由字母数字下划线组成且开头必须是字母，不能少于3位，不能超过31位！");
        }

        //校验密码格式
        if (!FormatUtil.checkPassword(operationAddRequest.getPassword())) {
            throw new ResultException("密码由字母和数字构成，不能少于6位，不能超过31位！");
        }

        //验证用户名是否已经存在
        User user = userMapper.getUserByName(operationAddRequest.getUserName());
        if (!Objects.isNull(user)) {
            throw new ResultException("用户名已存在，请重新输入或者直接登录！");
        }

        //把操作员添加到数据库
        user = new User();
        BeanUtils.copyProperties(operationAddRequest, user);
        //对密码进行MD5加密
        user.setPassword(SecurityUtil.getMD5(operationAddRequest.getPassword()));
        user.setRoleId(RoleEnum.OPERATOR.getId());
        userMapper.insertSelective(user);

        //生成工号 年份+角色ID+用户ID
        user = new User().perfectUser(user.getId(), user.getRoleId());

        //操作用户
        user.setCreateUser(getCurrentUser(request).getUserId());

        return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    private LoginUserVO getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (LoginUserVO)session.getAttribute(USER_INFO);
    }

    /**
     * 删除用户
     *
     * @param id
     * @return
     */
    public int deleteUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setIsDel(Boolean.TRUE);

       return userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * 复制用户
     *
     * @param fromUserName
     * @param toUserName
     * @param request
     * @return
     */
    public int copyUser(String fromUserName, String toUserName, HttpServletRequest request) {

        User user = userMapper.getUserByName(fromUserName);

        if (Objects.isNull(user)) {
            throw new ResultException("复制源用户不存在！");
        }

        User toUser = userMapper.getUserByName(toUserName);
        if (!Objects.isNull(toUser)) {
            throw new ResultException("复制用户已经存在！");
        }

        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setUserName(toUserName);
        newUser.setId(null);
        newUser.setJobNumber(null);
        newUser.setCreateTime(null);
        newUser.setUpdateTime(null);
        newUser.setCreateUser(null);
        userMapper.insertSelective(newUser);

        //生成工号 年份+角色ID+用户ID
        newUser = new User().perfectUser(newUser.getId(), newUser.getRoleId());

        //操作用户
        newUser.setCreateUser(getCurrentUser(request).getUserId());

        return userMapper.updateByPrimaryKeySelective(newUser);
    }
}
