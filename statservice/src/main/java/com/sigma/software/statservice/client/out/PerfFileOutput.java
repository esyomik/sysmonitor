package com.sigma.software.statservice.client.out;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;


class PerfFileOutput implements PerfOutput {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String CHARSET_KEY = "charset";
    private static final String FILENAME_KEY = "filename";
    private static final String DEFAULT_FILE_NAME = "perf.txt";

    private final File file;
    private final Charset charset;

    PerfFileOutput(Properties properties) {
        file = new File(properties.getProperty(FILENAME_KEY, DEFAULT_FILE_NAME));
        charset = Charset.forName(properties.getProperty(CHARSET_KEY, DEFAULT_CHARSET));
    }

    @Override
    public void write(String key, byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, true);
    }

    @Override
    public void write(String key, String data) throws IOException {
        FileUtils.writeStringToFile(file, String.format("#Key:%s\n%s\n\n", key, data), charset, true);
    }
}
