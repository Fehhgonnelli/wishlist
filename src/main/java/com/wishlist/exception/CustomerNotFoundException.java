package com.wishlist.exception;


import java.io.Serial;

public class CustomerNotFoundException extends Exception{

    @Serial
    private static final long serialVersionUID = 1149241039409861914L;

    public CustomerNotFoundException(String msg){
        super(msg);
    }
    public CustomerNotFoundException(String msg, Throwable cause){
        super(msg, cause);
    }
}