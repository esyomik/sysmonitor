package com.sigma.software.statservice.client.preferences;

import lombok.Value;


@Value
public class RecorderChannelPreferences {
    StatisticType type;
    long duration;
    String inputName;
    OutputChannelPreferences[] output;
}
