package com.sigma.software.rmonitor.resource;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Localized resources.
 */
public class Resources {

    private static final String BASE_LABELS_NAME = "i18n/label";
    private static final String BASE_MESSAGES_NAME = "i18n/message";

    private static ResourceBundle labelBundle;
    private static ResourceBundle messageBundle;


    private Resources() {}

    public static void init(String language, String country) {
        Locale locale = new Locale(language, country);
        labelBundle = ResourceBundle.getBundle(BASE_LABELS_NAME, locale);
        messageBundle = ResourceBundle.getBundle(BASE_MESSAGES_NAME, locale);
    }

    static String getLabel(String idd) {
        return labelBundle.getString(idd);
    }

    static String getMessage(String idd) {
        return messageBundle.getString(idd);
    }
}
