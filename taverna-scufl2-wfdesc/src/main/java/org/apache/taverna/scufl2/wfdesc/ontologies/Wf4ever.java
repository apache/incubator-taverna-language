/* CVS $Id: $ */
package org.apache.taverna.scufl2.wfdesc.ontologies; 
import org.apache.jena.rdf.model.*;
import org.apache.jena.ontology.*;
 
/**
 * Vocabulary definitions from file:/home/stain/src/incubator-taverna-language/taverna-scufl2-wfdesc/src/main/resources/org/purl/wf4ever/wfdesc/wf4ever.ttl 
 * @author Auto-generated by schemagen on 21 Nov 2018 10:58 
 */
public class Wf4ever {
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
    
    public static final DatatypeProperty command = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#command" );
    
    /** <p>The root of the service. This is not necessarily the 'base' of the service, 
     *  but should more predictably be accessible for HTTP HEAD. Example: if the REST 
     *  service template is &lt;http://kronos.ifs.tuwien.ac.at:8080/fex/featureExtractionREST?voucher={voucher}&amp;music={mp3Base64}&gt; 
     *  then the wf4ever:rootURI is &lt;http://kronos.ifs.tuwien.ac.at:8080/&gt;.</p>
     */
    public static final DatatypeProperty rootURI = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#rootURI" );
    
    public static final DatatypeProperty script = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#script" );
    
    public static final DatatypeProperty serviceURI = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#serviceURI" );
    
    public static final DatatypeProperty wsdlOperationName = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#wsdlOperationName" );
    
    public static final DatatypeProperty wsdlPortName = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#wsdlPortName" );
    
    public static final DatatypeProperty wsdlURI = M_MODEL.createDatatypeProperty( "http://purl.org/wf4ever/wf4ever#wsdlURI" );
    
    public static final OntClass BeanshellScript = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#BeanshellScript" );
    
    public static final OntClass CommandLineTool = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#CommandLineTool" );
    
    public static final OntClass Dataset = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#Dataset" );
    
    public static final OntClass Document = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#Document" );
    
    public static final OntClass File = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#File" );
    
    public static final OntClass Image = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#Image" );
    
    /** <p>A RESTful web service</p> */
    public static final OntClass RESTService = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#RESTService" );
    
    public static final OntClass RScript = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#RScript" );
    
    /** <p>A SOAP service is typically described in a WSDL 1.1 or WSDL 2.0 TODO: Do we 
     *  need to distinguish between WSDL and SOAP? WSDL 1.1 and WSDL 2.0? RPC/Encoded 
     *  vs.Wrapped Document/Literal?</p>
     */
    public static final OntClass SOAPService = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#SOAPService" );
    
    public static final OntClass Script = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#Script" );
    
    /** <p>WebServiceProcess is a wfdesc:Process description, the enactment of which 
     *  gives rise to a web service call.</p>
     */
    public static final OntClass WebService = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#WebService" );
    
    /** <p>A workflow research object is a research object that contains at least one 
     *  workflow description.</p>
     */
    public static final OntClass WorkflowResearchObject = M_MODEL.createClass( "http://purl.org/wf4ever/wf4ever#WorkflowResearchObject" );
    
}