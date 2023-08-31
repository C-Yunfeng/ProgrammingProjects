package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.itheima.reggie.utils.ValidateCodeUtils;
import com.itheima.reggie.utils.SendSms;
import com.itheima.reggie.utils.SMSUtils;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

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
            // session.setAttribute(phone,code);

            // 将验证码存储到redis中,限时5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("验证码发送成功");
        }

        return R.error("验证码发送失败");
    }

    /**
     * 移动端用户登陆
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString();

        // 从Session中获取保存的验证码
        // Object codeInSession = session.getAttribute(phone);

        // 从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        // 若验证码比对成功,则登陆成功
        if(codeInSession !=null && codeInSession.equals(code)){
            // 若为新用户,则自动注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if (user==null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            // 如果不加，登陆后退回登陆页面，因为被拦截了
            session.setAttribute("user",user.getId());

            // 若登陆成功,则删除验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }

        return R.error("登陆失败");
    }

}
