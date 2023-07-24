package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.service.impl.EmployeeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        // 1.清理session的ID
        request.getSession().removeAttribute("employee");
        return R.success("推出成功");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息,{}",employee.toString());


        // 设置员工信息
        // String name = (String) request.getSession().getAttribute("name");
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long)request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    // 使用mybatisplus封装的Page泛型
    public R<Page> page(int page,int pageSize, String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);

        // 分页构造器
        Page pageInfo = new Page(page, pageSize);

        // 条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        // 执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());

        Long empId = (Long) request.getSession().getAttribute("employee");

        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);

        return R.success("员工信息修改成功");
    }
}
