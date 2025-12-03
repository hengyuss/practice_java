package com.restkeeper.exception;


import lombok.Data;

@Data
public class AccountException extends RuntimeException {
  public AccountException(String message) {
    super( message );
  }
}
