package com.oop.model;

public class NetWorkException extends Exception{
    public NetWorkException(String msg){
        super(msg);
    }
    public NetWorkException(Throwable cause){
        super(cause);
    }
}
