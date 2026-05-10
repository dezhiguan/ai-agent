-- 若库表已存在但缺少 id=1 用户，执行本脚本一次即可（与前端 VITE_USER_ID=1 对应）。
INSERT IGNORE INTO `sys_user` (`id`, `username`, `password`, `avatar`)
VALUES (1, 'local_player', 'dev-not-for-production', NULL);
