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

import org.apache.jena.ontology.AnnotationProperty;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
 
/**
 * Constants from the W3C Prov-O vocabulary
 * 
 * @see https://www.w3.org/TR/prov-o/
 */
public class prov {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://www.w3.org/ns/prov#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final ObjectProperty actedOnBehalfOf = M_MODEL.createObjectProperty(NS + "actedOnBehalfOf");
    public static final ObjectProperty activity = M_MODEL.createObjectProperty(NS + "activity");
    public static final ObjectProperty agent = M_MODEL.createObjectProperty(NS + "agent");
    public static final ObjectProperty alternateOf = M_MODEL.createObjectProperty(NS + "alternateOf");
    public static final ObjectProperty atLocation = M_MODEL.createObjectProperty(NS + "atLocation");
    public static final ObjectProperty entity = M_MODEL.createObjectProperty(NS + "entity");
    public static final ObjectProperty generated = M_MODEL.createObjectProperty(NS + "generated");
    public static final ObjectProperty hadActivity = M_MODEL.createObjectProperty(NS + "hadActivity");
    public static final ObjectProperty hadGeneration = M_MODEL.createObjectProperty(NS + "hadGeneration");
    public static final ObjectProperty hadMember = M_MODEL.createObjectProperty(NS + "hadMember");
    public static final ObjectProperty hadPlan = M_MODEL.createObjectProperty(NS + "hadPlan");
    public static final ObjectProperty hadPrimarySource = M_MODEL.createObjectProperty(NS + "hadPrimarySource");
    public static final ObjectProperty hadRole = M_MODEL.createObjectProperty(NS + "hadRole");
    public static final ObjectProperty hadUsage = M_MODEL.createObjectProperty(NS + "hadUsage");
    public static final ObjectProperty influenced = M_MODEL.createObjectProperty(NS + "influenced");
    public static final ObjectProperty influencer = M_MODEL.createObjectProperty(NS + "influencer");
    public static final ObjectProperty invalidated = M_MODEL.createObjectProperty(NS + "invalidated");

    public static final ObjectProperty qualifiedAssociation = M_MODEL.createObjectProperty(NS + "qualifiedAssociation");
    public static final ObjectProperty qualifiedAttribution = M_MODEL.createObjectProperty(NS + "qualifiedAttribution");
    public static final ObjectProperty qualifiedCommunication = M_MODEL.createObjectProperty(NS + "qualifiedCommunication");
    public static final ObjectProperty qualifiedDelegation = M_MODEL.createObjectProperty(NS + "qualifiedDelegation");
    public static final ObjectProperty qualifiedDerivation = M_MODEL.createObjectProperty(NS + "qualifiedDerivation");
    public static final ObjectProperty qualifiedEnd = M_MODEL.createObjectProperty(NS + "qualifiedEnd");
    public static final ObjectProperty qualifiedGeneration = M_MODEL.createObjectProperty(NS + "qualifiedGeneration");
    public static final ObjectProperty qualifiedInfluence = M_MODEL.createObjectProperty(NS + "qualifiedInfluence");
    public static final ObjectProperty qualifiedInvalidation = M_MODEL.createObjectProperty(NS + "qualifiedInvalidation");
    public static final ObjectProperty qualifiedPrimarySource = M_MODEL.createObjectProperty(NS + "qualifiedPrimarySource");
    public static final ObjectProperty qualifiedQuotation = M_MODEL.createObjectProperty(NS + "qualifiedQuotation");
    public static final ObjectProperty qualifiedRevision = M_MODEL.createObjectProperty(NS + "qualifiedRevision");
    public static final ObjectProperty qualifiedStart = M_MODEL.createObjectProperty(NS + "qualifiedStart");
    public static final ObjectProperty qualifiedUsage = M_MODEL.createObjectProperty(NS + "qualifiedUsage");

    public static final ObjectProperty specializationOf = M_MODEL.createObjectProperty(NS + "specializationOf");
    public static final ObjectProperty used = M_MODEL.createObjectProperty(NS + "used");
    public static final ObjectProperty wasAssociatedWith = M_MODEL.createObjectProperty(NS + "wasAssociatedWith");
    public static final ObjectProperty wasAttributedTo = M_MODEL.createObjectProperty(NS + "wasAttributedTo");
    public static final ObjectProperty wasDerivedFrom = M_MODEL.createObjectProperty(NS + "wasDerivedFrom");
    public static final ObjectProperty wasEndedBy = M_MODEL.createObjectProperty(NS + "wasEndedBy");
    public static final ObjectProperty wasGeneratedBy = M_MODEL.createObjectProperty(NS + "wasGeneratedBy");
    public static final ObjectProperty wasInfluencedBy = M_MODEL.createObjectProperty(NS + "wasInfluencedBy");
    public static final ObjectProperty wasInformedBy = M_MODEL.createObjectProperty(NS + "wasInformedBy");
    public static final ObjectProperty wasInvalidatedBy = M_MODEL.createObjectProperty(NS + "wasInvalidatedBy");
    public static final ObjectProperty wasQuotedFrom = M_MODEL.createObjectProperty(NS + "wasQuotedFrom");
    public static final ObjectProperty wasRevisionOf = M_MODEL.createObjectProperty(NS + "wasRevisionOf");
    public static final ObjectProperty wasStartedBy = M_MODEL.createObjectProperty(NS + "wasStartedBy");
    
    public static final DatatypeProperty atTime = M_MODEL.createDatatypeProperty(NS + "atTime");
    public static final DatatypeProperty endedAtTime = M_MODEL.createDatatypeProperty(NS + "endedAtTime");
    public static final DatatypeProperty generatedAtTime = M_MODEL.createDatatypeProperty(NS + "generatedAtTime");
    public static final DatatypeProperty invalidatedAtTime = M_MODEL.createDatatypeProperty(NS + "invalidatedAtTime");
    public static final DatatypeProperty startedAtTime = M_MODEL.createDatatypeProperty(NS + "startedAtTime");
    public static final DatatypeProperty value = M_MODEL.createDatatypeProperty(NS + "value");
    
