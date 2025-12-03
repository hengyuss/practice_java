package com.restkeeper.interceptor;

import com.restkeeper.utils.JWTUtil;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.handler.Handler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class WebHandlerInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    String tokenInfo = request.getHeader("Authorization");
    if (StringUtils.isNotEmpty(tokenInfo)){
      Map<String, Object> tokenMap = JWTUtil.decode(tokenInfo);
      String ShopId = (String) tokenMap.get("ShopId");
      RpcContext.getContext().setAttachment("ShopId", ShopId);
    }
    return true;
  }
}
