package com.restkeeper.operator.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginVo {

    @ApiModelProperty(value = "用户名")
    public String username;
    @ApiModelProperty(value = "密码")
     public String password;
}
