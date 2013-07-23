package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetColumnNameEntry;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetImportConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetRange;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SpreadsheetActivityParser extends AbstractActivityParser {

	private static URI activityRavenURI = T2FlowParser.ravenURI
			.resolve("net.sf.taverna.t2.activities/spreadsheet-import-activity/");

	private static String activityClassName = "net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity";

	public static URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import");

	public static URI RANGE_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import/Range");

	public static URI MAPPING_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import/Mapping");

	@Override
	public boolean canHandlePlugin(URI activityURI) {
		String activityUriStr = activityURI.toASCIIString();
		return activityUriStr.startsWith(activityRavenURI.toASCIIString())
				&& activityUriStr.endsWith(activityClassName);
	}

	@Override
	public URI mapT2flowRavenIdToScufl2URI(URI t2flowActivity) {
		return ACTIVITY_URI;
	}

	@Override
	public Configuration parseConfiguration(T2FlowParser t2FlowParser, ConfigBean configBean,
			ParserState parserState) throws ReaderException {
		SpreadsheetImportConfig config = unmarshallConfig(t2FlowParser, configBean, "xstream",
				SpreadsheetImportConfig.class);

		Configuration configuration = new Configuration();
		configuration.setParent(parserState.getCurrentProfile());

		ObjectNode json = (ObjectNode) configuration.getJson();
		configuration.setType(ACTIVITY_URI.resolve("#Config"));

		ArrayNode columnRanges = json.arrayNode();
		json.put("columnRange", columnRanges);
		addRange(config.getColumnRange(), columnRanges);

		ArrayNode rowRanges = json.arrayNode();
        json.put("rowRange", rowRanges);
		addRange(config.getRowRange(), rowRanges);

		if (config.getEmptyCellValue() != null) {
		    json.put("emptyCellValue", config.getEmptyCellValue());
		}
		
		ArrayNode columnNames = json.arrayNode();
        if (config.getColumnNames() != null && config.getColumnNames().getEntry() != null) {
    		for (SpreadsheetColumnNameEntry entry : config.getColumnNames().getEntry()) {
    		    ObjectNode mapping = json.objectNode();
    		    columnNames.add(mapping);
                mapping.put("column", entry.getString().get(0));
                mapping.put("port", entry.getString().get(1));
    		}
    		if (columnNames.size() > 0) {
    		    json.put("columnNames", columnNames);
    		}
        }
		
        json.put("allRows", config.isAllRows());
        json.put("excludeFirstRow", config.isExcludeFirstRow());
        json.put("ignoreBlankRows", config.isIgnoreBlankRows());
        if (config.getEmptyCellPolicy() != null) {
            json.put("emptyCellPolicy", config.getEmptyCellPolicy().value());
        }
		if(config.getOutputFormat() != null) {
            json.put("outputFormat", config.getOutputFormat().value());
        }
        if(config.getCsvDelimiter() != null) {
            json.put("csvDelimiter", config.getCsvDelimiter());
        }

		return configuration;
	}

	private void addRange(SpreadsheetRange range, ArrayNode arrayNode) {
	    ObjectNode rangeJson = arrayNode.objectNode();
	    arrayNode.add(rangeJson);
	    
		rangeJson.put("start", range.getStart().longValue());
        rangeJson.put("end", range.getStart().longValue());
        
        ArrayNode excludes = arrayNode.arrayNode();
		for (SpreadsheetRange excludesRange : range.getExcludes().getExclude()) {
			addRange(excludesRange, excludes);
		}
		if (excludes.size() > 0) {
		    rangeJson.put("excludes", excludes);
		}
	}
}
