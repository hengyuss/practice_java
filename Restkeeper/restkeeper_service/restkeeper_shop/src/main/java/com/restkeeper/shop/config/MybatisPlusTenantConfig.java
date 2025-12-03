package com.restkeeper.shop.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantHandler;
import com.baomidou.mybatisplus.extension.plugins.tenant.TenantSqlParser;
import com.google.common.collect.Lists;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusTenantConfig {

  private static final String SYSTEM_TENANT_ID = "shop_id";
  private static final List<String> IGNORE_TENANT_TABLES = Lists.newArrayList("");

  @Bean
  public PaginationInterceptor paginationInterceptor() {
    System.out.println("paginationInterceptor---------------------");
    PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
    TenantSqlParser tenantSqlParser = new TenantSqlParser().setTenantHandler(new TenantHandler() {

      @Override
      public Expression getTenantId(boolean where) {
        String shopId = RpcContext.getContext().getAttachment("shopId");
        if (null == shopId) {
          throw new RuntimeException("shopId is null");
        }
        return new StringValue(shopId);
      }

      @Override
      public String getTenantIdColumn() {
        return SYSTEM_TENANT_ID;
      }

      @Override
      public boolean doTableFilter(String tableName) {
        return IGNORE_TENANT_TABLES.stream().anyMatch(e -> e.equalsIgnoreCase(tableName));
      }
    });
    paginationInterceptor.setSqlParserList(Lists.newArrayList(tenantSqlParser));
    return paginationInterceptor;
  }

}
