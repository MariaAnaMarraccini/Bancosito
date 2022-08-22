package com.mindhub.homebanking.utils;

//el final es para que no se pueda instanciar mas
//Cuando cargue la app, el final le indica a la aplicacion que solo se instancie una vez
//y nadie puede heredar de este objeto. Se usa como CardUtilis. y va el metodo.


public final class CardUtils {

    private CardUtils(){}

    public static String getCardNumber(int min, int max) {
        return ("4657-"+(int)((Math.random() * (max - min)) + min)+"-"+(int)((Math.random() * (max - min)) + min)+"-"+(int)((Math.random() * (max - min)) + min));
    }
    public static int getCVVNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}





