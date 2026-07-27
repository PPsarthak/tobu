package com.keno.tobu.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Constant {
    private Constant() {
    }

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream inputStream = Constant.class.getClassLoader().getResourceAsStream("tobu.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Unable to find tobu.properties");
            }
            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to load Tobu configuration",e);
        }
    }

    public static final String TOBU_VERSION = PROPERTIES.getProperty("tobu.version");

    public static final String SYNC = "sync";
    public static final String VERSION = "version";
    public static final String INFO = "info";
    public static final String ROLLBACK = "rollback";
    public static final String STASH_REFRESH = "stash-refresh";

    public static final String GIT = "git";
    public static final String STASH = "stash";
}
