package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    // 这些代码应该写道service里吧，controller直接调用
    // @RequestBody注解表示将请求体中的JSON数据自动反序列化成Employee对象,不加会空指针
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        log.info("登陆中...");

        // 1.将页面提交的password用md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2.用username查数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        // 3.查询结果为空则失败
        if(emp == null){
            return R.error("登陆失败");
        }

        // 4.密码不一致则失败
        if(!password.equals(emp.getPassword())){
            return R.error("登陆失败");
        }

        // 5.是否被禁用
        if(0 == emp.getStatus()){
            return R.error("账号已禁用");
        }

        // 6.登陆成功,返回结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    // session跟token是完全不同的东西  实际开发中一般都是用redis存放token
}
