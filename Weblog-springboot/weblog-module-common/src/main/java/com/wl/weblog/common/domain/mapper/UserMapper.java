package com.wl.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wl.weblog.common.domain.dos.UserDo;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * @Author WL
 * @Date 2020/11/19 21:59
 * @Description TODO
 */
public interface UserMapper extends BaseMapper<UserDo> {
    default UserDo findByUsername(String username) {
        LambdaQueryWrapper<UserDo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDo::getUsername, username);
        return selectOne(wrapper);
    }

    default  int updatePasswordByUsername(String username,String password){
        LambdaUpdateWrapper<UserDo> wrapper = new LambdaUpdateWrapper<>();
        // 设置要更新的字段
        wrapper.set(UserDo::getPassword, password);
        wrapper.set(UserDo::getUpdateTime, LocalDateTime.now());
        //更新条件
        wrapper.eq(UserDo::getUsername, username);

        return update(null, wrapper);
    }
}
