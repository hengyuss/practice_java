package com.restkeeper.operator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.restkeeper.constants.SystemCode;
import com.restkeeper.operator.config.RabbitMQConfig;
import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.exception.AccountException;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.sms.SmsObject;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.JWTUtil;
import com.restkeeper.utils.MD5CryptUtil;
import com.restkeeper.utils.Result;
import com.restkeeper.utils.ResultCode;
import io.netty.util.internal.StringUtil;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

@Service(version = "1.0.0", protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends
    ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount> implements IEnterpriseAccountService {

  @Autowired
  private RabbitTemplate rabbitTemplate;

  @Value("${gateway.secret}")
  private String secret;

  @Override
  public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String name) {
    QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
    IPage<EnterpriseAccount> page = new Page<>(pageNum, pageSize);
    if (StringUtil.isNullOrEmpty(name)) {
      queryWrapper.like("enterprise_name", name);
    }
    return this.page(page, queryWrapper);
  }

  @Override
  @Transactional
  public boolean add(EnterpriseAccount account) {
    boolean flag = true;
    try {
      String shopId = getShopId();
      account.setShopId(shopId);
      String pwd = RandomStringUtils.randomNumeric(6);
      account.setPassword(Md5Crypt.md5Crypt(pwd.getBytes()));
      this.save(account);
      sendMessage(account.getPhone(), account.getShopId(), pwd);
    } catch (Exception e) {
      flag = false;
    }
    return flag;
  }

  @Override
  public boolean update(UpdateEnterpriseDTO updateEnterpriseDTO) throws AccountException {

    EnterpriseAccount enterpriseAccount = this.getById(
        updateEnterpriseDTO.getEnterpriseId());
    if (enterpriseAccount == null) {
      throw new AccountException("修改账户不存在");
    }

    if (updateEnterpriseDTO.getStatus() != null) {
      if (enterpriseAccount.getStatus() == 1 && updateEnterpriseDTO.getStatus() == 0) {
        throw new AccountException("不能将正式账号改为使用账号");
      }
    }

    if (updateEnterpriseDTO.getStatus() == 0 && enterpriseAccount.getStatus() == 1) {
      addAccountExpireTimeDays(enterpriseAccount, updateEnterpriseDTO.getValidityDay());
      enterpriseAccount.setApplicationTime(LocalDateTime.now());
    }

    if (updateEnterpriseDTO.getStatus() == 1 && enterpriseAccount.getStatus() == 1) {
      addAccountExpireTimeDays(enterpriseAccount, updateEnterpriseDTO.getValidityDay());
    }

    BeanUtils.copyProperties(updateEnterpriseDTO, enterpriseAccount);

    boolean flag = this.updateById(enterpriseAccount);

    return flag;
  }

  @Override
  public boolean delete(Integer id) {
    boolean flag = this.delete(id);
    return flag;
  }

  @Override
  @Transactional
  public boolean recovery(Integer id) {
    return this.recovery(id);
  }

  @Override
  public boolean forbidden(Integer id) {
    EnterpriseAccount enterpriseAccount = this.getById(id);
    if (enterpriseAccount == null) {
      throw new AccountException("账号不存在");
    }
    enterpriseAccount.setStatus(AccountStatus.Forbidden.getStatus());
    boolean flag = this.updateById(enterpriseAccount);
    return flag;
  }

  @Override
  public boolean resetPassword(Integer id, String oldPassword, String newPassword) {
    EnterpriseAccount enterpriseAccount = this.getById(id);
    if (newPassword == null) {
      throw new AccountException("新密码不能为空");
    }
    if (enterpriseAccount == null) {
      throw new AccountException("账号不存在");
    }
    String oldPasswordMd5 = Md5Crypt.md5Crypt(oldPassword.getBytes());
    if (!oldPasswordMd5.equals(enterpriseAccount.getPassword())) {
      throw new AccountException("原始密码错误");
    }

    String newPasswordMd5 = Md5Crypt.md5Crypt(newPassword.getBytes());
    enterpriseAccount.setPassword(newPasswordMd5);
    boolean flag = this.updateById(enterpriseAccount);
    sendMessage(enterpriseAccount.getPhone(), enterpriseAccount.getShopId(), newPassword);
    return flag;
  }

  @Override
  public Result login(String shopId, String telephone, String loginPass) {
    if (StringUtils.isEmpty(shopId) || StringUtils.isEmpty(loginPass)) {
      throw new AccountException("用户名或密码为空");
    }

    QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
    queryWrapper.lambda().eq(EnterpriseAccount::getShopId, shopId);
    queryWrapper.lambda().eq(EnterpriseAccount::getPhone, telephone);

    queryWrapper.lambda().notIn(EnterpriseAccount::getStatus, AccountStatus.Forbidden.getStatus());
    EnterpriseAccount account = this.getOne(queryWrapper);
    if (account == null) {
      throw new AccountException("账号不存在");
    }
    String salts = MD5CryptUtil.getSalts(account.getPassword());
    boolean password_verify_result = Md5Crypt.md5Crypt(loginPass.getBytes(), salts)
        .equals(account.getPassword());
    if (!password_verify_result) {
      throw new AccountException("密码不正确");
    }

    Map<String, Object> tokenInfo = Maps.newHashMap();
    tokenInfo.put("shopId", account.getShopId());
    tokenInfo.put("loginName", account.getEnterpriseName());
    tokenInfo.put("userType", SystemCode.USER_TYPE_SHOP);
    String token = null;
    try {
      token = JWTUtil.createJWTByObj(tokenInfo, secret);
    } catch (Exception e) {
      throw new AccountException("令牌生成失败");
    }
    return Result.builder().status(ResultCode.success)
        .desc("ok")
        .token(token)
        .data(account)
        .build();

  }

  public void sendMessage(String phone, String shopId, String pwd) {
    SmsObject smsObject = new SmsObject();
    smsObject.setPhoneNumber(phone);
//        smsObject.setSignName(signName);
//        smsObject.setTemplateCode(templateCode);
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("shopId", shopId);
    jsonObject.put("password", pwd);
    smsObject.setTemplateJsonParam(jsonObject.toJSONString());

    rabbitTemplate.convertAndSend(RabbitMQConfig.ACCOUNT_QUEUE, JSON.toJSONString(smsObject));
  }


  private void addAccountExpireTimeDays(EnterpriseAccount enterpriseAccount, int days) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expireTime = now.plusDays(days);
    enterpriseAccount.setExpireTime(expireTime);
  }

  private String getShopId() {
    String shopId = RandomStringUtils.randomNumeric(8);
    QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("shop_id", shopId);
    EnterpriseAccount account = this.getOne(queryWrapper);
    if (account != null) {
      this.getShopId();
    }
    return shopId;
  }
}
