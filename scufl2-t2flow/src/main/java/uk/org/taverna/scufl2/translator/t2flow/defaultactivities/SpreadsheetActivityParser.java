package uk.org.taverna.scufl2.translator.t2flow.defaultactivities;

import java.net.URI;
import java.util.List;

import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.property.PropertyLiteral;
import uk.org.taverna.scufl2.api.property.PropertyResource;
import uk.org.taverna.scufl2.translator.t2flow.ParserState;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetColumnNameEntry;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetExcludes;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetImportConfig;
import uk.org.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetRange;

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

		PropertyResource configResource = configuration.getPropertyResource();
		configResource.setTypeURI(ACTIVITY_URI.resolve("#Config"));

		addRange(config.getColumnRange(), configResource, ACTIVITY_URI.resolve("#columnRange"));

		addRange(config.getRowRange(), configResource, ACTIVITY_URI.resolve("#rowRange"));

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#emptyCellValue"),
				config.getEmptyCellValue());

		for (SpreadsheetColumnNameEntry entry : config.getColumnNames().getEntry()) {
			PropertyResource mappingResource = configResource.addPropertyAsNewResource(
					ACTIVITY_URI.resolve("#columnNames"), MAPPING_URI);
			mappingResource.addPropertyAsString(MAPPING_URI.resolve("#column"), entry.getString()
					.get(0));
			mappingResource.addPropertyAsString(MAPPING_URI.resolve("#port"), entry.getString()
					.get(1));

		}

		configResource.addProperty(ACTIVITY_URI.resolve("#allRows"),
				new PropertyLiteral(config.isAllRows()));

		configResource.addProperty(ACTIVITY_URI.resolve("#excludeFirstRow"), new PropertyLiteral(
				config.isExcludeFirstRow()));

		configResource.addProperty(ACTIVITY_URI.resolve("#ignoreBlankRows"), new PropertyLiteral(
				config.isIgnoreBlankRows()));

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#emptyCellPolicy"), config
				.getEmptyCellPolicy().value());

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#outputFormat"), config
				.getOutputFormat().value());

		configResource.addPropertyAsString(ACTIVITY_URI.resolve("#csvDelimiter"),
				config.getCsvDelimiter());

		return configuration;
	}

	private void addRange(SpreadsheetRange range, PropertyResource resource, URI uri) {
		PropertyResource rangeResource = resource.addPropertyAsNewResource(uri, RANGE_URI);
		rangeResource.addProperty(RANGE_URI.resolve("#start"), new PropertyLiteral(range.getStart()
				.intValue()));
		rangeResource.addProperty(RANGE_URI.resolve("#end"), new PropertyLiteral(range.getEnd()
				.intValue()));
		for (SpreadsheetRange excludesRange : range.getExcludes().getExclude()) {
			addRange(excludesRange, rangeResource, RANGE_URI.resolve("#excludes"));
		}

	}
}
