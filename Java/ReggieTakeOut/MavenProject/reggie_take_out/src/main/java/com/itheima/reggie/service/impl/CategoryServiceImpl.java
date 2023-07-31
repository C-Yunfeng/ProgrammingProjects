package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分配
     * @param id
     */
    @Override
    public void remove(Long id) {
        // 若菜品中正在使用,则异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int cnt_dish = dishService.count(dishLambdaQueryWrapper);
        if( cnt_dish > 0 ){
            // 使用中,异常
            throw new CustomException("当前分类关联了菜品,不能删除");
        }
        // 若套餐中正在使用,则异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int cnt_setmeal = setmealService.count(setmealLambdaQueryWrapper);
        if (cnt_setmeal>0){
            // 使用中,抛出异常
            throw new CustomException("当前分类关联了套餐,不能删除");
        }
        // 否则可以删除分类
        super.removeById(id);
    }
}
