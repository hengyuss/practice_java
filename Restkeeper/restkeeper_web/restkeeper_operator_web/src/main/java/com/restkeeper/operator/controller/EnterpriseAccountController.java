package com.restkeeper.operator.controller;

import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.response.vo.PageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api(tags = {"企业账号管理"})
@RestController
@RequestMapping("/enterprise")
public class EnterpriseAccountController {

  @Reference(version = "1.0.0", check = false)
  private IEnterpriseAccountService enterpriseAccountService;

  @ApiOperation(value = "查询企业账户(支持分页)")
  @GetMapping(value = "/pageList/{page}/{pageSize}")
  public PageVO<EnterpriseAccount> findListByPage(
      @PathVariable("page") int page,
      @PathVariable("pageSize") int pageSize,
      @RequestParam(value = "enterpriseName", required = false) String name) {

    return new PageVO<EnterpriseAccount>(
        enterpriseAccountService.queryPageByName(page, pageSize, name));
  }
}
