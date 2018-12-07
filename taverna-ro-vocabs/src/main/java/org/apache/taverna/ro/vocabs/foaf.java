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
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
 
/**
 * Constants from the Friend of a Friend (FOAF) namespace
 * 
 * @see http://xmlns.com/foaf/0.1/
 * @see FOAF
 *
 */
public class foaf {
    /** <p>The ontology model that holds the vocabulary terms</p> */
    private static final OntModel M_MODEL = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    
    /** <p>The namespace of the vocabulary as a string</p> */
    public static final String NS = "http://xmlns.com/foaf/0.1/";
    
    /** <p>The namespace of the vocabulary as a string</p>
     * @return namespace as String
     * @see #NS */
    public static String getURI() {return NS;}

    /** <p>The namespace of the vocabulary as a resource</p> */
    public static final Resource NAMESPACE = M_MODEL.createResource( NS );
    
    public static final ObjectProperty account = M_MODEL.createObjectProperty(NS + "account");
    public static final ObjectProperty accountServiceHomepage = M_MODEL.createObjectProperty(NS + "accountServiceHomepage");
    public static final ObjectProperty based_near = M_MODEL.createObjectProperty(NS + "based_near");
    public static final ObjectProperty currentProject = M_MODEL.createObjectProperty(NS + "currentProject");
    public static final ObjectProperty depiction = M_MODEL.createObjectProperty(NS + "depiction");
    public static final ObjectProperty depicts = M_MODEL.createObjectProperty(NS + "depicts");
    public static final ObjectProperty fundedBy = M_MODEL.createObjectProperty(NS + "fundedBy");
    public static final ObjectProperty holdsAccount = M_MODEL.createObjectProperty(NS + "holdsAccount");
    public static final ObjectProperty homepage = M_MODEL.createObjectProperty(NS + "homepage");    
    public static final ObjectProperty img = M_MODEL.createObjectProperty(NS + "img");    
    public static final ObjectProperty interest = M_MODEL.createObjectProperty(NS + "interest");
    public static final ObjectProperty knows = M_MODEL.createObjectProperty(NS + "knows");
    public static final ObjectProperty logo = M_MODEL.createObjectProperty(NS + "logo");
    public static final ObjectProperty made = M_MODEL.createObjectProperty(NS + "made");
    public static final ObjectProperty maker = M_MODEL.createObjectProperty(NS + "maker");
    public static final ObjectProperty mbox = M_MODEL.createObjectProperty(NS + "mbox");
    public static final ObjectProperty member = M_MODEL.createObjectProperty(NS + "member");
    public static final ObjectProperty openid = M_MODEL.createObjectProperty(NS + "openid");
    public static final ObjectProperty page = M_MODEL.createObjectProperty(NS + "page");
    public static final ObjectProperty pastProject = M_MODEL.createObjectProperty(NS + "pastProject");
    public static final ObjectProperty phone = M_MODEL.createObjectProperty(NS + "phone");    
    public static final ObjectProperty primaryTopic = M_MODEL.createObjectProperty(NS + "primaryTopic");
    public static final ObjectProperty publications = M_MODEL.createObjectProperty(NS + "publications");
    public static final ObjectProperty schoolHomepage = M_MODEL.createObjectProperty(NS + "schoolHomepage");
    public static final ObjectProperty theme = M_MODEL.createObjectProperty(NS + "theme");
    public static final ObjectProperty thumbnail = M_MODEL.createObjectProperty(NS + "thumbnail");
    public static final ObjectProperty tipjar = M_MODEL.createObjectProperty(NS + "tipjar");
    public static final ObjectProperty topic = M_MODEL.createObjectProperty(NS + "topic");
    public static final ObjectProperty topic_interest = M_MODEL.createObjectProperty(NS + "topic_interest");
    public static final ObjectProperty weblog = M_MODEL.createObjectProperty(NS + "weblog");
    public static final ObjectProperty workInfoHomepage = M_MODEL.createObjectProperty(NS + "workInfoHomepage");
    public static final ObjectProperty workplaceHomepage = M_MODEL.createObjectProperty(NS + "workplaceHomepage");
    
