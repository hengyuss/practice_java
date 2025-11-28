package com.restkeeper.operator.exception;


import lombok.Data;

@Data
public class AccountException extends RuntimeException {
  public AccountException(String message) {
    super( message );
  }
}
