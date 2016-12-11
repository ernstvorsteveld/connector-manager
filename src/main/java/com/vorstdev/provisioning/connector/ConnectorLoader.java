package com.vorstdev.provisioning.connector;

import com.vorstdev.provisioning.connector.domain.ConnectorDefinition;
import org.identityconnectors.framework.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.vorstdev.provisioning.connector.domain.ConnectorDefinition.ConnectorConfiguration;

/**
 * Created by ernstvorsteveld on 20/01/16.
 */
public class ConnectorLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ConnectorFacade load(ConnectorDefinition connectorDefinition) {
        ConnectorInfo info = getConnectorInfo(connectorDefinition);

        final APIConfiguration apiConfig = getDefaultApiConfiguration(connectorDefinition, info);
        final ConfigurationProperties properties = getDefaultApiConfigurationProperties(connectorDefinition, apiConfig);

        initializeWithConfigurationProperties(connectorDefinition, properties);
        return createAndValidateConnector(connectorDefinition, apiConfig);
    }

    private ConnectorFacade createAndValidateConnector(
            ConnectorDefinition connectorDefinition,
            APIConfiguration apiConfig) {
        logger.info("About to load {} with configuration properties {}.", connectorDefinition.getConnectorInfo(),
                connectorDefinition.getConfiguration());
        ConnectorFacade connector = ConnectorFacadeFactory.getInstance().newInstance(apiConfig);
        if (connector == null) {
            String error = String.format(
                    "Could not create connector facade for %s.",
                    connectorDefinition.getConnectorIdentification());
            logger.error(error);
            throw new ConnectorLoaderException(error);
        }
        // ask the connector to validate itself
        connector.validate();
        logger.info(String.format("Ready loading {}.", connectorDefinition.getConnectorInfo()));
        return connector;
    }

    private void initializeWithConfigurationProperties(
            ConnectorDefinition connectorDefinition,
            ConfigurationProperties properties) {
        List<ConnectorConfiguration> configuration = connectorDefinition.getConfiguration();
        if (configuration == null || configuration.isEmpty()) {
            logger.info("No configuration properties found for {}.", connectorDefinition.getConnectorInfo());
        } else {
            configuration.stream()
                    .forEach(config -> properties.setPropertyValue(config.getName(), config.getValues()));
        }
    }

    private ConfigurationProperties getDefaultApiConfigurationProperties(ConnectorDefinition connectorDefinition, APIConfiguration apiConfig) {
        final ConfigurationProperties properties = apiConfig.getConfigurationProperties();
        if (properties == null) {
            throw new ConnectorLoaderException(
                    String.format(
                            "Could not get default properties for %s.",
                            connectorDefinition.getConnectorInfo()));
        }
        return properties;
    }

    private APIConfiguration getDefaultApiConfiguration(ConnectorDefinition connectorDefinition, ConnectorInfo info) {
        APIConfiguration apiConfig = info.createDefaultAPIConfiguration();
        if (apiConfig == null) {
            throw new ConnectorLoaderException(
                    String.format(
                            "Creating default connector configuration failed for %s.",
                            connectorDefinition.getConnectorInfo()));
        }
        return apiConfig;
    }

    private ConnectorInfo getConnectorInfo(ConnectorDefinition connectorDefinition) {
        ConnectorInfo connectorInfo = null;
        try {
            URL url = getUrl(connectorDefinition);
            ConnectorInfoManager manager = ConnectorInfoManagerFactory.getInstance().getLocalManager(url);
            connectorInfo = manager.findConnectorInfo(new ConnectorKey(connectorDefinition.getBundleName(),
                    connectorDefinition.getVersion(), connectorDefinition.getBundleName()));
        } catch (Exception e) {
            String error = String.format("Exception while loading %s.", connectorDefinition.getConnectorInfo());
            logger.error(error);
            throw new ConnectorLoaderException(error, e);
        }
        if (connectorInfo == null) {
            String error = String.format("Could not load %s.", connectorDefinition.getConnectorInfo());
            logger.error(error);
            throw new ConnectorLoaderException(error);
        }
        return connectorInfo;
    }

    private URL getUrl(ConnectorDefinition connectorDefinition) throws MalformedURLException {
        return new URL("file://" + connectorDefinition.getBase() + File.separator + connectorDefinition.getLocation());
    }

    public static final class ConnectorLoaderException extends RuntimeException {
        private String error;

        public ConnectorLoaderException(String error) {
            this.error = error;
        }

        public ConnectorLoaderException(String error, Exception e) {
            super(e);
            this.error = error;
        }
    }
}
