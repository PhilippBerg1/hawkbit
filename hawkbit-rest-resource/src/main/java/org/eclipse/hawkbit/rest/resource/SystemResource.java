/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Map;

import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.model.TenantConfigurationValue;
import org.eclipse.hawkbit.rest.resource.model.system.TenantConfigurationValueRequest;
import org.eclipse.hawkbit.rest.resource.model.system.TenantConfigurationValueRest;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource handling tenant specific configuration operations.
 *
 *
 *
 *
 */
@RestController
@RequestMapping(RestConstants.SYSTEM_V1_REQUEST_MAPPING)
public class SystemResource {

    private static final Logger LOG = LoggerFactory.getLogger(SystemResource.class);

    @Autowired
    private TenantConfigurationManagement tenantConfigurationManagement;

    @RequestMapping(method = RequestMethod.GET, produces = { "application/hal+json", MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<ResourceSupport> getSystem() {
        final ResourceSupport resourceSupport = new ResourceSupport();
        resourceSupport.add(linkTo(methodOn(SystemResource.class).getSystemConfiguration()).withRel("configs"));
        return ResponseEntity.ok(resourceSupport);
    }

    /**
     * @return a Map of all configuration values.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/configs", produces = { "application/hal+json",
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, TenantConfigurationValueRest>> getSystemConfiguration() {
        return new ResponseEntity<>(SystemMapper.toResponse(tenantConfigurationManagement), HttpStatus.OK);
    }

    /**
     * Handles the DELETE request of deleting a tenant specific configuration
     * value within SP.
     *
     * @param keyName
     *            the Name of the configuration key
     * @return If the given configuration value exists and could be deleted Http
     *         OK. In any failure the JsonResponseExceptionHandler is handling
     *         the response.
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/configs/{keyName}", produces = { "application/hal+json",
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Void> deleteConfigurationValue(@PathVariable final String keyName) {

        final TenantConfigurationKey configKey = TenantConfigurationKey.fromKeyName(keyName);

        tenantConfigurationManagement.deleteConfiguration(configKey);

        LOG.debug("{} config value deleted, return status {}", keyName, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Handles the GET request of deleting a tenant specific configuration value
     * within SP.
     *
     * @param keyName
     *            the Name of the configuration key
     * @return If the given configuration value exists and could be get Http OK.
     *         In any failure the JsonResponseExceptionHandler is handling the
     *         response.
     */
    @RequestMapping(method = RequestMethod.GET, value = "/configs/{keyName}", produces = { "application/hal+json",
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<TenantConfigurationValueRest> getConfigurationValue(@PathVariable final String keyName) {

        final TenantConfigurationKey configKey = TenantConfigurationKey.fromKeyName(keyName);

        LOG.debug("{} config value getted, return status {}", keyName, HttpStatus.OK);
        return new ResponseEntity<>(SystemMapper.toResponse(configKey.getKeyName(),
                tenantConfigurationManagement.getConfigurationValue(configKey)), HttpStatus.OK);
    }

    /**
     * Handles the GET request of deleting a tenant specific configuration value
     * within SP.
     *
     * @param keyName
     *            the Name of the configuration key
     * @param configurationValueRest
     *            the new value for the configuration
     * @return If the given configuration value exists and could be get Http OK.
     *         In any failure the JsonResponseExceptionHandler is handling the
     *         response.
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/configs/{keyName}", consumes = { "application/hal+json",
            MediaType.APPLICATION_JSON_VALUE }, produces = { "application/hal+json", MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<TenantConfigurationValueRest> updateConfigurationValue(@PathVariable final String keyName,
            @RequestBody final TenantConfigurationValueRequest configurationValueRest) {

        final TenantConfigurationKey configKey = TenantConfigurationKey.fromKeyName(keyName);

        final TenantConfigurationValue<Object> updatedValue = tenantConfigurationManagement

                .addOrUpdateConfiguration(configKey, configurationValueRest.getValue());
        return new ResponseEntity<>(SystemMapper.toResponse(keyName, updatedValue), HttpStatus.OK);
    }

}