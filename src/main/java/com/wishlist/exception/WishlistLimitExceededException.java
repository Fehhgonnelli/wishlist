package com.wishlist.exception;

public class WishlistLimitExceededException extends RuntimeException {
  public WishlistLimitExceededException(String message) {
    super(message);
  }
}
