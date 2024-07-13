package com.wl.weblog.common.domain.dos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.util.Date;

/**
 * @Author WL
 * @Date 2020/11/19 21:59
 * @Description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user")
public class UserDo {

        @TableId(type = IdType.AUTO)
        private Long id;

        private String username;

        private String password;

        private Date createTime;

        private Date updateTime;

        private Boolean isDeleted;
}
