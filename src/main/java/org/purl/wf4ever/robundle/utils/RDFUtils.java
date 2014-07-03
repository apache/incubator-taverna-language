package org.purl.wf4ever.robundle.utils;

import java.nio.file.attribute.FileTime;
import java.util.logging.Logger;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class RDFUtils {

	private static Logger logger = Logger.getLogger(RDFUtils.class
			.getCanonicalName());

	public static FileTime literalAsFileTime(RDFNode rdfNode) {
		if (rdfNode == null) {
			return null;
		}
		if (!rdfNode.isLiteral()) {
			logger.warning("Expected literal. not " + rdfNode);
			return null;
		}
		Literal literal = rdfNode.asLiteral();
		Object value = literal.getValue();
		XSDDateTime dateTime;
		if (value instanceof XSDDateTime) {
			dateTime = (XSDDateTime) value;
		} else { 
			logger.info("Literal not an XSDDateTime, but: "
					+ value.getClass() + " " + value);
			
			// Try to parse it anyway
			try { 
				dateTime = (XSDDateTime) XSDDatatype.XSDdateTime.parse(literal.getLexicalForm());			
			} catch (DatatypeFormatException e) {
				logger.warning("Invalid datetime: " + literal);
				return null;
			}
		}
		long millis = dateTime.asCalendar().getTimeInMillis();
		return FileTime.fromMillis(millis);
	}
}
