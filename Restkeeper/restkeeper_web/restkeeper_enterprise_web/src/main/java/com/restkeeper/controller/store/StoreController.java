package com.restkeeper.controller.store;

import com.restkeeper.response.vo.PageVO;
import com.restkeeper.shop.entity.Store;
import com.restkeeper.shop.service.IStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Api(tags = {"门店信息"})
@RequestMapping("/store")
public class StoreController {

  @Reference(version = "1.0.0", check = false)
  private IStoreService storeService;


  /**
   * 分页数据
   */

  @ApiOperation(value = "分页查询所有门店")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "path", name = "page", value = "当前页码", required = true, dataType = "Integer"),
      @ApiImplicitParam(paramType = "path", name = "pageSize", value = "分大小", required = true, dataType = "Integer"),
      @ApiImplicitParam(paramType = "query", name = "name", value = "门店名称", required = false, dataType = "String")})
  @GetMapping(value = "/pageList/{page}/{pageSize}")
  public PageVO<Store> findListByPage(@PathVariable int page,
      @PathVariable int pageSize,
      @RequestParam(value = "name", required = false) String name) {
    return new PageVO<Store>(storeService.queryPageByName(page, pageSize, name));
  }
}
