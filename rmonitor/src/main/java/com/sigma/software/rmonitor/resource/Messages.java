package com.sigma.software.rmonitor.resource;


/**
 * GUI messages. It uses {@link Resources Resources} class. It should be
 * initialized prior first usage.
 */
public enum Messages {

    ERR_CONFIG_NOT_FOUND("ERR.CONFIG.NOT.FOUND");


    private final String idd;

    Messages(String idd) {
        this.idd = idd;
    }

    public String get() {
        return Resources.getMessage(idd);
    }
}