    public static final DatatypeProperty accountName = M_MODEL.createDatatypeProperty(NS + "accountName");
    public static final DatatypeProperty age = M_MODEL.createDatatypeProperty(NS + "age");
    public static final DatatypeProperty aimChatID = M_MODEL.createDatatypeProperty(NS + "aimChatID");
    public static final DatatypeProperty birthday = M_MODEL.createDatatypeProperty(NS + "birthday");
    public static final DatatypeProperty dnaChecksum = M_MODEL.createDatatypeProperty(NS + "dnaChecksum");
    public static final DatatypeProperty familyName = M_MODEL.createDatatypeProperty(NS + "familyName");
    public static final DatatypeProperty family_name = M_MODEL.createDatatypeProperty(NS + "family_name");
    public static final DatatypeProperty firstName = M_MODEL.createDatatypeProperty(NS + "firstName");
    public static final DatatypeProperty geekcode = M_MODEL.createDatatypeProperty(NS + "geekcode");
    public static final DatatypeProperty gender = M_MODEL.createDatatypeProperty(NS + "gender");
    public static final DatatypeProperty givenName = M_MODEL.createDatatypeProperty(NS + "givenName");
    public static final DatatypeProperty givenname = M_MODEL.createDatatypeProperty(NS + "givenname");
    public static final DatatypeProperty icqChatID = M_MODEL.createDatatypeProperty(NS + "icqChatID");
    public static final DatatypeProperty jabberID = M_MODEL.createDatatypeProperty(NS + "jabberID");
    public static final DatatypeProperty lastName = M_MODEL.createDatatypeProperty(NS + "lastName");
    public static final DatatypeProperty mbox_sha1sum = M_MODEL.createDatatypeProperty(NS + "mbox_sha1sum");
    public static final DatatypeProperty msnChatID = M_MODEL.createDatatypeProperty(NS + "msnChatID");
    public static final DatatypeProperty myersBriggs = M_MODEL.createDatatypeProperty(NS + "myersBriggs");
    public static final DatatypeProperty name = M_MODEL.createDatatypeProperty(NS + "name");
    public static final DatatypeProperty nick = M_MODEL.createDatatypeProperty(NS + "nick");
    public static final DatatypeProperty plan = M_MODEL.createDatatypeProperty(NS + "plan");
    public static final DatatypeProperty sha1 = M_MODEL.createDatatypeProperty(NS + "sha1");
    public static final DatatypeProperty skypeID = M_MODEL.createDatatypeProperty(NS + "skypeID");
    public static final DatatypeProperty status = M_MODEL.createDatatypeProperty(NS + "status");
    public static final DatatypeProperty surname = M_MODEL.createDatatypeProperty(NS + "surname");
    public static final DatatypeProperty title = M_MODEL.createDatatypeProperty(NS + "title");
    public static final DatatypeProperty yahooChatID = M_MODEL.createDatatypeProperty(NS + "yahooChatID");

    public static final AnnotationProperty membershipClass = M_MODEL.createAnnotationProperty(NS + "membershipClass");
    
    public static final OntProperty isPrimaryTopicOf = M_MODEL.createOntProperty(NS + "isPrimaryTopicOf");

    public static final OntClass Agent = M_MODEL.createClass(NS + "Agent");
    public static final OntClass Document = M_MODEL.createClass(NS + "Document");
    public static final OntClass Group = M_MODEL.createClass(NS + "Group");
    public static final OntClass Image = M_MODEL.createClass(NS + "Image");
    public static final OntClass LabelProperty = M_MODEL.createClass(NS + "LabelProperty");
    public static final OntClass OnlineAccount = M_MODEL.createClass(NS + "OnlineAccount");
    public static final OntClass OnlineChatAccount = M_MODEL.createClass(NS + "OnlineChatAccount");
    public static final OntClass OnlineEcommerceAccount = M_MODEL.createClass(NS + "OnlineEcommerceAccount");
    public static final OntClass OnlineGamingAccount = M_MODEL.createClass(NS + "OnlineGamingAccount");
    public static final OntClass Organization = M_MODEL.createClass(NS + "Organization");
    public static final OntClass Person = M_MODEL.createClass(NS + "Person");
    public static final OntClass PersonalProfileDocument = M_MODEL.createClass(NS + "PersonalProfileDocument");    
    public static final OntClass Project = M_MODEL.createClass(NS + "Project");
    
}
