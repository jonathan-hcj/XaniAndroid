package com.xaniapp.xani.entites;

public class Result<T1> {

    public Boolean  success = true;
    public String   errorMessage;
    public T1       data;

    public void setFail (String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
    }
}
