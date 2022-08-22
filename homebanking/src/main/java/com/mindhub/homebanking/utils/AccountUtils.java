package com.mindhub.homebanking.utils;

public final class AccountUtils {

    private AccountUtils(){}
    public static int createAccountNumber(int min, int max){
        return (int) ((Math.random() * (max - min)) + min);}

}
