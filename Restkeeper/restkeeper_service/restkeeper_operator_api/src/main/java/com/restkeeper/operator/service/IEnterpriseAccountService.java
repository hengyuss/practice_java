package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import javax.security.auth.login.AccountException;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {
    IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String name);

    boolean add(EnterpriseAccount account);

    boolean update(UpdateEnterpriseDTO updateEnterpriseDTO) throws AccountException;
}
