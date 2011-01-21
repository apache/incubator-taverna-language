package uk.org.taverna.scufl2.api.property;

import java.net.URI;
import java.util.Calendar;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;

import uk.org.taverna.scufl2.api.common.Visitor;

/**
 * A {@link PropertyObject} representing a literal, like a string.
 * <p>
 * A literal has always a {@link #getLiteralValue()} and a
 * {@link #getLiteralType()}. Note that RDF literal string's "language" is not
 * yet supported.
 * </p>
 * <p>
 * Plain old strings should have the default {@link #setLiteralType(URI)} of
 * {@link #XSD_STRING}. For known XSD types like
 * {@value #XSD_NONNEGATIVEINTEGER} and {@link #XSD_NORMALIZEDSTRING}, the
 * corresponding {@link #setLiteralType(URI)} should be set.
 * </p>
 * <p>
 * Some primitive Java types can be set directly by using constructors like
 * {@link #PropertyLiteral(double)} and {@link #PropertyLiteral(boolean)}, and
 * retrieved using methods like {@link #getLiteralValueAsDouble() and
 * #getLiteralValueAsBoolean().
 * </p>
 * <p><p>
 *
 *
 *
 * See {@link DatatypeConverter} to convert other XML types not covered by this
 * class.
 * </p>
 * <p>
 * Values which are XML should have the type #XML_LITERAL set to avoid
 * double-escaped serialisation.
 * </p>
 *
 * @see DatatypeConverter
 * @author Stian Soiland-Reyes
 *
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
	public static URI XSD_NONPOSITIVEINTEGER = XSD
	.resolve("#nonPositiveInteger");
	public static URI XSD_NEGATIVEINTEGER = XSD.resolve("#negativeInteger");
	public static URI XSD_LONG = XSD.resolve("#long");
	public static URI XSD_INT = XSD.resolve("#int");
	public static URI XSD_SHORT = XSD.resolve("#short");
	public static URI XSD_BYTE = XSD.resolve("#byte");
	public static URI XSD_NONNEGATIVEINTEGER = XSD
	.resolve("#nonNegativeInteger");
	public static URI XSD_UNSIGNEDLONG = XSD.resolve("#unsignedLong");
	public static URI XSD_UNSIGNEDINT = XSD.resolve("#unsignedInt");
	public static URI XSD_UNSIGNEDSHORT = XSD.resolve("#unsignedShort");
	public static URI XSD_UNSIGNEDBYTE = XSD.resolve("#unsignedByte");
	public static URI XSD_POSITIVEINTEGER = XSD.resolve("#positiveInteger");

	public static URI XML_LITERAL = URI
	.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");

	private String literalValue = "";
	private URI literalType = XSD_STRING;

	public PropertyLiteral() {
	}

	public PropertyLiteral(boolean b) {
		setLiteralValue(DatatypeConverter.printBoolean(b));
		setLiteralType(XSD_BOOLEAN);
	}

	public PropertyLiteral(Calendar cal) {
		setLiteralType(XSD_DATETIME);
		setLiteralValue(DatatypeConverter.printDateTime(cal));
	}

	public PropertyLiteral(double d) {
		setLiteralValue(DatatypeConverter.printDouble(d));
		setLiteralType(XSD_DOUBLE);
	}

	public PropertyLiteral(float f) {
		setLiteralValue(DatatypeConverter.printFloat(f));
		setLiteralType(XSD_FLOAT);
	}

	public PropertyLiteral(int i) {
		setLiteralValue(DatatypeConverter.printInt(i));
		setLiteralType(XSD_INT);
	}

	public PropertyLiteral(long l) {
		setLiteralValue(DatatypeConverter.printLong(l));
		setLiteralType(XSD_LONG);
	}

	public PropertyLiteral(String value) {
		setLiteralValue(value);
	}

	public PropertyLiteral(String value, URI literalType) {
		setLiteralValue(value);
		setLiteralType(literalType);
	}

	@Override
	public boolean accept(Visitor visitor) {
		return visitor.visit(this);
	}

	public URI getLiteralType() {
		return literalType;
	}

	public String getLiteralValue() {
		return literalValue;
	}

	public boolean getLiteralValueAsBoolean() {
		return DatatypeConverter.parseBoolean(getLiteralValue());
	}

	public Calendar getLiteralValueAsCalendar() {
		return DatatypeConverter.parseDateTime(getLiteralValue());
	}

	public double getLiteralValueAsDouble() {
		return DatatypeConverter.parseDouble(getLiteralValue());
	}

	public float getLiteralValueAsFloat() {
		return DatatypeConverter.parseFloat(getLiteralValue());
	}

	public int getLiteralValueAsInt() {
		return DatatypeConverter.parseInt(getLiteralValue());
	}

	public long getLiteralValueAsLong() {
		return DatatypeConverter.parseLong(getLiteralValue());
	}

	public void setLiteralType(URI literalType) {
		if (literalType == null) {
			throw new NullPointerException(
			"Data value type can't be null, try PropertyLiteral");
		}
		this.literalType = literalType;
	}

	public void setLiteralValue(String literalValue) {
		if (literalValue == null) {
			throw new NullPointerException("Value can't be null");
		}
		this.literalValue = literalValue;
	}

}
