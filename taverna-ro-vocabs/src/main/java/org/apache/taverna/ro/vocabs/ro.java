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
import org.apache.jena.vocabulary.DCTerms;
 
/**
 * Constants for the Research Object ro vocabulary
 * 
 * @see https://w3id.org/ro/2016-01-28/ro
 */
public class ro {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/ro#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final OntClass AggregatedAnnotation = M_MODEL.createClass( NS + "AggregatedAnnotation" );
    public static final OntClass Folder = M_MODEL.createClass( NS + "Folder" );
    public static final OntClass FolderEntry = M_MODEL.createClass( NS + "FolderEntry" );
    public static final OntClass Manifest = M_MODEL.createClass( NS + "Manifest" );
    public static final OntClass ResearchObject = M_MODEL.createClass( NS + "ResearchObject" );
    public static final OntClass Resource = M_MODEL.createClass( NS + "Resource" );

    public static final ObjectProperty annotatesAggregatedResource = M_MODEL.createObjectProperty( NS + "annotatesAggregatedResource" );
    public static final ObjectProperty rootFolder = M_MODEL.createObjectProperty( NS + "rootFolder" );
    
    public static final ObjectProperty conformsTo = M_MODEL.createObjectProperty(DCTerms.conformsTo.getURI());
    
    
    public static final DatatypeProperty entryName = M_MODEL.createDatatypeProperty( NS + "entryName" );
}
