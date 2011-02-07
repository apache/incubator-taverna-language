package uk.org.taverna.scufl2.rdfxml;

import java.net.URI;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.org.taverna.scufl2.api.common.Visitor.VisitorAdapter;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.property.PropertyList;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyReference;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.api.property.PropertyResource.PropertyVisit;

public class PropertyResourceSerialiser extends VisitorAdapter {

	private Stack<Element> elementStack = new Stack<Element>();
	private final List<Object> elements;
	private DocumentBuilder docBuilder;
	private Document doc;

	public PropertyResourceSerialiser(List<Object> elements, URI baseUri) {
		this.elements = elements;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			docBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException("Can't create DocumentBuilder", e);
		}
		doc = docBuilder.newDocument();

	}

	@Override
	public boolean visitEnter(WorkflowBean node) {
		process(node);
		return true;
	}

	@Override
	public boolean visit(WorkflowBean node) {
		process(node);
		return visitLeave(node);
	}

	public void process(WorkflowBean node) {
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
	}

	@Override
	public boolean visitLeave(WorkflowBean node) {
		if (elementStack.isEmpty()) {
			return true;
		}
		elementStack.pop();
		return true;
	}

	private void property(PropertyVisit node) {
		QName qname = uriToQName(node.getPredicateUri());
		Element element = doc.createElementNS(qname.getNamespaceURI(),
				qname.getLocalPart());
		elements.add(element);
		elementStack.push(element);
	}

	private QName uriToQName(URI uri) {
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

	private void reference(PropertyReference node) {
		// TODO Auto-generated method stub

	}

	private void resource(PropertyResource node) {
		// TODO Auto-generated method stub

	}

	private void literal(PropertyLiteral node) {
		Element element = elementStack.peek();

		if (node.getLiteralType().equals(PropertyLiteral.XML_LITERAL)) {
			Element nodeElement = node.getLiteralValueAsElement();
			// TODO: Copy element..
			element.appendChild(doc.importNode(nodeElement, true));
			element.setAttributeNS(
					"http://www.w3.org/1999/02/22-rdf-syntax-ns#", "parseType",
			"Literal");
		} else {
			element.setTextContent(node.getLiteralValue());
			if (!node.getLiteralType().equals(PropertyLiteral.XSD_STRING)) {
				element.setAttributeNS(
						"http://www.w3.org/1999/02/22-rdf-syntax-ns#",
						"datatype", node.getLiteralType().toASCIIString());
			}
		}
	}

	private void list(PropertyList node) {		

	}

}
