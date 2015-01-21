/**
 *
 */
package org.apache.taverna.scufl2.api.configurations;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.IOException;
import java.net.URI;

import org.apache.taverna.scufl2.api.activity.Activity;
import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Configurable;
import org.apache.taverna.scufl2.api.common.Typed;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.port.Port;
import org.apache.taverna.scufl2.api.profiles.Profile;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

/**
 * Configuration of a {@link Configurable} workflow bean.
 * <p>
 * A configuration is activated by a {@link Profile}, and provides a link to the
 * {@link #getJson()} containing the properties to configure the bean, like an
 * {@link Activity}.
 * <p>
 * A configuration is of a certain (RDF) <strong>type</strong>, as defined by
 * {@link #getType()} - which determines which properties are required and
 * optional. For instance, the type
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/ConfigType</code> requires
 * the property
 * <code>http://ns.taverna.org.uk/2010/activity/wsdl/operation</code>.
 * <p>
 * These requirements are described in the {@link ConfigurationDefinition} that
 * matches the getConfigurableType() of the {@link Configurable} found by
 * {@link #getConfigures()}. Its
 * {@link ConfigurationDefinition#getPropertyResourceDefinition()} should in
 * {@link PropertyResourceDefinition#getTypeURI()} should match the
 * {@link #getType()} of this {@link Configuration}.
 * <p>
 * Note: {@link #getType()} (and the potentially misleading {@link #getType()})
 * return the type of <b>this</b> Configuration, not the type of the
 * {@link Configurable} bean that it happens to configure. For instance, a
 * Configuration typed <code>http://example.com/WSDLConfiguration</code> might
 * configure an activity typed <code>http://example.com/WSDLActivity</code>, but
 * could also have configured an activity typed
 * <code>http://example.com/GlobusWSDLActivity</code>.
 * <p>
 * <strong>TODO: Where are the ConfigurationDefinitions found?</strong>
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public class Configuration extends AbstractNamed implements Child<Profile>,
		Typed {
	private static final JsonNodeFactory JSON_NODE_FACTORY = JsonNodeFactory.instance;
	private Configurable configures;
	private Profile parent;
	private JsonNode json = JSON_NODE_FACTORY.objectNode();
	private JsonNode jsonSchema;
	private URI type;

	/**
	 * Constructs a <code>Configuration</code> with a random UUID as the name.
	 */
	public Configuration() {
		super();
	}

	/**
	 * Construct a <code>Configuration</code> with the specified name.
	 * 
	 * @param name
	 *            the name of the <code>Configuration</code>. <strong>Must
	 *            not</strong> be <code>null</code> or an empty String.
	 */
	public Configuration(String name) {
		super(name);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Return the {@link Configurable} workflow bean that is configured.
	 * Typically an {@link Activity} or {@link Processor}, {@link Workflow} and
	 * {@link Port} can be configured.
	 * 
	 * @return the <code>Configurable</code> <code>WorkflowBean</code> that is
	 *         configured
	 */
	public Configurable getConfigures() {
		return configures;
	}

	@Override
	public Profile getParent() {
		return parent;
	}

	/**
	 * Return the underlying JSON {@link JsonNode} which contains the properties
	 * set by this configuration.
	 * <p>
	 * The JSON node is typically an {@link ObjectNode} or {@link ArrayNode},
	 * but could also be a {@link ValueNode}. The default node for a freshly
	 * constructed Configuration is an {@link ObjectNode}. * @return the backing
	 * {@link ObjectNode}.
	 */
	public JsonNode getJson() {
		return json;
	}

	/**
	 * Return a string representation of the {@link #getJson()} configuration,
	 * the string is valid JSON.
	 * 
	 * @return
	 */
	public String getJsonAsString() {
		return json.toString();
	}

	/**
	 * Return the {@link #getJson()} configuration as an {@link ObjectNode}.
	 * This is the default type for a new {@link Configuration}.
	 * 
	 * @throws IllegalStateException
	 *             if the {@link #getJson()} is not an {@link ObjectNode}
	 * 
	 * @return
	 */
	public ObjectNode getJsonAsObjectNode() {
		if (!json.isObject())
			throw new IllegalStateException("JSON is not an ObjectNode, but "
					+ json);
		return (ObjectNode) json;
	}

	/**
	 * Return the type of the <code>Configuration</code>.
	 * <p>
	 * The URI will match the {@link PropertyResource#getTypeURI()}.
	 * 
	 * @return the type of the <code>Configuration</code>
	 */
	@Override
	public URI getType() {
		return type;
	}

	/**
	 * Set the {@link Configurable} {@link WorkflowBean} that is configured.
	 * 
	 * @param configurable
	 *            the <code>Configurable</code> <code>WorkflowBean</code> that
	 *            is configured
	 */
	public void setConfigures(Configurable configurable) {
		configures = configurable;
	}

	@Override
	public void setParent(Profile parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getConfigurations().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getConfigurations().add(this);
	}

	/**
	 * Set the underlying JSON {@link JsonNode} which contains the properties
	 * set by this configuration. The JSON node is typically an
	 * {@link ObjectNode} or {@link ArrayNode}, but could also be a
	 * {@link ValueNode}.
	 * <p>
	 * If the provided ObjectNode is <code>null</code>, a new, blank
	 * {@link ObjectNode} will be set.
	 * 
	 * @param json
	 *            the underlying <code>JsonNode</code> which contains the
	 *            properties set by this configuration.
	 */
	public void setJson(JsonNode json) {
		if (json == null)
			this.json = JSON_NODE_FACTORY.objectNode();
		this.json = json;
	}

	/**
	 * Set the type of the <code>Configuration</code>.
	 * <p>
	 * This will also set {@link PropertyResource#setTypeURI(URI)}.
	 * 
	 * @param type
	 *            the type of the <code>Configuration</code>.
	 */
	@Override
	public void setType(URI type) {
		this.type = type;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Configuration cloneConfig = (Configuration) clone;
		cloneConfig.setConfigures(cloning.cloneOrOriginal(getConfigures()));
	}

	public JsonNode getJsonSchema() {
		return jsonSchema;
	}

	public void setJsonSchema(JsonNode jsonSchema) {
		this.jsonSchema = jsonSchema;
	}

	public void setJsonSchema(String jsonString) {
		setJsonSchema(parseJson(jsonString));
	}

	public void setJson(String jsonString) {
		setJson(parseJson(jsonString));
	}

	protected JsonNode parseJson(String jsonString) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonParser parser = mapper.getFactory().createParser(jsonString);
			return parser.readValueAs(JsonNode.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("Not valid JSON string", e);
		}
	}
}
