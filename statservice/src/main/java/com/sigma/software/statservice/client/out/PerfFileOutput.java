package com.sigma.software.statservice.client.out;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 * Output stream for writing serialized statistics or performance data to a file.
 * Serialized data can has two forms: binary, represented as byte array and string.
 * <br/><b>Properties:</b>
 * <br/><b>filename</b> the name of created file; records will be appended to existing
 * <br/><b>charset</b> the charset for writing
 * <br/><b>format</b> the format string to write to file in text mode; must include two
 * placeholders: for key and value accordingly
 */
class PerfFileOutput implements PerfOutput {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String CHARSET_KEY = "charset";
    private static final String FILENAME_KEY = "filename";
    private static final String FORMAT_KEY = "format";
    private static final String DEFAULT_FILE_NAME = "perf.txt";
    private static final String DEFAULT_FORMAT = "%s\n%s\n\n";

    private final File file;
    private final Charset charset;
    private final String format;


    PerfFileOutput(Properties properties) {
        file = new File(properties.getProperty(FILENAME_KEY, DEFAULT_FILE_NAME));
        charset = Charset.forName(properties.getProperty(CHARSET_KEY, DEFAULT_CHARSET));
        format = properties.getProperty(FORMAT_KEY, DEFAULT_FORMAT);
    }

    @Override
    public void write(String key, byte[] data) throws IOException {
        FileUtils.writeByteArrayToFile(file, data, true);
    }

    @Override
    public void write(String key, String data) throws IOException {
        FileUtils.writeStringToFile(file, String.format(format, key, data), charset, true);
    }
}
