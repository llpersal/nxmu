package com.nxmu.service;

import com.nxmu.common.utils.VerifyCode;
import com.nxmu.exception.ResultException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.nxmu.common.Constant.VERIFY_CODE_KEY;

@Service
public class VerifyService {

    public void getVerifyCode(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/png");
        response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);

        try {
            //服务器自动创建输出流，目标指向浏览器
            VerifyCode randomValidateCode = new VerifyCode();
            randomValidateCode.getRandcode(request, response);
        } catch (Exception e) {
            throw new ResultException("获取验证码失败，请重试！");
        }
    }

    public void verifyCode(String verifyCode, HttpSession session) {
        if (StringUtils.isEmpty(verifyCode)) {
            throw new ResultException("请输入验证码！");
        }

        String loginCode = (String) session.getAttribute(VERIFY_CODE_KEY);
        if (!verifyCode.equals(loginCode)) {
            throw new ResultException("验证码不正确，请重新输入！");
        }
    }
}
