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
 *  
 */
package org.apache.taverna.ro.vocabs;

import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
 
/**
 * Constants for the Research Object wfdesc vocabulary
 * 
 * @see https://w3id.org/ro/2016-01-28/wfdesc
 */
public class wfdesc {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/wfdesc#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final ObjectProperty hasArtifact = M_MODEL.createObjectProperty(NS + "hasArtifact");
    public static final ObjectProperty hasConfiguration = M_MODEL.createObjectProperty(NS + "hasConfiguration");    
    public static final ObjectProperty hasDataLink = M_MODEL.createObjectProperty(NS + "hasDataLink");
    public static final ObjectProperty hasImplementation = M_MODEL.createObjectProperty(NS + "hasImplementation");    
    public static final ObjectProperty hasInput = M_MODEL.createObjectProperty(NS + "hasInput");
    public static final ObjectProperty hasOutput = M_MODEL.createObjectProperty(NS + "hasOutput");
    public static final ObjectProperty hasSink = M_MODEL.createObjectProperty(NS + "hasSink");
    public static final ObjectProperty hasSource = M_MODEL.createObjectProperty(NS + "hasSource");
    public static final ObjectProperty hasSubProcess = M_MODEL.createObjectProperty(NS + "hasSubProcess");
    public static final ObjectProperty hasSubWorkflow = M_MODEL.createObjectProperty(NS + "hasSubWorkflow");
    public static final ObjectProperty hasWorkflowDefinition = M_MODEL.createObjectProperty(NS + "hasWorkflowDefinition");    
    
    public static final OntClass Artifact = M_MODEL.createClass(NS + "Artifact");
    public static final OntClass Configuration = M_MODEL.createClass(NS + "Configuration");
    public static final OntClass DataLink = M_MODEL.createClass(NS + "DataLink");
    public static final OntClass Input = M_MODEL.createClass(NS + "Input");
    public static final OntClass Output = M_MODEL.createClass(NS + "Output");
    public static final OntClass Parameter = M_MODEL.createClass(NS + "Parameter");
    public static final OntClass Process = M_MODEL.createClass(NS + "Process");
    public static final OntClass ProcessImplementation = M_MODEL.createClass(NS + "ProcessImplementation");
    public static final OntClass Workflow = M_MODEL.createClass(NS + "Workflow");
    public static final OntClass WorkflowDefinition = M_MODEL.createClass(NS + "WorkflowDefinition");
    public static final OntClass WorkflowInstance = M_MODEL.createClass(NS + "WorkflowInstance");

}
