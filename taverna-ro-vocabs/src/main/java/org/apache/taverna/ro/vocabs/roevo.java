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
 * Constants for the Research Object roevo vocabulary
 * 
 * @see https://w3id.org/ro/2016-01-28/roevo
 */
public class roevo {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://purl.org/wf4ever/roevo#";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}
    
    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final OntClass Addition = M_MODEL.createClass( NS + "Addition" );
    
    public static final OntClass AggregatedAnnotation = M_MODEL.createClass( NS + "AggregatedAnnotation" );
    public static final OntClass ArchivedRO = M_MODEL.createClass( NS + "ArchivedRO" );
    public static final OntClass Change = M_MODEL.createClass( NS + "Change" );
    public static final OntClass ChangeSpecification = M_MODEL.createClass( NS + "ChangeSpecification" );
    public static final OntClass LiveRO = M_MODEL.createClass( NS + "LiveRO" );
    public static final OntClass Modification = M_MODEL.createClass( NS + "Modification" );
    public static final OntClass Removal = M_MODEL.createClass( NS + "Removal" );
    public static final OntClass SnapshotRO = M_MODEL.createClass( NS + "SnapshotRO" );
    public static final OntClass VersionableResource = M_MODEL.createClass( NS + "VersionableResource" );

    public static final ObjectProperty fromVersion = M_MODEL.createObjectProperty( NS + "fromVersion" );
    public static final ObjectProperty hasArchive = M_MODEL.createObjectProperty( NS + "hasArchive" );
    public static final ObjectProperty hasChange = M_MODEL.createObjectProperty( NS + "hasChange" );
    public static final ObjectProperty hasPreviousChange = M_MODEL.createObjectProperty( NS + "hasPreviousChange" );
    public static final ObjectProperty hasRevision = M_MODEL.createObjectProperty( NS + "hasRevision" );
    public static final ObjectProperty hasSnapshot = M_MODEL.createObjectProperty( NS + "hasSnapshot" );
    public static final ObjectProperty isArchiveOf = M_MODEL.createObjectProperty( NS + "isArchiveOf" );
    public static final ObjectProperty relatedResource = M_MODEL.createObjectProperty( NS + "relatedResource" );
    public static final ObjectProperty toVersion = M_MODEL.createObjectProperty( NS + "toVersion" );
    public static final ObjectProperty wasArchivedBy = M_MODEL.createObjectProperty( NS + "wasArchivedBy" );
    public static final ObjectProperty wasChangedBy = M_MODEL.createObjectProperty( NS + "wasChangedBy" );
    public static final ObjectProperty wasSnapshotedBy = M_MODEL.createObjectProperty( NS + "wasSnapshotedBy" );
       
    public static final DatatypeProperty archivedAtTime = M_MODEL.createDatatypeProperty( NS + "archivedAtTime" );
    // Note: Typo "snapshoted" in upstream vocabulary
    public static final DatatypeProperty snapshottedAtTime = M_MODEL.createDatatypeProperty( NS + "snapshotedAtTime" );
}

