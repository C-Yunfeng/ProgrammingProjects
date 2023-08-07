package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;


public interface DishService extends IService<Dish> {
    // 新增菜品,同时插入口味数据
    public  void saveWithFlavor(DishDto dishDto);

    // 根据id查询菜品和口味
    public DishDto getByIdWithFlavor(Long id);

    // 更新菜品,更新口味
    public void updateWithFlavor(DishDto dishDto);
}
