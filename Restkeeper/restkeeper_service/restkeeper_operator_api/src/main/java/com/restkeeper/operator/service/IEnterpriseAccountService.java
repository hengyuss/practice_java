package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.dto.UpdateEnterpriseDTO;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.exception.AccountException;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {
    IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String name);

    boolean add(EnterpriseAccount account);

    boolean update(UpdateEnterpriseDTO updateEnterpriseDTO) throws AccountException;

    boolean delete(Integer id);

    boolean recovery(Integer id);

    boolean forbidden(Integer id);

    boolean resetPassword(Integer id, String OldPassword, String NewPassword);
}
