package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.operator.entity.EnterpriseAccount;

public interface IEnterpriseAccountService extends IService<EnterpriseAccount> {
    IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String name);
}
