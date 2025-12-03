package com.restkeeper.operator.controller;

import com.restkeeper.operator.dto.ResetPwdDTO;
import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.exception.AccountException;
import com.restkeeper.operator.service.IEnterpriseAccountService;
import com.restkeeper.operator.vo.AddEnterpriseAccountVO;
import com.restkeeper.operator.vo.ResetPwdVO;
import com.restkeeper.operator.vo.UpdateEnterpriseAccountVO;
import com.restkeeper.response.vo.PageVO;
import com.restkeeper.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
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
  public Result add(@RequestBody AddEnterpriseAccountVO addEnterpriseAccountVo) {
    EnterpriseAccount enterpriseAccount = new EnterpriseAccount();

    Result result = new Result();
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
    boolean flag = enterpriseAccountService.add(enterpriseAccount);
    if (!flag) {
      return result.error("修改失败");
    } else {
      return result.ok("修改成功");
    }
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

  @ApiOperation(value = "账号删除")
  @ApiImplicitParam(paramType="path", name = "id", value = "主键", required = true, dataType = "String")
  @DeleteMapping(value = "/delete/{id}")
  public Result delete(@PathVariable("id") Integer id) {
    boolean flag = enterpriseAccountService.delete(id);
    Result result = new Result();
    if (flag) {
      return result.ok("删除成功");
    } else {
      return result.error("删除失败");
    }
  }

  @ApiOperation(value = "账号还原")
  @PutMapping(value = "/recovery/{id}")
  public Result recovery(@PathVariable("id") Integer id) {
    boolean flag = enterpriseAccountService.recovery(id);
    Result result = new Result();
    if (flag) {
      return result.ok("账号还原成功");
    } else {
      return result.error("账号还原失败");
    }
  }

  @ApiOperation(value = "账号禁用")
  @PutMapping(value = "/forbidden/{id}")
  public Result forbidden(@PathVariable("id") Integer id) {
    boolean flag = enterpriseAccountService.forbidden(id);
    Result result = new Result();
    if (flag) {
      return result.ok("账号禁用成功");
    } else {
      return result.error("账号禁用失败");
    }
  }

  @ApiOperation(value = "重置密码")
  @PutMapping(value = "/resetPwd")
  public Result resetPwd(@RequestBody ResetPwdVO resetPwdVO) {
    Result result = new Result();
    ResetPwdDTO resetPwdDTO = new ResetPwdDTO();
    BeanUtils.copyProperties(resetPwdVO, resetPwdDTO);
    boolean flag = enterpriseAccountService.resetPassword(resetPwdDTO.getId(),
        resetPwdDTO.getOldPassword(), resetPwdDTO.getNewPassword());
    if (flag){
      return result.ok("重置密码成功");
    } else {
      return result.error("重置密码失败");
    }
  }

}
