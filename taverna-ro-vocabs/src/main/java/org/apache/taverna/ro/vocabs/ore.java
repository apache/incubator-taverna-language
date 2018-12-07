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
 * Constants for the OAI ORE terms vocabulary
 * 
 * @see http://www.openarchives.org/ore/terms/
 * @see http://www.openarchives.org/ore/toc"
 */
public class ore {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.openarchives.org/ore/terms/";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final AnnotationProperty similarTo = M_MODEL.createAnnotationProperty(NS + "similarTo");

    public static final OntClass AggregatedResource = M_MODEL.createClass(NS + "AggregatedResource");
    public static final OntClass Aggregation = M_MODEL.createClass(NS + "Aggregation");
    public static final OntClass Proxy = M_MODEL.createClass(NS + "Proxy");
    public static final OntClass ResourceMap = M_MODEL.createClass(NS + "ResourceMap");

    public static final OntClass Graph = M_MODEL.createClass("http://www.w3.org/2004/03/trix/rdfg-1/Graph");

    public static final ObjectProperty aggregates = M_MODEL.createObjectProperty(NS + "aggregates");    
    public static final ObjectProperty describes = M_MODEL.createObjectProperty(NS + "describes");    
    public static final ObjectProperty isAggregatedBy = M_MODEL.createObjectProperty(NS + "isAggregatedBy");    
    public static final ObjectProperty isDescribedBy = M_MODEL.createObjectProperty(NS + "isDescribedBy");    
    public static final ObjectProperty lineage = M_MODEL.createObjectProperty(NS + "lineage");    
    public static final ObjectProperty proxyFor = M_MODEL.createObjectProperty(NS + "proxyFor");    
    public static final ObjectProperty proxyIn = M_MODEL.createObjectProperty(NS + "proxyIn");    
}
