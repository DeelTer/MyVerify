package ru.deelter.verify.utils;

import ru.deelter.verify.Config;
import ru.deelter.verify.VerifyReload;

public class Console {

    public static void log(String s) {
       VerifyReload.getInstance().getLogger().info(Colors.set(s));
    }

    public static void debug(String s) {
        if (!Config.DEBUG)
            return;

        log("&6&o" + s);
    }
}
