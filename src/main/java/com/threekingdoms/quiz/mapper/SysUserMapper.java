package com.threekingdoms.quiz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.threekingdoms.quiz.entity.SysUser;
import org.apache.ibatis.annotations.Insert;

public interface SysUserMapper extends BaseMapper<SysUser> {

  /**
   * 确保存在 id=1 的本地答题用户（与前端默认 userId 一致）；库中已有记录则忽略。
   */
  @Insert(
      "INSERT IGNORE INTO sys_user (id, username, password, avatar) "
          + "VALUES (1, 'local_player', 'dev-not-for-production', NULL)")
  void seedDefaultQuizUser();
}
