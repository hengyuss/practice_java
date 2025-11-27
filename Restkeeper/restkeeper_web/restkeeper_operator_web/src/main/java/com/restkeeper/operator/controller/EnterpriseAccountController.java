package com.restkeeper.operator.controller;

import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.operator.vo.AddEnterpriseAccountVO;
import com.restkeeper.operator.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.security.auth.login.AccountException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@Api(tags = { "企业账号管理" })
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

  @ApiOperation(value = "新增账号")
  @PutMapping("/add")
  public boolean add(@RequestBody AddEnterpriseAccountVO addEnterpriseAccountVo) {
    EnterpriseAccount enterpriseAccount = new EnterpriseAccount();

    BeanUtils.copyProperties(addEnterpriseAccountVo, enterpriseAccount);
    LocalDateTime localDateTime = LocalDateTime.now();
    enterpriseAccount.setApplicationTime(localDateTime);
    LocalDateTime expireTime = null;
    if (addEnterpriseAccountVo.getStatus() == 0) {
      expireTime = localDateTime.plusDays(7);
    }
    if (addEnterpriseAccountVo.getStatus() == 1) {
      expireTime = localDateTime.plusDays(addEnterpriseAccountVo.getValidityDay());
    }
    if (expireTime != null) {
      enterpriseAccount.setExpireTime(expireTime);
    } else {
      throw new RuntimeException("账号类型设置有误");
    }
    return enterpriseAccountService.add(enterpriseAccount);
  }

  @ApiOperation(value = "账户查看")
  @GetMapping(value = "/getById/{id}")
  public EnterpriseAccount getById(@PathVariable("id") Integer id) {
    return enterpriseAccountService.getById(id);
  }

  @ApiOperation(value = "账号编辑")
  @PutMapping(value = "/update")
  public Result update (@RequestBody UpdateEnterpriseAccountVO updateEnterpriseAccountVO)
      throws AccountException {

    Result result = new Result();
    UpdateEnterpriseDTO updateEnterpriseDTO = new UpdateEnterpriseDTO();
    BeanUtils.copyProperties(updateEnterpriseAccountVO, updateEnterpriseDTO);
    boolean flag = enterpriseAccountService.update(updateEnterpriseDTO);
    if (flag) {
      return result.error("修改失败");
    } else {
      return result.ok("修改成功");
    }

  }

}
