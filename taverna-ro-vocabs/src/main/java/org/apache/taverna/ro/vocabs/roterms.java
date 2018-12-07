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
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
 
/**
 * Constants from the Research Object Terms (roterms) vocabulary
 * 
 * @see http://wf4ever.github.io/ro/2016-01-28/roterms/
 */
public class roterms {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/roterms";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    
    public static final OntClass Conclusion = M_MODEL.createClass(NS + "Conclusion");
    public static final OntClass ExampleRun = M_MODEL.createClass(NS + "ExampleRun");
    public static final OntClass Hypothesis = M_MODEL.createClass(NS + "Hypothesis");
    public static final OntClass OptionalInput = M_MODEL.createClass(NS + "OptionalInput");
    public static final OntClass ProspectiveRun = M_MODEL.createClass(NS + "ProspectiveRun");
    public static final OntClass ResearchQuestion = M_MODEL.createClass(NS + "ResearchQuestion");
    public static final OntClass Result = M_MODEL.createClass(NS + "Result");
    public static final OntClass ResultGenerationRun = M_MODEL.createClass(NS + "ResultGenerationRun");
    public static final OntClass Sketch = M_MODEL.createClass(NS + "Sketch");
    public static final OntClass WorkflowValue = M_MODEL.createClass(NS + "WorkflowValue");
    
    public static final ObjectProperty defaultValue = M_MODEL.createObjectProperty(NS + "defaultValue");
    public static final ObjectProperty exampleValue = M_MODEL.createObjectProperty(NS + "exampleValue");
    public static final ObjectProperty ofSemanticType = M_MODEL.createObjectProperty(NS + "ofSemanticType");
    public static final ObjectProperty ofStructuralType = M_MODEL.createObjectProperty(NS + "ofStructuralType");
    public static final ObjectProperty performsTask = M_MODEL.createObjectProperty(NS + "performsTask");
    public static final ObjectProperty previousWorkflow = M_MODEL.createObjectProperty(NS + "previousWorkflow");
    public static final ObjectProperty requiresDataset = M_MODEL.createObjectProperty(NS + "requiresDataset");
    public static final ObjectProperty requiresHardware = M_MODEL.createObjectProperty(NS + "requiresHardware");
    public static final ObjectProperty requiresSoftware = M_MODEL.createObjectProperty(NS + "requiresSoftware");
    public static final ObjectProperty subsequentWorkflow = M_MODEL.createObjectProperty(NS + "subsequentWorkflow");
    public static final ObjectProperty technicalContact = M_MODEL.createObjectProperty(NS + "technicalContact");

    public static final DatatypeProperty sampleSize = M_MODEL.createDatatypeProperty(NS + "sampleSize");
}

