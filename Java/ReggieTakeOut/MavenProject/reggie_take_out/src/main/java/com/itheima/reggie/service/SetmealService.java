package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，并保存套餐与菜品关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);
}
