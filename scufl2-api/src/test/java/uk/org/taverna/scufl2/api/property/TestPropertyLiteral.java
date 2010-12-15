package uk.org.taverna.scufl2.api.property;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class TestPropertyLiteral {

	@Test
	public void constructEmpty() {
		PropertyLiteral literal = new PropertyLiteral();
		assertEquals("", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_STRING, literal.getLiteralType());
	}

	@Test
	public void constructFromBoolean() {
		PropertyLiteral literal = new PropertyLiteral(true);
		assertEquals("true", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_BOOLEAN, literal.getLiteralType());
	}

	@Test
	public void constructFromCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
		Calendar cal = Calendar.getInstance(timeZone, Locale.FRENCH);
		// Note that Calendar.MONTH is 0-based!
		cal.set(2010, 11, 15, 14, 47, 35);
		cal.set(Calendar.MILLISECOND, 123);
		PropertyLiteral literal = new PropertyLiteral(cal);
		assertEquals("2010-12-15T14:47:35.123+01:00", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DATETIME, literal.getLiteralType());
	}

	@Test
	public void constructFromDouble() {
		PropertyLiteral literal = new PropertyLiteral(Math.PI);
		assertEquals("3.141592653589793", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DOUBLE, literal.getLiteralType());
	}

	@Test
	public void constructFromDoubleInf() {
		PropertyLiteral literal = new PropertyLiteral(Double.NEGATIVE_INFINITY);
		assertEquals("-INF", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DOUBLE, literal.getLiteralType());
	}

	@Test
	public void constructFromDoubleLarge() {
		PropertyLiteral literal = new PropertyLiteral(Double.MAX_VALUE);
		assertEquals("1.7976931348623157E308", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DOUBLE, literal.getLiteralType());
	}

	@Test
	public void constructFromDoubleNaN() {
		PropertyLiteral literal = new PropertyLiteral(Double.NaN);
		assertEquals("NaN", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DOUBLE, literal.getLiteralType());
	}


	@Test
	public void constructFromDoubleNegInf() {
		PropertyLiteral literal = new PropertyLiteral(Double.NEGATIVE_INFINITY);
		assertEquals("-INF", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_DOUBLE, literal.getLiteralType());
	}

	@Test
	public void constructFromInt() {
		PropertyLiteral literal = new PropertyLiteral(-15);
		assertEquals("-15", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_INT, literal.getLiteralType());
	}

	@Test(expected = NullPointerException.class)
	public void constructFromNull() {
		new PropertyLiteral((String) null);
	}

	@Test
	public void constructFromString() {
		PropertyLiteral literal = new PropertyLiteral("Hello there");
		assertEquals("Hello there", literal.getLiteralValue());
		assertEquals(PropertyLiteral.XSD_STRING, literal.getLiteralType());
	}

	public void getAsBoolean() {
		PropertyLiteral literal = new PropertyLiteral("false",
				PropertyLiteral.XSD_BOOLEAN);
		assertFalse(literal.getLiteralValueAsBoolean());
	}

	public void getAsBoolean1() {
		PropertyLiteral literal = new PropertyLiteral("1",
				PropertyLiteral.XSD_BOOLEAN);
		assertTrue(literal.getLiteralValueAsBoolean());
	}

	public void getAsCalendar() {
		TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
		Calendar cal = Calendar.getInstance(timeZone, Locale.FRENCH);
		// Note that Calendar.MONTH is 0-based!
		cal.set(2010, 11, 15, 14, 47, 35);
		cal.set(Calendar.MILLISECOND, 123);


		PropertyLiteral literal = new PropertyLiteral("2010-12-15T14:47:35.123+01:00",
				PropertyLiteral.XSD_DATETIME);

		assertEquals(cal, literal.getLiteralValueAsCalendar());
	}


	public void getAsDouble() {
		PropertyLiteral literal = new PropertyLiteral("3.14",
				PropertyLiteral.XSD_DOUBLE);
		assertEquals(3.14, literal.getLiteralValueAsDouble(), 0.0001);
	}

	public void getAsDoubleINF() {
		PropertyLiteral literal = new PropertyLiteral("inf",
				PropertyLiteral.XSD_DOUBLE);
		assertEquals(Double.POSITIVE_INFINITY,
				literal.getLiteralValueAsDouble());
	}

	public void getAsDoubleLarge() {
		PropertyLiteral literal = new PropertyLiteral("3.14e142",
				PropertyLiteral.XSD_DOUBLE);
		assertEquals(3.14e142, literal.getLiteralValueAsDouble(), 1e140);
	}


	public void getAsDoubleNaN() {
		PropertyLiteral literal = new PropertyLiteral("NaN",
				PropertyLiteral.XSD_DOUBLE);
		assertEquals(Double.NaN, literal.getLiteralValueAsDouble());
	}

	public void getAsDoubleNegINF() {
		PropertyLiteral literal = new PropertyLiteral("-inf",
				PropertyLiteral.XSD_DOUBLE);
		assertEquals(Double.NEGATIVE_INFINITY,
				literal.getLiteralValueAsDouble());
	}

	public void getAsInt() {
		PropertyLiteral literal = new PropertyLiteral("1337",
				PropertyLiteral.XSD_INT);
		assertEquals(1337, literal.getLiteralValueAsInt());
	}

}
