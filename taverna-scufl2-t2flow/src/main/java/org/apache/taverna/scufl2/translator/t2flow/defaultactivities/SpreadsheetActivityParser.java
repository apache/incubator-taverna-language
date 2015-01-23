package org.apache.taverna.scufl2.translator.t2flow.defaultactivities;
/*
 *
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


import static org.apache.taverna.scufl2.translator.t2flow.T2FlowParser.ravenURI;

import java.net.URI;

import org.apache.taverna.scufl2.api.configurations.Configuration;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.translator.t2flow.ParserState;
import org.apache.taverna.scufl2.translator.t2flow.T2FlowParser;

import org.apache.taverna.scufl2.xml.t2flow.jaxb.ConfigBean;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetColumnNameEntry;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetImportConfig;
import org.apache.taverna.scufl2.xml.t2flow.jaxb.SpreadsheetRange;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class SpreadsheetActivityParser extends AbstractActivityParser {
	private static final URI activityRavenURI = ravenURI
			.resolve("net.sf.taverna.t2.activities/spreadsheet-import-activity/");
	private static final String activityClassName = "net.sf.taverna.t2.activities.spreadsheet.SpreadsheetImportActivity";
	public static final URI ACTIVITY_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import");
	public static final URI RANGE_URI = URI
			.create("http://ns.taverna.org.uk/2010/activity/spreadsheet-import/Range");
	public static final URI MAPPING_URI = URI
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

		ObjectNode columnRange = json.objectNode();
		json.put("columnRange", columnRange);
		makeRange(config.getColumnRange(), columnRange);

		ObjectNode rowRange = json.objectNode();
        json.put("rowRange", rowRange);
        makeRange(config.getRowRange(), rowRange);

		if (config.getEmptyCellValue() != null)
		    json.put("emptyCellValue", config.getEmptyCellValue());
		
		ArrayNode columnNames = json.arrayNode();
        if (config.getColumnNames() != null && config.getColumnNames().getEntry() != null) {
    		for (SpreadsheetColumnNameEntry entry : config.getColumnNames().getEntry()) {
    		    ObjectNode mapping = json.objectNode();
    		    columnNames.add(mapping);
                mapping.put("column", entry.getString().get(0));
                mapping.put("port", entry.getString().get(1));
    		}
    		if (columnNames.size() > 0)
    		    json.put("columnNames", columnNames);
        }
		
		json.put("allRows", config.isAllRows());
		json.put("excludeFirstRow", config.isExcludeFirstRow());
		json.put("ignoreBlankRows", config.isIgnoreBlankRows());
		if (config.getEmptyCellPolicy() != null)
			json.put("emptyCellPolicy", config.getEmptyCellPolicy().value());
		if (config.getOutputFormat() != null)
			json.put("outputFormat", config.getOutputFormat().value());
		if (config.getCsvDelimiter() != null)
			json.put("csvDelimiter", config.getCsvDelimiter());

		return configuration;
	}

	private void makeRange(SpreadsheetRange range, ObjectNode rangeJson) {
		rangeJson.put("start", range.getStart().longValue());
        rangeJson.put("end", range.getEnd().longValue());
        
        ArrayNode excludes = rangeJson.arrayNode();
		for (SpreadsheetRange excludesRange : range.getExcludes().getExclude()) {
			ObjectNode exclude = rangeJson.objectNode();
			makeRange(excludesRange, exclude);
		    excludes.add(exclude);
		}
		if (excludes.size() > 0)
		    rangeJson.put("excludes", excludes);
	}
}
