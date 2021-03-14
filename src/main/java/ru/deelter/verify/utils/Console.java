package ru.deelter.verify.utils;

import ru.deelter.verify.VerifyReload;

public class Console {

    public static void log(String s) {
       VerifyReload.getInstance().getLogger().info(Colors.set(s));
    }
}
