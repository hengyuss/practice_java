package com.restkeeper.operator.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restkeeper.operator.entity.EnterpriseAccount;
import com.restkeeper.operator.mapper.EnterpriseAccountMapper;
import io.netty.util.internal.StringUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Service(version = "1.0.0",protocol = "dubbo")
@RefreshScope
public class EnterpriseAccountServiceImpl extends ServiceImpl<EnterpriseAccountMapper, EnterpriseAccount> implements IEnterpriseAccountService{

    @Override
    public IPage<EnterpriseAccount> queryPageByName(int pageNum, int pageSize, String name) {
        QueryWrapper<EnterpriseAccount> queryWrapper = new QueryWrapper<>();
        IPage<EnterpriseAccount>  page = new Page<>(pageNum, pageSize);
        if(StringUtil.isNullOrEmpty(name)){
            queryWrapper.like("enterprise_name",name);
        }
        return this.page(page, queryWrapper);
    }
}
