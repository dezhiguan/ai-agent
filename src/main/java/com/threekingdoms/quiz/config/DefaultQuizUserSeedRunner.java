package com.threekingdoms.quiz.config;

import com.threekingdoms.quiz.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "app.seed.default-quiz-user",
    havingValue = "true",
    matchIfMissing = true)
public class DefaultQuizUserSeedRunner implements ApplicationRunner {

  private final SysUserMapper sysUserMapper;

  @Override
  public void run(ApplicationArguments args) {
    sysUserMapper.seedDefaultQuizUser();
  }
}
