package com.wishlist.exception;


import java.io.Serial;

public class NoItemsAddedException extends Exception{

    @Serial
    private static final long serialVersionUID = 1149241039409861914L;

    public NoItemsAddedException(String msg){
        super(msg);
    }
}