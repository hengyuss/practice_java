package com.restkeeper.shop.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.restkeeper.shop.entity.Store;

public interface IStoreService extends IService<Store> {
  IPage<Store> queryPageByName(int pageNo, int pageSize, String name);
}
