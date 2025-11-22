package com.restkeeper.utils;

import lombok.Data;

/**
 * 返回结果通用封装
 */
@Data
public class Result {
    // 返回状态
    private int status;
    // 状态描述
    private String desc;
    // 返回数据
    private Object data;

    private String token;


   public Result ok(Object data){
       this.data=ResultCode.success;
       this.status=200;
       this.desc="ok";
       return this;
   };

   public Result error(String msg){
       this.status=ResultCode.error;
       this.desc=msg;
       return this;
   };
}