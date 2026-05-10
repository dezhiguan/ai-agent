-- ============================================================================
-- 三国演义答题系统 - MySQL 8.0 建表脚本
-- 引擎: InnoDB | 字符集: utf8mb4 | 排序规则: utf8mb4_unicode_ci
-- ============================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------------------------------------------------------
-- 用户表
-- ----------------------------------------------------------------------------
DROP TABLE IF EXISTS `quiz_record`;
DROP TABLE IF EXISTS `quiz_question`;
DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
  `username` VARCHAR(64) NOT NULL COMMENT '登录用户名，唯一',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（建议存储哈希值，如 bcrypt）',
  `avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像图片 URL 或存储路径',
  `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '账号创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- ----------------------------------------------------------------------------
-- 题目表
-- ----------------------------------------------------------------------------
CREATE TABLE `quiz_question` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '题目主键ID',
  `stem` TEXT NOT NULL COMMENT '题干',
  `option_a` VARCHAR(512) NOT NULL COMMENT '选项 A',
  `option_b` VARCHAR(512) NOT NULL COMMENT '选项 B',
  `option_c` VARCHAR(512) NOT NULL COMMENT '选项 C',
  `option_d` VARCHAR(512) NOT NULL COMMENT '选项 D',
  `correct_answer` ENUM('A', 'B', 'C', 'D') NOT NULL COMMENT '正确答案：A/B/C/D',
  `difficulty` ENUM('简单', '中等', '困难') NOT NULL DEFAULT '中等' COMMENT '题目难度',
  `explanation` TEXT COMMENT '答案解析',
  PRIMARY KEY (`id`),
  KEY `idx_quiz_question_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题库题目表';

-- ----------------------------------------------------------------------------
-- 答题记录表
-- ----------------------------------------------------------------------------
CREATE TABLE `quiz_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '答题记录主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '关联用户 ID（sys_user.id）',
  `score` INT NOT NULL DEFAULT 0 COMMENT '本次答题得分',
  `answered_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '答题结束/提交时间',
  `duration_seconds` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '答题耗时（秒）',
  PRIMARY KEY (`id`),
  KEY `idx_quiz_record_user_id` (`user_id`),
  KEY `idx_quiz_record_answered_at` (`answered_at`),
  CONSTRAINT `fk_quiz_record_user`
    FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户答题记录表';

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------------------------------------------------------
-- 默认本地答题用户（与前端 VITE_USER_ID=1 对应；密码占位，未接登录时可忽略）
-- ----------------------------------------------------------------------------
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `avatar`)
VALUES (1, 'local_player', 'dev-not-for-production', NULL);

-- ============================================================================
-- 预置数据：3 道关于「诸葛亮」的测试题
-- ============================================================================

INSERT INTO `quiz_question`
  (`stem`, `option_a`, `option_b`, `option_c`, `option_d`, `correct_answer`, `difficulty`, `explanation`)
VALUES
  (
    '《三国演义》中，诸葛亮的字是哪一个？',
    '孔明',
    '云长',
    '孟德',
    '翼德',
    'A',
    '简单',
    '诸葛亮，字孔明，号卧龙，琅琊阳都人。'
  ),
  (
    '「草船借箭」一回中，诸葛亮利用大雾向哪一方「借」得十万支箭？',
    '曹操',
    '孙权',
    '刘备',
    '袁绍',
    'A',
    '中等',
    '诸葛亮趁江上大雾，用草船靠近曹军水寨，曹军不明虚实以箭拒敌，箭尽落于草船之上。'
  ),
  (
    '诸葛亮为主匡扶汉室，多次北伐，传说中「六出祁山」主要对抗的是哪一政权？',
    '曹魏',
    '东吴',
    '西晋',
    '刘表',
    'A',
    '困难',
    '诸葛亮北伐主要针对占据中原的曹魏；祁山为北伐进军路线上的要地之一，《三国演义》中有六出祁山的文学描写。'
  );
