package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.PropertyVisit;

public class PropertyResourceSerialiser extends VisitorWithPath {
	public static final String ABOUT = "about";
	public static final String DESCRIPTION = "Description";
	public static final String RESOURCE = "resource";
	public static final String LITERAL = "Literal";
	public static final String DATATYPE = "datatype";
	public static final String LI = "li";
	public static final String PARSE_TYPE = "parseType";
	public static final String COLLECTION = "Collection";
	public static final String RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	public static final String RDF_ = "rdf:";
	public static final String RDF_ELEM = RDF_ + "RDF";
	public static final String RDF_ABOUT = RDF_ + ABOUT;
	public static final String RDF_DESCRIPTION = RDF_ + DESCRIPTION;
	public static final String RDF_DATATYPE = RDF_ + DATATYPE;
	public static final String RDF_LI = RDF_ + LI;
	public static final String RDF_PARSE_TYPE = RDF_ + PARSE_TYPE;
	private static final String RDF_RESOURCE = RDF_ + RESOURCE;

	private static URITools uriTools = new URITools();
	
	protected Stack<Element> elementStack = new Stack<Element>();
	private DocumentBuilder docBuilder;
	private Document doc;
	private final URI baseUri;

	public PropertyResourceSerialiser(URI baseUri) {
		this.baseUri = baseUri;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			setDocBuilder(factory.newDocumentBuilder());
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Can't create DocumentBuilder", e);
		}
		setDoc(getDocBuilder().newDocument());

	}

	private void addElement(Element element) {
		if (elementStack.isEmpty()) {
			// Top level
			if (getRootElement() == null) {
				setRootElement(element);
			} else {
				ensureRDFRootElem();
				getRootElement().appendChild(element);
			}
		} else {
			// System.out.println("Appending to " + elementStack.peek());
			elementStack.peek().appendChild(element);
		}

		elementStack.push(element);
		printStatus(false);
	}

	public void ensureRDFRootElem() {
		Element existingRoot = getRootElement();
		if (existingRoot == null || !existingRoot.getNodeName().equals(RDF_ELEM)) {
			// Wrap existingRoot in <rdf:RDF>
			Element rootElem = doc.createElementNS(RDF, RDF_ELEM);
			setRootElement(rootElem);
			if (existingRoot != null) {
				rootElem.appendChild(existingRoot);
			}
		}
	}

	private final void printStatus(boolean isClosing) {
	};

	/*
	 * private final void printStatus(boolean isClosing) { StackTraceElement[]
	 * st = new Exception().getStackTrace(); StackTraceElement caller = st[1];
	 * 
	 * // indent by stack depth for (Element e : elementStack) {
	 * System.out.print("-"); } System.out.print(isClosing ? "<" : ">");
	 * System.out.print(" " + getCurrentNode().getClass().getSimpleName() +
	 * ": "); for (Element e : elementStack) { System.out.print("<" +
	 * e.getNodeName() + "> "); } System.out.print("\t\t-- " +
	 * caller.getMethodName() + "(..) in (" + caller.getFileName() + ":" +
	 * caller.getLineNumber() + ")"); System.out.println("\t" +
	 * getCurrentPath());
	 * 
	 * }
	 */

	public Element getRootElement() {
		return getDoc().getDocumentElement();
	}

	protected void list(PropertyList node) {
		Element element = elementStack.peek();
		element.setAttributeNS(RDF, RDF_PARSE_TYPE, COLLECTION);
	}

	protected void literal(PropertyLiteral node) {
		Element element = elementStack.peek();
		if (node.getLiteralType().equals(PropertyLiteral.XML_LITERAL)) {
			Element nodeElement = node.getLiteralValueAsElement();
			element.appendChild(getDoc().importNode(nodeElement, true));
			element.setAttributeNS(RDF, RDF_PARSE_TYPE, LITERAL);
		} else {
			element.setTextContent(node.getLiteralValue());
			if (!node.getLiteralType().equals(PropertyLiteral.XSD_STRING)) {
				element.setAttributeNS(RDF, RDF_DATATYPE, node.getLiteralType()
						.toASCIIString());
			}
		}
	}

	protected void property(PropertyVisit node) {
		// Handled by individual visits further down (as we'll need to create
		// multiple elements if there's several values of a property)
	}

	protected void reference(PropertyReference node) {
		Element element = elementStack.peek();
		element.setAttributeNS(RDF, RDF_RESOURCE, relativize(node.getResourceURI())
				.toASCIIString());
	}

	protected URI relativize(URI resourceURI) {
		if (resourceURI.toASCIIString().startsWith(WorkflowBundle.WORKFLOW_BUNDLE_ROOT.toASCIIString()) ||
				resourceURI.toASCIIString().startsWith(Workflow.WORKFLOW_ROOT.toASCIIString()) ||
				resourceURI.toASCIIString().startsWith(Profile.PROFILE_ROOT.toASCIIString())) {
			// Avoid climbing too high up
			return uriTools.relativePath(baseUri, resourceURI);
		} else	{
			return baseUri.relativize(resourceURI);
		}
		
		
	}

	protected void resource(PropertyResource node) {
		URI typeUri = node.getTypeURI();
		Element element;
		if (typeUri != null) {
			element = uriToElement(typeUri);
		} else {
			// Anonymous - give warning?
			element = getDoc().createElementNS(RDF, RDF_DESCRIPTION);
		}
		if (node.getResourceURI() != null) {
			element.setAttributeNS(RDF, RDF_ABOUT, relativize(node.getResourceURI())
					.toASCIIString());
		}
		addElement(element);
	}

	public void setRootElement(Element rootElement) {
		Element oldRoot = getRootElement();
		if (oldRoot != null) {
			doc.removeChild(oldRoot);
		}
		doc.appendChild(rootElement);
	}

	protected Element uriToElement(URI uri) {
		QName propertyQname = uriToQName(uri);
		return getDoc().createElementNS(propertyQname.getNamespaceURI(),
				propertyQname.getLocalPart());
	}

	protected QName uriToQName(URI uri) {
		String uriStr = uri.toASCIIString();
		// \\u10000-\\uEFFFF not included
		String NMTOKEN = " \\xC0-\\xD6\\xD8-\\xF6\\xF8-\\u02FF\\u0370-\\u037D\\u037F-\\u1FFF\\u200C-\\u200D\\u2070-\\u218F\\u2C00-\\u2FEF\\u3001-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFFD";
		String ncNameRegex = "[_A-Za-z" + NMTOKEN + "][-._A-Za-z0-9" + NMTOKEN
				+ "]*$";
		Pattern ncPattern = Pattern.compile(ncNameRegex);
		Matcher m = ncPattern.matcher(uriStr);
		if (!(m.find())) {
			throw new IllegalArgumentException(
					"End of URI not valid in QName: " + uri);
		}

		String ns = uriStr.substring(0, m.start());
		String name = m.group();

		m = ncPattern.matcher(ns);
		// TODO: Suggest prefix
		return new QName(ns, name);
	}

	@Override
	public boolean visit() {
		WorkflowBean node = getCurrentNode();
		if (!getCurrentPath().isEmpty()) {
			WorkflowBean parent = getCurrentPath().peek();
			if (parent instanceof PropertyVisit) {
				PropertyVisit propertyVisit = (PropertyVisit) parent;
				Element element = uriToElement(propertyVisit.getPredicateUri());
				addElement(element);
			} else if (parent instanceof PropertyList) {
				addElement(getDoc().createElementNS(RDF, RDF_LI));
			}
		}
		if (node instanceof PropertyList) {
			list((PropertyList) node);
		} else if (node instanceof PropertyLiteral) {
			literal((PropertyLiteral) node);
		} else if (node instanceof PropertyResource) {
			resource((PropertyResource) node);
		} else if (node instanceof PropertyReference) {
			reference((PropertyReference) node);
		} else if (node instanceof PropertyVisit) {
			property((PropertyVisit) node);			
		} else {
			throw new IllegalStateException("Did not expect " + node);
		}
		return true;
	}

	@Override
	public boolean visitLeave() {

		Stack<WorkflowBean> currentPath = getCurrentPath();
		if (currentPath.size() > 1
				&& currentPath.get(currentPath.size() - 2) instanceof PropertyVisit
				&& !(currentPath.get(currentPath.size() - 1) instanceof PropertyList)) {
			// TODO: This seems to work, but uncertain if it's general enough,
			// or why.
			return true;
		}
		if (getCurrentNode() instanceof PropertyResource) {
			// We need to pop the <Class> before <predicate>
			printStatus(true);
			elementStack.pop();
		}

		if (elementStack.isEmpty()) {
			// System.out.println("Stack empty! " + getCurrentNode());
			return true;
		}
		Element element;
		if (getCurrentPath().size() > 3
				&& getCurrentPath().get(getCurrentPath().size() - 3) instanceof PropertyList) {
			return true;
		}

		if (elementStack.size() > 1) {
			printStatus(true);
			elementStack.pop();
		}
		// System.out.println();
		return true;
	}

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public DocumentBuilder getDocBuilder() {
		return docBuilder;
	}

	public void setDocBuilder(DocumentBuilder docBuilder) {
		this.docBuilder = docBuilder;
	}

	public void includeBase() {
		ensureRDFRootElem();
		getRootElement().setAttribute("xml:base", baseUri.toASCIIString());
		
	}

}
