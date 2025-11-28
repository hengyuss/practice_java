package com.restkeeper.operator.vo;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ResetPwdVO {
  private Integer id;
  private String oldPassword;
  private String newPassword;

}
