package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.exception.AccountException;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import com.restkeeper.utils.AccountStatus;
import com.restkeeper.utils.MD5CryptUtil;
import io.netty.util.internal.StringUtil;
import java.time.LocalDateTime;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;

@Service(version = "1.0.0", protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount> implements IEnterpriseAccountService {

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
        } catch (Exception e){
            flag = false;
            throw e;
        }
        return flag;
    }

  @Override
  public boolean update(UpdateEnterpriseDTO updateEnterpriseDTO) throws AccountException {

    EnterpriseAccount enterpriseAccount = this.getById(
        updateEnterpriseDTO.getEnterpriseId());
    if (enterpriseAccount == null){
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
    return flag;
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
        if(account != null){
            this.getShopId();
        }
        return shopId;
    }
}
