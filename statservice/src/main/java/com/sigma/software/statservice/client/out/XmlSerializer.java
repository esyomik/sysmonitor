package com.sigma.software.statservice.client.out;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;


/**
 * Serializes statistics into PerfOutput stream as XML string, see {@link PerfOutput}
 * @param <D> type of serializing data
 */
class XmlSerializer<D> implements Serializer<D> {

    private final XmlMapper xmlMapper;


    XmlSerializer() {
        xmlMapper = new XmlMapper();
    }

    @Override
    public void serialize(String key, D data, PerfOutput output) throws IOException {
        try {
            String xml = xmlMapper.writeValueAsString(data);
            output.write(key, xml);
        } catch (JsonProcessingException e) {
            throw new IOException(e.getMessage());
        }
    }
}
