package com.wl.weblog.web;

import com.wl.weblog.common.domain.dos.UserDo;
import com.wl.weblog.common.domain.mapper.UserMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class WeblogWebApplicationTests {
    @Autowired
    private UserMapper userMapper;


    @Test
    void contextLoads() {
    }

    @Test
    void testLog() {
        log.trace("trace");
        log.debug("debug");
        log.info("info");
        log.warn("warn");
        log.error("error");

        //占位符
        String name = "wl";
        log.info("name: {}", name);
    }

    @Test
    void insertTest() {
        // 构建数据库实体类
        UserDo userDo = UserDo.builder()
                .username("wl12")
                .password("123456")
                .createTime(new Date())
                .updateTime(new Date())
                .isDeleted(false)
                .build();

        userMapper.insert(userDo);
    }
}
