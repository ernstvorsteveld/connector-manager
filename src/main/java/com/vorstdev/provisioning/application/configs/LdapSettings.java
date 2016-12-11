package com.vorstdev.provisioning.application.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by ernstvorsteveld on 17/06/16.
 */
@Component
@ConfigurationProperties("connector.ldap")
public class LdapSettings {

    private List<String> attributeNames;

    public List<String> getAttributeNames() {
        return attributeNames;
    }

    public void setAttributeNames(List<String> attributeNames) {
        this.attributeNames = attributeNames;
    }
}
