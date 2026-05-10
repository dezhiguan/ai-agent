package com.threekingdoms.quiz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("sys_user")
public class SysUser {

  @TableId(type = IdType.AUTO)
  private Long id;

  private String username;

  private String password;

  private String avatar;

  @TableField("created_at")
  private LocalDateTime createdAt;
}
