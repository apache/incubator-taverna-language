package uk.org.taverna.scufl2.api.property;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Calendar;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.org.taverna.scufl2.api.common.Visitor;

/**
 * A {@link PropertyObject} representing a literal, like a string.
 * <p>
 * A literal always has a {@link #getLiteralValue()} and a {@link #getLiteralType()}. Note that RDF
 * literal string's "language" is not yet supported.
 * <p>
 * Plain old strings should have the default {@link #setLiteralType(URI)} of {@link #XSD_STRING}.
 * For known XSD types like {@value #XSD_NONNEGATIVEINTEGER} and {@link #XSD_NORMALIZEDSTRING}, the
 * corresponding {@link #setLiteralType(URI)} should be set.
 * <p>
 * Some primitive Java types can be set directly by using constructors like
 * {@link #PropertyLiteral(double)} and {@link #PropertyLiteral(boolean)}, and retrieved using
 * methods like {@link #getLiteralValueAsDouble()} and {@link #getLiteralValueAsBoolean()}.
 * <p>
 * See {@link DatatypeConverter} to convert other XML types not covered by this class.
 * <p>
 * Values which are XML should have the type {@value #XML_LITERAL} set to avoid double-escaped serialisation.
 * 
 * @see DatatypeConverter
 * @author Stian Soiland-Reyes
 */
public class PropertyLiteral implements PropertyObject {

	/* TODO: Move these constants to separate class */

	public static URI XSD = URI.create(XMLConstants.W3C_XML_SCHEMA_NS_URI);

	public static URI XSD_STRING = XSD.resolve("#string");
	public static URI XSD_BOOLEAN = XSD.resolve("#boolean");
	public static URI XSD_DECIMAL = XSD.resolve("#decimal");
	public static URI XSD_FLOAT = XSD.resolve("#float");
	public static URI XSD_DOUBLE = XSD.resolve("#double");
	public static URI XSD_DURATION = XSD.resolve("#duration");
	public static URI XSD_DATETIME = XSD.resolve("#dateTime");
	public static URI XSD_TIME = XSD.resolve("#time");
	public static URI XSD_DATE = XSD.resolve("#date");

	public static URI XSD_YEARMONTH = XSD.resolve("#gYearMonth");
	public static URI XSD_YEAR = XSD.resolve("#gYear");
	public static URI XSD_MONTHDAY = XSD.resolve("#gMonthDay");
	public static URI XSD_DAY = XSD.resolve("#gDay");
	public static URI XSD_MONTH = XSD.resolve("#gMonth");
	public static URI XSD_HEXBINARY = XSD.resolve("#hexBinary");
	public static URI XSD_BASE64BINARY = XSD.resolve("#base64Binary");

	public static URI XSD_ANYURI = XSD.resolve("#anyURI");
	public static URI XSD_QNAME = XSD.resolve("#QName");

	public static URI XSD_NORMALIZEDSTRING = XSD.resolve("#normalizedString");
	public static URI XSD_TOKEN = XSD.resolve("#token");
	public static URI XSD_LANGUAGE = XSD.resolve("#language");
	public static URI XSD_NMTOKEN = XSD.resolve("#NMTOKEN");
	public static URI XSD_NMTOKENS = XSD.resolve("#NMTOKENS");
	public static URI XSD_NAME = XSD.resolve("#Name");
	public static URI XSD_NCNAME = XSD.resolve("#NCName");
	public static URI XSD_ID = XSD.resolve("#ID");
	public static URI XSD_IDREF = XSD.resolve("#IDREF");
	public static URI XSD_IDREFS = XSD.resolve("#IDREFS");
	public static URI XSD_ENTITY = XSD.resolve("#ENTITY");
	public static URI XSD_ENTITIES = XSD.resolve("#ENTITIES");
	public static URI XSD_INTEGER = XSD.resolve("#integer");
	public static URI XSD_NONPOSITIVEINTEGER = XSD.resolve("#nonPositiveInteger");
	public static URI XSD_NEGATIVEINTEGER = XSD.resolve("#negativeInteger");
	public static URI XSD_LONG = XSD.resolve("#long");
	public static URI XSD_INT = XSD.resolve("#int");
	public static URI XSD_SHORT = XSD.resolve("#short");
	public static URI XSD_BYTE = XSD.resolve("#byte");
	public static URI XSD_NONNEGATIVEINTEGER = XSD.resolve("#nonNegativeInteger");
	public static URI XSD_UNSIGNEDLONG = XSD.resolve("#unsignedLong");
	public static URI XSD_UNSIGNEDINT = XSD.resolve("#unsignedInt");
	public static URI XSD_UNSIGNEDSHORT = XSD.resolve("#unsignedShort");
	public static URI XSD_UNSIGNEDBYTE = XSD.resolve("#unsignedByte");
	public static URI XSD_POSITIVEINTEGER = XSD.resolve("#positiveInteger");

