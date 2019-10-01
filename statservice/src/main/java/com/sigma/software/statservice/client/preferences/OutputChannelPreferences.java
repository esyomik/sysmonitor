package com.sigma.software.statservice.client.preferences;

import lombok.Value;

import java.util.Properties;


@Value
public class OutputChannelPreferences {
    OutputStreamType destination;
    SerializerType serializer;
    Properties properties;
}
