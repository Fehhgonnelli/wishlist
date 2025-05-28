package com.wishlist.exception;

import java.io.Serial;

public class NoItemsDeletedException extends Exception {

  @Serial
  private static final long serialVersionUID = 1149241039409861914L;

  public NoItemsDeletedException(String msg) {
    super(msg);
  }
}

