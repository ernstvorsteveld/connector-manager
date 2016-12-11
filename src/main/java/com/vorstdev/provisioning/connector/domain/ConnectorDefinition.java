package com.vorstdev.provisioning.connector.domain;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * Created by ernstvorsteveld on 20/01/16.
 */
public class ConnectorDefinition {

    private static final int DEFAULT_TIMEOUT = 10;

    private String id;

    private String location;

    private String base;

    private String bundleName;

    private String version;

    private boolean active = false;

    private Set<ConnectorCapabilities> capabilities;

    private List<ConnectorConfiguration> configuration;

    public Object[] getConnectorIdentification() {
        return new Object[]{
                notNullValue(this.bundleName),
                notNullValue(this.version),
                notNullValue(this.location)
        };
    }

    private Object notNullValue(String value) {
        return StringUtils.isEmpty(value) ? "null value" : value;
    }

    public String getConnectorInfo() {
        return String.format("connector definition, bundle %s, version %s, location: %s",
                getConnectorIdentification
                        ());
    }

    public void setBase(String base) {
        this.base = base;
    }

    public static final class ConnectorConfiguration {
        private String name;
        private List<Object> values;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Object> getValues() {
            return values;
        }

        public void setValues(List<Object> values) {
            this.values = values;
        }

        @Override
        public String toString() {
            return ReflectionToStringBuilder.toString(this);
        }
    }

    private String displayName;

    private Integer timeout = DEFAULT_TIMEOUT;

    public ConnectorDefinition() {
        capabilities = Sets.newHashSet();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<ConnectorCapabilities> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Set<ConnectorCapabilities> capabilities) {
        this.capabilities = capabilities;
    }

    public List<ConnectorConfiguration> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(List<ConnectorConfiguration> configuration) {
        this.configuration = configuration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getBase() {
        return base;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
