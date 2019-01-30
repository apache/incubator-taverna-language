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
 * Constants for the Web Annotation ontology
 * 
 * @see https://www.w3.org/ns/oa
 * @see https://www.w3.org/TR/2017/REC-annotation-vocab-20170223/
 */
public class oa {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null);
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/ns/oa#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS);
  
    public static final ObjectProperty annotationService = M_MODEL.createObjectProperty(NS + "annotationService");
    public static final ObjectProperty cachedSource = M_MODEL.createObjectProperty(NS + "cachedSource");
    public static final ObjectProperty hasBody = M_MODEL.createObjectProperty(NS + "hasBody");
    public static final ObjectProperty hasEndSelector = M_MODEL.createObjectProperty(NS + "hasEndSelector");
    public static final ObjectProperty hasPurpose = M_MODEL.createObjectProperty(NS + "hasPurpose");    
    public static final ObjectProperty hasScope = M_MODEL.createObjectProperty(NS + "hasScope");
    public static final ObjectProperty hasSelector = M_MODEL.createObjectProperty(NS + "hasSelector");
    public static final ObjectProperty hasSource = M_MODEL.createObjectProperty(NS + "hasSource");
    public static final ObjectProperty hasStartSelector = M_MODEL.createObjectProperty(NS + "hasStartSelector");
    public static final ObjectProperty hasState = M_MODEL.createObjectProperty(NS + "hasState");
    public static final ObjectProperty hasTarget = M_MODEL.createObjectProperty(NS + "hasTarget");
    public static final ObjectProperty motivatedBy = M_MODEL.createObjectProperty(NS + "motivatedBy");
    public static final ObjectProperty refinedBy = M_MODEL.createObjectProperty(NS + "refinedBy");
    public static final ObjectProperty renderedVia = M_MODEL.createObjectProperty(NS + "renderedVia");
    public static final ObjectProperty styledBy = M_MODEL.createObjectProperty(NS + "styledBy");
    public static final ObjectProperty textDirection = M_MODEL.createObjectProperty(NS + "textDirection");
    public static final ObjectProperty via = M_MODEL.createObjectProperty(NS + "via");
    
    public static final DatatypeProperty bodyValue = M_MODEL.createDatatypeProperty(NS + "bodyValue");
    public static final DatatypeProperty canonical = M_MODEL.createDatatypeProperty(NS + "canonical");
    public static final DatatypeProperty end = M_MODEL.createDatatypeProperty(NS + "end");    
    public static final DatatypeProperty exact = M_MODEL.createDatatypeProperty(NS + "exact");
    public static final DatatypeProperty prefix = M_MODEL.createDatatypeProperty(NS + "prefix");        
    public static final DatatypeProperty processingLanguage = M_MODEL.createDatatypeProperty(NS + "processingLanguage");
    public static final DatatypeProperty sourceDate = M_MODEL.createDatatypeProperty(NS + "sourceDate");
    public static final DatatypeProperty sourceDateEnd = M_MODEL.createDatatypeProperty(NS + "sourceDateEnd");
    public static final DatatypeProperty sourceDateStart = M_MODEL.createDatatypeProperty(NS + "sourceDateStart");
    public static final DatatypeProperty start = M_MODEL.createDatatypeProperty(NS + "start");    
    public static final DatatypeProperty styleClass = M_MODEL.createDatatypeProperty(NS + "styleClass");
    public static final DatatypeProperty suffix = M_MODEL.createDatatypeProperty(NS + "suffix");
    
    public static final OntClass Annotation = M_MODEL.createClass(NS + "Annotation");
    public static final OntClass Choice = M_MODEL.createClass(NS + "Choice");
    public static final OntClass CssSelector = M_MODEL.createClass(NS + "CssSelector");
    public static final OntClass CssStyle = M_MODEL.createClass(NS + "CssStyle");
    public static final OntClass DataPositionSelector = M_MODEL.createClass(NS + "DataPositionSelector");
    public static final OntClass Direction = M_MODEL.createClass(NS + "Direction");
    public static final OntClass FragmentSelector = M_MODEL.createClass(NS + "FragmentSelector");    
    public static final OntClass HttpRequestState = M_MODEL.createClass(NS + "HttpRequestState");    
    public static final OntClass Motivation = M_MODEL.createClass(NS + "Motivation");
    public static final OntClass RangeSelector = M_MODEL.createClass(NS + "RangeSelector");
    public static final OntClass ResourceSelection = M_MODEL.createClass(NS + "ResourceSelection");
    public static final OntClass Selector = M_MODEL.createClass(NS + "Selector");
    public static final OntClass SpecificResource = M_MODEL.createClass(NS + "SpecificResource");
    public static final OntClass State = M_MODEL.createClass(NS + "State");
    public static final OntClass Style = M_MODEL.createClass(NS + "Style");
    public static final OntClass SvgSelector = M_MODEL.createClass(NS + "SvgSelector");    
    public static final OntClass TextPositionSelector = M_MODEL.createClass(NS + "TextPositionSelector");
    public static final OntClass TextQuoteSelector = M_MODEL.createClass(NS + "TextQuoteSelector");
    public static final OntClass TextualBody = M_MODEL.createClass(NS + "TextualBody");
    public static final OntClass TimeState = M_MODEL.createClass(NS + "TimeState");
    public static final OntClass XPathSelector = M_MODEL.createClass(NS + "XPathSelector");

    private static final OntClass ConceptScheme = M_MODEL.createClass( "http://www.w3.org/2004/02/skos/core#ConceptScheme");
    private static final OntClass Concept = M_MODEL.createClass( "http://www.w3.org/2004/02/skos/core#Concept") ;
    
    public static final Individual motivationScheme = M_MODEL.createIndividual(NS + "motivationScheme", ConceptScheme);
    public static final Individual assessing = M_MODEL.createIndividual(NS + "assessing", Motivation);    
    public static final Individual bookmarking = M_MODEL.createIndividual(NS + "bookmarking", Motivation);
    public static final Individual classifying = M_MODEL.createIndividual(NS + "classifying", Motivation);
    public static final Individual commenting = M_MODEL.createIndividual(NS + "commenting", Motivation);
    public static final Individual describing = M_MODEL.createIndividual(NS + "describing", Motivation);
    public static final Individual editing = M_MODEL.createIndividual(NS + "editing", Motivation);
    public static final Individual highlighting = M_MODEL.createIndividual(NS + "highlighting", Motivation);    
    public static final Individual identifying = M_MODEL.createIndividual(NS + "identifying", Motivation);
    public static final Individual linking = M_MODEL.createIndividual(NS + "linking", Motivation);
    public static final Individual moderating = M_MODEL.createIndividual(NS + "moderating", Motivation);
    public static final Individual questioning = M_MODEL.createIndividual(NS + "questioning", Motivation);
    public static final Individual replying = M_MODEL.createIndividual(NS + "replying", Motivation);
    public static final Individual tagging = M_MODEL.createIndividual(NS + "tagging", Motivation);

    public static final Individual ltrDirection = M_MODEL.createIndividual(NS + "ltrDirection", Direction);
    public static final Individual rtlDirection = M_MODEL.createIndividual(NS + "rtlDirection", Direction);
    public static final Individual PreferContainedDescriptions = M_MODEL.createIndividual(NS + "PreferContainedDescriptions", Concept);
    public static final Individual PreferContainedIRIs = M_MODEL.createIndividual(NS + "PreferContainedIRIs", Concept);

}

