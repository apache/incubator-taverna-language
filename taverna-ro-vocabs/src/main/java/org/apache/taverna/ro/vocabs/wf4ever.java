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

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
 
/**
 * Constants for the Research Object wf4ever vocabulary
 * 
 * @see https://w3id.org/ro/2016-01-28/wf4ever
 */
public class wf4ever {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/wf4ever#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );

    public static final DatatypeProperty command = M_MODEL.createDatatypeProperty(NS + "command");
    public static final DatatypeProperty filePath = M_MODEL.createDatatypeProperty(NS + "filePath");
    public static final DatatypeProperty parameterFilePath = M_MODEL.createDatatypeProperty(NS + "parameterFilePath");
    public static final DatatypeProperty rootURI = M_MODEL.createDatatypeProperty(NS + "rootURI");
    public static final DatatypeProperty script = M_MODEL.createDatatypeProperty(NS + "script");
    public static final DatatypeProperty serviceURI = M_MODEL.createDatatypeProperty(NS + "serviceURI");
    public static final DatatypeProperty wsdlOperationName = M_MODEL.createDatatypeProperty(NS + "wsdlOperationName");
    public static final DatatypeProperty wsdlPortName = M_MODEL.createDatatypeProperty(NS + "wsdlPortName");
    public static final DatatypeProperty wsdlURI = M_MODEL.createDatatypeProperty(NS + "wsdlURI");

    public static final OntClass BeanshellScript = M_MODEL.createClass(NS + "BeanshellScript");
    public static final OntClass CommandLineTool = M_MODEL.createClass(NS + "CommandLineTool");
    public static final OntClass Dataset = M_MODEL.createClass(NS + "Dataset");
    public static final OntClass Document = M_MODEL.createClass(NS + "Document");
    public static final OntClass File = M_MODEL.createClass(NS + "File");
    public static final OntClass FileParameter = M_MODEL.createClass(NS + "FileParameter");
    public static final OntClass Image = M_MODEL.createClass(NS + "Image");
    public static final OntClass PythonScript = M_MODEL.createClass(NS + "PythonScript");
    public static final OntClass RScript = M_MODEL.createClass(NS + "RScript");
    public static final OntClass RESTService = M_MODEL.createClass(NS + "RESTService");
    public static final OntClass Script = M_MODEL.createClass(NS + "Script");
    public static final OntClass SOAPService = M_MODEL.createClass(NS + "SOAPService");
    public static final OntClass WebService = M_MODEL.createClass(NS + "WebService");
    public static final OntClass WorkflowResearchObject = M_MODEL.createClass(NS + "WorkflowResearchObject");

}
