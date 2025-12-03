package com.restkeeper.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Bean
  public HandlerInterceptor MyInterceptor(){
      return new WebHandlerInterceptor();
    }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    InterceptorRegistration interceptor = registry.addInterceptor(MyInterceptor());
    interceptor.addPathPatterns("/**").excludePathPatterns("/login", "/enterprise/add");
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**").allowCredentials(true).allowedOrigins("*").allowedMethods(
        "GET", "PUT", "POST", "DELETE", "OPTIONS", "PATCH"
    ).maxAge(3600);
  }
}
