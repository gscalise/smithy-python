/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.python.codegen;

import java.util.Arrays;
import java.util.Objects;
import software.amazon.smithy.codegen.core.CodegenException;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.ShapeId;

/**
 * Settings used by {@link PythonCodegenPlugin}.
 */
public final class PythonSettings {

    private static final String SERVICE = "service";
    private static final String MODULE_NAME = "module";
    private static final String MODULE_DESCRIPTION = "moduleDescription";
    private static final String MODULE_VERSION = "moduleVersion";

    private ShapeId service;
    private String moduleName;
    private String moduleVersion;
    private String moduleDescription = "";

    /**
     * Create a settings object from a configuration object node.
     *
     * @param config Config object to load.
     * @return Returns the extracted settings.
     */
    public static PythonSettings from(ObjectNode config) {
        PythonSettings settings = new PythonSettings();
        config.warnIfAdditionalProperties(Arrays.asList(SERVICE, MODULE_NAME, MODULE_DESCRIPTION, MODULE_VERSION));

        settings.setService(config.expectStringMember(SERVICE).expectShapeId());
        settings.setModuleName(config.expectStringMember(MODULE_NAME).getValue());
        settings.setModuleVersion(config.expectStringMember(MODULE_VERSION).getValue());
        settings.setModuleDescription(config.getStringMemberOrDefault(
                MODULE_DESCRIPTION, settings.getModuleName() + " client"));

        return settings;
    }

    /**
     * Gets the id of the service that is being generated.
     *
     * @return Returns the service id.
     * @throws NullPointerException if the service has not been set.
     */
    public ShapeId getService() {
        return Objects.requireNonNull(service, SERVICE + " not set");
    }

    /**
     * Gets the corresponding {@link ServiceShape} from a model.
     *
     * @param model Model to search for the service shape by ID.
     * @return Returns the found {@code Service}.
     * @throws NullPointerException if the service has not been set.
     * @throws CodegenException if the service is invalid or not found.
     */
    public ServiceShape getService(Model model) {
        return model
                .getShape(getService())
                .orElseThrow(() -> new CodegenException("Service shape not found: " + getService()))
                .asServiceShape()
                .orElseThrow(() -> new CodegenException("Shape is not a Service: " + getService()));
    }

    /**
     * Sets the service to generate.
     *
     * @param service The service to generate.
     */
    public void setService(ShapeId service) {
        this.service = Objects.requireNonNull(service);
    }

    /**
     * Gets the required module name for the module that will be generated.
     *
     * @return Returns the module name.
     * @throws NullPointerException if the module name has not been set.
     */
    public String getModuleName() {
        return Objects.requireNonNull(moduleName, MODULE_NAME + " not set");
    }

    /**
     * Sets the name of the module to generate.
     *
     * @param moduleName The name of the module to generate.
     */
    public void setModuleName(String moduleName) {
        this.moduleName = Objects.requireNonNull(moduleName);
    }

    /**
     * Gets the required module version for the module that will be generated.
     *
     * @return The version of the module that will be generated.
     * @throws NullPointerException if the module version has not been set.
     */
    public String getModuleVersion() {
        return Objects.requireNonNull(moduleVersion, MODULE_VERSION + " not set");
    }

    /**
     * Sets the required module version for the module that will be generated.
     *
     * @param moduleVersion The version of the module that will be generated.
     */
    public void setModuleVersion(String moduleVersion) {
        this.moduleVersion = Objects.requireNonNull(moduleVersion);
    }

    /**
     * Gets the optional module description for the module that will be generated.
     *
     * @return Returns the module description.
     */
    public String getModuleDescription() {
        return moduleDescription;
    }

    /**
     * Sets the description of the module to generate.
     *
     * @param moduleDescription The description of the module to generate.
     */
    public void setModuleDescription(String moduleDescription) {
        this.moduleDescription = Objects.requireNonNull(moduleDescription);
    }
}