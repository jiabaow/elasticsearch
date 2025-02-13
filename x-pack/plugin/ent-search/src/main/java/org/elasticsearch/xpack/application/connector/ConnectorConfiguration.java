/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.application.connector;

import org.elasticsearch.ElasticsearchParseException;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.core.Nullable;
import org.elasticsearch.xcontent.ConstructingObjectParser;
import org.elasticsearch.xcontent.ObjectParser;
import org.elasticsearch.xcontent.ParseField;
import org.elasticsearch.xcontent.ToXContentObject;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentParseException;
import org.elasticsearch.xcontent.XContentParser;
import org.elasticsearch.xcontent.XContentParserConfiguration;
import org.elasticsearch.xcontent.XContentType;
import org.elasticsearch.xpack.application.connector.configuration.ConfigurationDependency;
import org.elasticsearch.xpack.application.connector.configuration.ConfigurationDisplayType;
import org.elasticsearch.xpack.application.connector.configuration.ConfigurationFieldType;
import org.elasticsearch.xpack.application.connector.configuration.ConfigurationSelectOption;
import org.elasticsearch.xpack.application.connector.configuration.ConfigurationValidation;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.xcontent.ConstructingObjectParser.constructorArg;
import static org.elasticsearch.xcontent.ConstructingObjectParser.optionalConstructorArg;

/**
 * Represents the configuration field settings for a connector.
 */
public class ConnectorConfiguration implements Writeable, ToXContentObject {

    @Nullable
    private final String category;
    private final Object defaultValue;
    private final List<ConfigurationDependency> dependsOn;
    private final ConfigurationDisplayType display;
    private final String label;
    private final List<ConfigurationSelectOption> options;
    @Nullable
    private final Integer order;
    @Nullable
    private final String placeholder;
    private final boolean required;
    private final boolean sensitive;
    @Nullable
    private final String tooltip;
    private final ConfigurationFieldType type;
    private final List<String> uiRestrictions;
    private final List<ConfigurationValidation> validations;
    private final Object value;

    /**
     * Constructs a new {@link ConnectorConfiguration} instance with specified properties.
     *
     * @param category       The category of the configuration field.
     * @param defaultValue   The default value for the configuration.
     * @param dependsOn      A list of {@link ConfigurationDependency} indicating dependencies on other configurations.
     * @param display        The display type, defined by {@link ConfigurationDisplayType}.
     * @param label          The display label associated with the config field.
     * @param options        A list of {@link ConfigurationSelectOption} for selectable options.
     * @param order          The order in which this configuration appears.
     * @param placeholder    A placeholder text for the configuration field.
     * @param required       A boolean indicating whether the configuration is required.
     * @param sensitive      A boolean indicating whether the configuration contains sensitive information.
     * @param tooltip        A tooltip text providing additional information about the configuration.
     * @param type           The type of the configuration field, defined by {@link ConfigurationFieldType}.
     * @param uiRestrictions A list of UI restrictions in string format.
     * @param validations    A list of {@link ConfigurationValidation} for validating the configuration.
     * @param value          The current value of the configuration.
     */
    private ConnectorConfiguration(
        String category,
        Object defaultValue,
        List<ConfigurationDependency> dependsOn,
        ConfigurationDisplayType display,
        String label,
        List<ConfigurationSelectOption> options,
        Integer order,
        String placeholder,
        boolean required,
        boolean sensitive,
        String tooltip,
        ConfigurationFieldType type,
        List<String> uiRestrictions,
        List<ConfigurationValidation> validations,
        Object value
    ) {
        this.category = category;
        this.defaultValue = defaultValue;
        this.dependsOn = dependsOn;
        this.display = display;
        this.label = label;
        this.options = options;
        this.order = order;
        this.placeholder = placeholder;
        this.required = required;
        this.sensitive = sensitive;
        this.tooltip = tooltip;
        this.type = type;
        this.uiRestrictions = uiRestrictions;
        this.validations = validations;
        this.value = value;
    }

    public ConnectorConfiguration(StreamInput in) throws IOException {
        this.category = in.readString();
        this.defaultValue = in.readGenericValue();
        this.dependsOn = in.readOptionalCollectionAsList(ConfigurationDependency::new);
        this.display = in.readEnum(ConfigurationDisplayType.class);
        this.label = in.readString();
        this.options = in.readOptionalCollectionAsList(ConfigurationSelectOption::new);
        this.order = in.readOptionalInt();
        this.placeholder = in.readOptionalString();
        this.required = in.readBoolean();
        this.sensitive = in.readBoolean();
        this.tooltip = in.readOptionalString();
        this.type = in.readEnum(ConfigurationFieldType.class);
        this.uiRestrictions = in.readOptionalStringCollectionAsList();
        this.validations = in.readOptionalCollectionAsList(ConfigurationValidation::new);
        this.value = in.readGenericValue();
    }

