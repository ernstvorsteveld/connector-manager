package com.vorstdev.provisioning.connector;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.vorstdev.provisioning.connector.ConnectorLoader.ConnectorLoaderException;
import com.vorstdev.provisioning.connector.domain.ConnectorDefinition;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.Uid;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by ernstvorsteveld on 23/01/16.
 */
public class ConnectorLoaderTest {

    public static final String BUNDLE_NAME = "com.vorstdev.connid.connector.ConnectorTester";
    public static final String BASE = "/tmp";
    public static final String LOCATION = "connector-tester-0.1.0.jar";
    public static final String VERSION = "0.1.0-SNAPSHOT";

    private ConnectorLoader connectorLoader = new ConnectorLoader();

    @Test
    public void should_load() {
        ConnectorDefinition connectorDefinition = expectConnectorBundleVersionDefinition();
        ConnectorFacade connectorFacade = connectorLoader.load(connectorDefinition);
        Assert.assertThat(connectorFacade, Is.is(IsNull.notNullValue()));
    }

    @Test
    public void should_create() {
        ConnectorDefinition connectorDefinition = expectConnectorBundleVersionDefinition();
        ConnectorFacade connectorFacade = connectorLoader.load(connectorDefinition);
        ObjectClass objectClass = ObjectClass.ACCOUNT;
        Set<Attribute> attributes = Sets.newHashSet();
        OperationOptions operationOptions = new OperationOptions(Maps.newHashMap());
        Uid uid = connectorFacade.create(objectClass, attributes, operationOptions);
        Assert.assertThat(uid.getUidValue(), Is.is("none"));
    }

    @Test(expected = ConnectorLoaderException.class)
    public void should_throw_MalformedURLException() {
        ConnectorDefinition connectorDefinition = expectConnectorDefinitionWithoutUri();
        connectorLoader.load(connectorDefinition);
    }

    @Test(expected = ConnectorLoaderException.class)
    public void should_throw_IllegalArgumentException_missing_bundle() {
        ConnectorDefinition connectorDefinition = expectConnectorUriDefinition();
        connectorLoader.load(connectorDefinition);
    }

    private ConnectorDefinition expectConnectorBundleVersionDefinition() {
        ConnectorDefinition definition = expectConnectorBundleNameDefinition();
        definition.setVersion(VERSION);
        return definition;
    }

    private ConnectorDefinition expectConnectorBundleNameDefinition() {
        ConnectorDefinition definition = expectConnectorUriDefinition();
        definition.setBundleName(BUNDLE_NAME);
        return definition;
    }

    private ConnectorDefinition expectConnectorDefinitionWithoutUri() {
        ConnectorDefinition definition = new ConnectorDefinition();
        return definition;
    }

    private ConnectorDefinition expectConnectorUriDefinition() {
        ConnectorDefinition definition = expectConnectorDefinitionWithoutUri();
        definition.setBase(BASE);
        definition.setLocation(LOCATION);
        return definition;
    }
}