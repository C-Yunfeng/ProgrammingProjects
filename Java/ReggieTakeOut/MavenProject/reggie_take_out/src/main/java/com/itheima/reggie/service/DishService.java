package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {
    // 新增菜品,同时插入口味数据
    public  void saveWithFlavor(DishDto dishDto);
}
