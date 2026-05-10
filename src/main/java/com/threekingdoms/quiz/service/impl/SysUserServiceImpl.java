package com.threekingdoms.quiz.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.threekingdoms.quiz.entity.SysUser;
import com.threekingdoms.quiz.mapper.SysUserMapper;
import com.threekingdoms.quiz.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {}