    static final ParseField CATEGORY_FIELD = new ParseField("category");
    static final ParseField DEFAULT_VALUE_FIELD = new ParseField("default_value");
    static final ParseField DEPENDS_ON_FIELD = new ParseField("depends_on");
    static final ParseField DISPLAY_FIELD = new ParseField("display");
    static final ParseField LABEL_FIELD = new ParseField("label");
    static final ParseField OPTIONS_FIELD = new ParseField("options");
    static final ParseField ORDER_FIELD = new ParseField("order");
    static final ParseField PLACEHOLDER_FIELD = new ParseField("placeholder");
    static final ParseField REQUIRED_FIELD = new ParseField("required");
    static final ParseField SENSITIVE_FIELD = new ParseField("sensitive");
    static final ParseField TOOLTIP_FIELD = new ParseField("tooltip");
    static final ParseField TYPE_FIELD = new ParseField("type");
    static final ParseField UI_RESTRICTIONS_FIELD = new ParseField("ui_restrictions");
    static final ParseField VALIDATIONS_FIELD = new ParseField("validations");
    static final ParseField VALUE_FIELD = new ParseField("value");

    @SuppressWarnings("unchecked")
    private static final ConstructingObjectParser<ConnectorConfiguration, Void> PARSER = new ConstructingObjectParser<>(
        "connector_configuration_dependency",
        true,
        args -> {
            int i = 0;
            return new ConnectorConfiguration.Builder().setCategory((String) args[i++])
                .setDefaultValue(args[i++])
                .setDependsOn((List<ConfigurationDependency>) args[i++])
                .setDisplay((ConfigurationDisplayType) args[i++])
                .setLabel((String) args[i++])
                .setOptions((List<ConfigurationSelectOption>) args[i++])
                .setOrder((Integer) args[i++])
                .setPlaceholder((String) args[i++])
                .setRequired((boolean) args[i++])
                .setSensitive((boolean) args[i++])
                .setTooltip((String) args[i++])
                .setType((ConfigurationFieldType) args[i++])
                .setUiRestrictions((List<String>) args[i++])
                .setValidations((List<ConfigurationValidation>) args[i++])
                .setValue(args[i])
                .build();
        }
    );

    static {
        PARSER.declareString(optionalConstructorArg(), CATEGORY_FIELD);
        PARSER.declareField(optionalConstructorArg(), (p, c) -> {
            if (p.currentToken() == XContentParser.Token.VALUE_STRING) {
                return p.text();
            } else if (p.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                return p.numberValue();
            } else if (p.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                return p.booleanValue();
            } else if (p.currentToken() == XContentParser.Token.VALUE_NULL) {
                return null;
            }
            throw new XContentParseException("Unsupported token [" + p.currentToken() + "]");
        }, DEFAULT_VALUE_FIELD, ObjectParser.ValueType.VALUE);
        PARSER.declareObjectArray(constructorArg(), (p, c) -> ConfigurationDependency.fromXContent(p), DEPENDS_ON_FIELD);
        PARSER.declareField(
            constructorArg(),
            (p, c) -> ConfigurationDisplayType.displayType(p.text()),
            DISPLAY_FIELD,
            ObjectParser.ValueType.STRING
        );
        PARSER.declareString(constructorArg(), LABEL_FIELD);
        PARSER.declareObjectArray(constructorArg(), (p, c) -> ConfigurationSelectOption.fromXContent(p), OPTIONS_FIELD);
        PARSER.declareInt(optionalConstructorArg(), ORDER_FIELD);
        PARSER.declareString(optionalConstructorArg(), PLACEHOLDER_FIELD);
        PARSER.declareBoolean(constructorArg(), REQUIRED_FIELD);
        PARSER.declareBoolean(constructorArg(), SENSITIVE_FIELD);
        PARSER.declareStringOrNull(optionalConstructorArg(), TOOLTIP_FIELD);
        PARSER.declareField(
            constructorArg(),
            (p, c) -> ConfigurationFieldType.fieldType(p.text()),
            TYPE_FIELD,
            ObjectParser.ValueType.STRING
        );
        PARSER.declareStringArray(constructorArg(), UI_RESTRICTIONS_FIELD);
        PARSER.declareObjectArray(constructorArg(), (p, c) -> ConfigurationValidation.fromXContent(p), VALIDATIONS_FIELD);
        PARSER.declareField(constructorArg(), (p, c) -> {
            if (p.currentToken() == XContentParser.Token.VALUE_STRING) {
                return p.text();
            } else if (p.currentToken() == XContentParser.Token.VALUE_NUMBER) {
                return p.numberValue();
            } else if (p.currentToken() == XContentParser.Token.VALUE_BOOLEAN) {
                return p.booleanValue();
            } else if (p.currentToken() == XContentParser.Token.VALUE_NULL) {
                return null;
            }
            throw new XContentParseException("Unsupported token [" + p.currentToken() + "]");
        }, VALUE_FIELD, ObjectParser.ValueType.VALUE);
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();
        {
            if (category != null) {
                builder.field(CATEGORY_FIELD.getPreferredName(), category);
            }
            builder.field(DEFAULT_VALUE_FIELD.getPreferredName(), defaultValue);
            builder.xContentList(DEPENDS_ON_FIELD.getPreferredName(), dependsOn);
            builder.field(DISPLAY_FIELD.getPreferredName(), display.toString());
            builder.field(LABEL_FIELD.getPreferredName(), label);
            builder.xContentList(OPTIONS_FIELD.getPreferredName(), options);
            if (order != null) {
                builder.field(ORDER_FIELD.getPreferredName(), order);
            }
            if (placeholder != null) {
                builder.field(PLACEHOLDER_FIELD.getPreferredName(), placeholder);
            }
            builder.field(REQUIRED_FIELD.getPreferredName(), required);
            builder.field(SENSITIVE_FIELD.getPreferredName(), sensitive);
            builder.field(TOOLTIP_FIELD.getPreferredName(), tooltip);
            builder.field(TYPE_FIELD.getPreferredName(), type.toString());
            builder.stringListField(UI_RESTRICTIONS_FIELD.getPreferredName(), uiRestrictions);
            builder.xContentList(VALIDATIONS_FIELD.getPreferredName(), validations);
            builder.field(VALUE_FIELD.getPreferredName(), value);
        }
        builder.endObject();
        return builder;
    }

