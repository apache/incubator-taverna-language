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

import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
 

/**
 * Constants for the Research Object bundle vocabulary
 * 
 * @see https://w3id.org/bundle/owl
 */
public class bundle {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/bundle";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final ObjectProperty bundledAs = M_MODEL.createObjectProperty( "http://purl.org/wf4ever/bundle#bundledAs" );
    public static final ObjectProperty hasAnnotation = M_MODEL.createObjectProperty( "http://purl.org/wf4ever/bundle#hasAnnotation" );
    public static final ObjectProperty hasProxy = M_MODEL.createObjectProperty( "http://purl.org/wf4ever/bundle#hasProxy" );
    public static final ObjectProperty inFolder = M_MODEL.createObjectProperty( "http://purl.org/wf4ever/bundle#inFolder" );
}
