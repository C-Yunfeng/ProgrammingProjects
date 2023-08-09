package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.itheima.reggie.utils.ValidateCodeUtils;
import com.itheima.reggie.utils.SendSms;
import com.itheima.reggie.utils.SMSUtils;

import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        // 获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)){
            // 生成4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info(code);

            // 调用aliyun短信服务
            // SendSms.main();

            // 将生成的验证码保存到session
            session.setAttribute(phone,code);

            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

}
