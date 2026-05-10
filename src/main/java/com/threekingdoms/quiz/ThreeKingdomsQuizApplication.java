package com.threekingdoms.quiz;

import com.threekingdoms.quiz.config.properties.ZhugeLiangAiProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@MapperScan("com.threekingdoms.quiz.mapper")
@EnableConfigurationProperties(ZhugeLiangAiProperties.class)
public class ThreeKingdomsQuizApplication {

  public static void main(String[] args) {
    SpringApplication.run(ThreeKingdomsQuizApplication.class, args);
  }
}