    public static final AnnotationProperty aq = M_MODEL.createAnnotationProperty(NS + "aq");
    public static final AnnotationProperty category = M_MODEL.createAnnotationProperty(NS + "category");
    public static final AnnotationProperty component = M_MODEL.createAnnotationProperty(NS + "component");
    public static final AnnotationProperty constraints = M_MODEL.createAnnotationProperty(NS + "constraints");
    public static final AnnotationProperty definition = M_MODEL.createAnnotationProperty(NS + "definition");
    public static final AnnotationProperty dm = M_MODEL.createAnnotationProperty(NS + "dm");
    public static final AnnotationProperty editorialNote = M_MODEL.createAnnotationProperty(NS + "editorialNote");
    public static final AnnotationProperty editorsDefinition = M_MODEL.createAnnotationProperty(NS + "editorsDefinition");
    public static final AnnotationProperty inverse = M_MODEL.createAnnotationProperty(NS + "inverse");
    public static final AnnotationProperty n = M_MODEL.createAnnotationProperty(NS + "n");
    public static final AnnotationProperty order = M_MODEL.createAnnotationProperty(NS + "order");
    public static final AnnotationProperty qualifiedForm = M_MODEL.createAnnotationProperty(NS + "qualifiedForm");
    public static final AnnotationProperty sharesDefinitionWith = M_MODEL.createAnnotationProperty(NS + "sharesDefinitionWith");
    public static final AnnotationProperty todo = M_MODEL.createAnnotationProperty(NS + "todo");
    public static final AnnotationProperty unqualifiedForm = M_MODEL.createAnnotationProperty(NS + "unqualifiedForm");
    
    public static final OntClass Activity = M_MODEL.createClass(NS + "Activity");
    public static final OntClass ActivityInfluence = M_MODEL.createClass(NS + "ActivityInfluence");
    public static final OntClass Agent = M_MODEL.createClass(NS + "Agent");
    public static final OntClass AgentInfluence = M_MODEL.createClass(NS + "AgentInfluence");
    public static final OntClass Association = M_MODEL.createClass(NS + "Association");
    public static final OntClass Attribution = M_MODEL.createClass(NS + "Attribution");
    public static final OntClass Bundle = M_MODEL.createClass(NS + "Bundle");
    public static final OntClass Collection = M_MODEL.createClass(NS + "Collection");
    public static final OntClass Communication = M_MODEL.createClass(NS + "Communication");
    public static final OntClass Delegation = M_MODEL.createClass(NS + "Delegation");
    public static final OntClass Derivation = M_MODEL.createClass(NS + "Derivation");
    public static final OntClass EmptyCollection = M_MODEL.createClass(NS + "EmptyCollection");
    public static final OntClass End = M_MODEL.createClass(NS + "End");
    public static final OntClass Entity = M_MODEL.createClass(NS + "Entity");
    public static final OntClass EntityInfluence = M_MODEL.createClass(NS + "EntityInfluence");
    public static final OntClass Generation = M_MODEL.createClass(NS + "Generation");
    public static final OntClass Influence = M_MODEL.createClass(NS + "Influence");
    public static final OntClass InstantaneousEvent = M_MODEL.createClass(NS + "InstantaneousEvent");
    public static final OntClass Invalidation = M_MODEL.createClass(NS + "Invalidation");
    public static final OntClass Location = M_MODEL.createClass(NS + "Location");
    public static final OntClass Organization = M_MODEL.createClass(NS + "Organization");
    public static final OntClass Person = M_MODEL.createClass(NS + "Person");
    public static final OntClass Plan = M_MODEL.createClass(NS + "Plan");
    public static final OntClass PrimarySource = M_MODEL.createClass(NS + "PrimarySource");
    public static final OntClass Quotation = M_MODEL.createClass(NS + "Quotation");
    public static final OntClass Revision = M_MODEL.createClass(NS + "Revision");
    public static final OntClass Role = M_MODEL.createClass(NS + "Role");
    public static final OntClass SoftwareAgent = M_MODEL.createClass(NS + "SoftwareAgent");
    public static final OntClass Start = M_MODEL.createClass(NS + "Start");
    public static final OntClass Usage = M_MODEL.createClass(NS + "Usage");
    
	/**
	 * Constants from the W3C Prov-AQ extension to Prov vocabulary
	 * 
	 * @see https://www.w3.org/TR/prov-aq/
	 */    	    
    public static class AQ { 
	    public static final ObjectProperty describesService = M_MODEL.createObjectProperty(NS + "describesService");
	    public static final ObjectProperty has_anchor = M_MODEL.createObjectProperty(NS + "has_anchor");
	    public static final ObjectProperty has_provenance = M_MODEL.createObjectProperty(NS + "has_provenance");
	    public static final ObjectProperty has_provenance_service = M_MODEL.createObjectProperty(NS + "has_provenance_service");
	    public static final ObjectProperty has_query_service = M_MODEL.createObjectProperty(NS + "has_query_service");
	    public static final ObjectProperty pingback = M_MODEL.createObjectProperty(NS + "pingback");
	        
	    public static final DatatypeProperty provenanceUriTemplate = M_MODEL.createDatatypeProperty(NS + "provenanceUriTemplate");
	        
	    public static final OntClass ServiceDescription = M_MODEL.createClass(NS + "ServiceDescription");
	    public static final OntClass DirectQueryService = M_MODEL.createClass(NS + "DirectQueryService");
    }
    
}