	public static URI XML_LITERAL = URI
	.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");

	private static DocumentBuilderFactory documentBuilderFactory;

	private static TransformerFactory transformerFactory;

	private static DocumentBuilder docBuilder;

	private static Transformer transformer;

	public static DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		}
		return documentBuilderFactory;
	}

	public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
		PropertyLiteral.documentBuilderFactory = documentBuilderFactory;
	}

	public static TransformerFactory getTransformerFactory() {
		if (transformerFactory == null) {
			transformerFactory = TransformerFactory.newInstance();
		}
		return transformerFactory;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		PropertyLiteral.transformerFactory = transformerFactory;
	}

	protected static DocumentBuilder getDocumentBuilder() {
		if (docBuilder == null) {
			synchronized (PropertyLiteral.class) {
				if (docBuilder != null) {
					return docBuilder;
				}
				DocumentBuilderFactory factory = getDocumentBuilderFactory();
				factory.setNamespaceAware(true);
				factory.setValidating(false);
				try {
					docBuilder = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e) {
					throw new IllegalStateException("Can't create DocumentBuilder", e);
				}
			}
		}
		return docBuilder;
	}

	protected static Transformer getTransformer() {
		if (transformer == null) {
			synchronized (PropertyLiteral.class) {
				if (transformer != null) {
					return transformer;
				}
				TransformerFactory tFactory = getTransformerFactory();
				try {
					transformer = tFactory.newTransformer();
				} catch (TransformerConfigurationException e1) {
					throw new IllegalStateException("Can't find transformer to write XML", e1);
				}
			}
		}
		return transformer;
	}

	private String literalValue = "";

	private URI literalType = XSD_STRING;

	/**
	 * Constructs a <code>PropertyLiteral</code> with no value or type.
	 */
	public PropertyLiteral() {
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_BOOLEAN}
	 * 
	 * @param b the literal value
	 */
	public PropertyLiteral(boolean b) {
		setLiteralValue(DatatypeConverter.printBoolean(b));
		setLiteralType(XSD_BOOLEAN);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_DATETIME}
	 * 
	 * @param cal the literal value. <strong>Must not</strong> be null
	 */
	public PropertyLiteral(Calendar cal) {
		setLiteralType(XSD_DATETIME);
		setLiteralValue(DatatypeConverter.printDateTime(cal));
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_DOUBLE}
	 * 
	 * @param d the literal value
	 */
	public PropertyLiteral(double d) {
		setLiteralValue(DatatypeConverter.printDouble(d));
		setLiteralType(XSD_DOUBLE);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XML_LITERAL}
	 * 
	 * @param element the literal value. <strong>Must not</strong> be null
	 */
	public PropertyLiteral(Element element) {
		setLiteralType(PropertyLiteral.XML_LITERAL);

		// DocumentBuilderFactory factory =
		// DocumentBuilderFactory.newInstance();
		// factory.setNamespaceAware(true);
		// Document doc;
		// try {
		// doc = factory.newDocumentBuilder().newDocument();
		// } catch (ParserConfigurationException e1) {
		// throw new IllegalStateException("Can't construct XML Document",
		// e1);
		// }
		// doc.importNode(element, true);

		Transformer transformer = getTransformer();
		CharArrayWriter writer = new CharArrayWriter();
		try {
			transformer.transform(new DOMSource(element), new StreamResult(writer));
		} catch (TransformerException e) {
			throw new IllegalStateException("Can't write XML", e);
		}
		setLiteralValue(writer.toString());
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_FLOAT}
	 * 
	 * @param f the literal value
	 */
	public PropertyLiteral(float f) {
		setLiteralValue(DatatypeConverter.printFloat(f));
		setLiteralType(XSD_FLOAT);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_INT}
	 * 
	 * @param i the literal value
	 */
	public PropertyLiteral(int i) {
		setLiteralValue(DatatypeConverter.printInt(i));
		setLiteralType(XSD_INT);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_LONG}
	 * 
	 * @param l the literal value
	 */
	public PropertyLiteral(long l) {
		setLiteralValue(DatatypeConverter.printLong(l));
		setLiteralType(XSD_LONG);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and a type of
	 * {@link #XSD_BOOLEAN}
	 * 
	 * @param value the literal value. <strong>Must not</strong> be null
	 */
	public PropertyLiteral(String value) {
		setLiteralValue(value);
	}

	/**
	 * Constructs a <code>PropertyLiteral</code> with the specified value and type.
	 * 
	 * @param value the literal value. <strong>Must not</strong> be null
	 * @param literalType the literal type. <strong>Must not</strong> be null
	 */
	public PropertyLiteral(String value, URI literalType) {
		setLiteralValue(value);
		setLiteralType(literalType);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	/**
	 * Returns the literal type.
	 * 
	 * @return the literal type
	 */
	public URI getLiteralType() {
		return literalType;
	}

	/**
	 * Returns the literal value as a <code>String</code>.
	 * 
	 * @return the literal value as a <code>String</code>
	 */
	public String getLiteralValue() {
		return literalValue;
	}

	/**
	 * Returns the literal value as a <code>boolean</code>.
	 * 
	 * @return the literal value as a <code>boolean</code>
	 */
	public boolean getLiteralValueAsBoolean() {
		return DatatypeConverter.parseBoolean(getLiteralValue());
	}

	/**
	 * Returns the literal value as a <code>Calendar</code>.
	 * 
	 * @return the literal value as a <code>Calendar</code>
	 */
	public Calendar getLiteralValueAsCalendar() {
		return DatatypeConverter.parseDateTime(getLiteralValue());
	}

	/**
	 * Returns the literal value as a <code>double</code>.
	 * 
	 * @return the literal value as a <code>double</code>
	 */
	public double getLiteralValueAsDouble() {
		return DatatypeConverter.parseDouble(getLiteralValue());
	}

	/**
	 * Returns the literal value as an XML <code>Element</code>.
	 * 
	 * @return the literal value as an XML <code>Element</code>
	 */
	public Element getLiteralValueAsElement() {
		DocumentBuilder docBuilder = getDocumentBuilder();
		StringReader reader = new StringReader(getLiteralValue());
		Document valueDoc;
		try {
			valueDoc = docBuilder.parse(new InputSource(reader));
		} catch (SAXException e) {
			throw new IllegalStateException("Can't parse literal XML:\n" + getLiteralValue(), e);
		} catch (IOException e) {
			throw new IllegalStateException("Can't parse literal XML:\n" + getLiteralValue(), e);
		}
		return valueDoc.getDocumentElement();
	}

	/**
	 * Returns the literal value as a <code>float</code>.
	 * 
	 * @return the literal value as a <code>float</code>
	 */
	public float getLiteralValueAsFloat() {
		return DatatypeConverter.parseFloat(getLiteralValue());
	}

	/**
	 * Returns the literal value as a <code>int</code>.
	 * 
	 * @return the literal value as a <code>int</code>
	 */
	public int getLiteralValueAsInt() {
		return DatatypeConverter.parseInt(getLiteralValue());
	}

	/**
	 * Returns the literal value as a <code>long</code>.
	 * 
	 * @return the literal value as a <code>long</code>
	 */
	public long getLiteralValueAsLong() {
		return DatatypeConverter.parseLong(getLiteralValue());
	}

	/**
	 * Sets the literal type.
	 * 
	 * @param literalType the literal type. <strong>Must not</strong> be null
	 */
	public void setLiteralType(URI literalType) {
		if (literalType == null) {
			throw new NullPointerException("Data value type can't be null, try PropertyLiteral");
		}
		this.literalType = literalType;
	}

	/**
	 * Sets the literal value.
	 * 
	 * @param literalValue the literal value. <strong>Must not</strong> be null
	 */
	public void setLiteralValue(String literalValue) {
		if (literalValue == null) {
			throw new NullPointerException("Value can't be null");
		}
		this.literalValue = literalValue;
	}

	@Override
	public String toString() {
		return "PropertyLiteral [getLiteralType()=" + getLiteralType() + ", getLiteralValue()="
		+ getLiteralValue() + "]";
	}

}
