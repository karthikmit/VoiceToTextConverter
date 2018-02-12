package com.makemytrip.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Component for fetching properties.
 */
@Component
public class PropertiesFetcher {

    @Autowired private Environment environment;

    public PropertiesFetcher() {

    }

    public String fetchProperty(String key, String defaultValue) {
        String propertyValue = environment.getProperty(key);
        return propertyValue != null ? propertyValue : defaultValue;
    }
}
