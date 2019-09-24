package com.sigma.software.rmonitor.resource;


/**
 * GUI item labels. It uses {@link Resources Resources} class. It should be
 * initialized prior first usage.
 */
public enum Labels {

    APP_TITLE("MONITOR.APP.TITLE");


    private final String idd;

    Labels(String idd) {
        this.idd = idd;
    }

    public String get() {
        return Resources.getLabel(idd);
    }
}
