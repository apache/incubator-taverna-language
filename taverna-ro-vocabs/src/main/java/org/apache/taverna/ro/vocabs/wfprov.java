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
 * Constants for the Research Object wfprov vocabulary
 * 
 * @see https://w3id.org/ro/2016-01-28/wfprov
 */
public class wfprov {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/wfprov#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );

    public static final ObjectProperty describedByParameter = M_MODEL.createObjectProperty(NS + "describedByParameter");
    public static final ObjectProperty describedByProcess = M_MODEL.createObjectProperty(NS + "describedByProcess");
    public static final ObjectProperty describedByWorkflow = M_MODEL.createObjectProperty(NS + "describedByWorkflow");
    public static final ObjectProperty interactedWith = M_MODEL.createObjectProperty(NS + "interactedWith");
    public static final ObjectProperty usedInput = M_MODEL.createObjectProperty(NS + "usedInput");
    public static final ObjectProperty wasEnactedBy = M_MODEL.createObjectProperty(NS + "wasEnactedBy");
    public static final ObjectProperty wasInitiatedBy = M_MODEL.createObjectProperty(NS + "wasInitiatedBy");    
    public static final ObjectProperty wasOutputFrom = M_MODEL.createObjectProperty(NS + "wasOutputFrom");
    public static final ObjectProperty wasPartOfWorkflowRun = M_MODEL.createObjectProperty(NS + "wasPartOfWorkflowRun");

    public static final OntClass Artifact = M_MODEL.createClass(NS + "Artifact");
    public static final OntClass ProcessRun = M_MODEL.createClass(NS + "ProcessRun");
    public static final OntClass WorkflowEngine = M_MODEL.createClass(NS + "WorkflowEngine");
    public static final OntClass WorkflowRun = M_MODEL.createClass(NS + "WorkflowRun");

}
