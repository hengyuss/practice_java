package com.restkeeper.operator.dto;

import lombok.Data;

@Data
public class ResetPwdDTO {

  private Integer id;
  private String oldPassword;
  private String newPassword;
}