    public static ConnectorConfiguration fromXContent(XContentParser parser) throws IOException {
        return PARSER.parse(parser, null);
    }

    public static ConnectorConfiguration fromXContentBytes(BytesReference source, XContentType xContentType) {
        try (XContentParser parser = XContentHelper.createParser(XContentParserConfiguration.EMPTY, source, xContentType)) {
            return ConnectorConfiguration.fromXContent(parser);
        } catch (IOException e) {
            throw new ElasticsearchParseException("Failed to parse a connector configuration field.", e);
        }
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        out.writeString(category);
        out.writeGenericValue(defaultValue);
        out.writeOptionalCollection(dependsOn);
        out.writeEnum(display);
        out.writeString(label);
        out.writeOptionalCollection(options);
        out.writeOptionalInt(order);
        out.writeOptionalString(placeholder);
        out.writeBoolean(required);
        out.writeBoolean(sensitive);
        out.writeOptionalString(tooltip);
        out.writeEnum(type);
        out.writeOptionalStringCollection(uiRestrictions);
        out.writeOptionalCollection(validations);
        out.writeGenericValue(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectorConfiguration that = (ConnectorConfiguration) o;
        return required == that.required
            && sensitive == that.sensitive
            && Objects.equals(category, that.category)
            && Objects.equals(defaultValue, that.defaultValue)
            && Objects.equals(dependsOn, that.dependsOn)
            && display == that.display
            && Objects.equals(label, that.label)
            && Objects.equals(options, that.options)
            && Objects.equals(order, that.order)
            && Objects.equals(placeholder, that.placeholder)
            && Objects.equals(tooltip, that.tooltip)
            && type == that.type
            && Objects.equals(uiRestrictions, that.uiRestrictions)
            && Objects.equals(validations, that.validations)
            && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            category,
            defaultValue,
            dependsOn,
            display,
            label,
            options,
            order,
            placeholder,
            required,
            sensitive,
            tooltip,
            type,
            uiRestrictions,
            validations,
            value
        );
    }

    public static class Builder {

        private String category;
        private Object defaultValue;
        private List<ConfigurationDependency> dependsOn;
        private ConfigurationDisplayType display;
        private String label;
        private List<ConfigurationSelectOption> options;
        private Integer order;
        private String placeholder;
        private boolean required;
        private boolean sensitive;
        private String tooltip;
        private ConfigurationFieldType type;
        private List<String> uiRestrictions;
        private List<ConfigurationValidation> validations;
        private Object value;

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder setDependsOn(List<ConfigurationDependency> dependsOn) {
            this.dependsOn = dependsOn;
            return this;
        }

        public Builder setDisplay(ConfigurationDisplayType display) {
            this.display = display;
            return this;
        }

        public Builder setLabel(String label) {
            this.label = label;
            return this;
        }

        public Builder setOptions(List<ConfigurationSelectOption> options) {
            this.options = options;
            return this;
        }

        public Builder setOrder(Integer order) {
            this.order = order;
            return this;
        }

        public Builder setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder setRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder setSensitive(boolean sensitive) {
            this.sensitive = sensitive;
            return this;
        }

        public Builder setTooltip(String tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder setType(ConfigurationFieldType type) {
            this.type = type;
            return this;
        }

        public Builder setUiRestrictions(List<String> uiRestrictions) {
            this.uiRestrictions = uiRestrictions;
            return this;
        }

        public Builder setValidations(List<ConfigurationValidation> validations) {
            this.validations = validations;
            return this;
        }

        public Builder setValue(Object value) {
            this.value = value;
            return this;
        }

        public ConnectorConfiguration build() {
            return new ConnectorConfiguration(
                category,
                defaultValue,
                dependsOn,
                display,
                label,
                options,
                order,
                placeholder,
                required,
                sensitive,
                tooltip,
                type,
                uiRestrictions,
                validations,
                value
            );
        }
    }
}
