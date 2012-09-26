package org.purl.wf4ever.wfdesc.scufl2;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class WfdescAgent {
	public static final URI DEFAULT_BASE = URI.create("http://sandbox.wf4ever-project.org/rosrs5/");

	Client client = Client.create();
	ObjectMapper mapper = new ObjectMapper();

	private WebResource base;
	
	public WfdescAgent() {
		base = client.resource(DEFAULT_BASE);
	}
	
	public WfdescAgent(URI baseURI) {
		base = client.resource(baseURI);
	}

	public static void main(String[] args) throws Exception {
		URI baseURI;
		if (args.length > 0) {
			baseURI = URI.create(args[0]);
		} else {
			baseURI = DEFAULT_BASE;
		}
		
		new WfdescAgent(baseURI).annotateT2flows();
		
		
		
	}
	
	public void annotateT2flows() throws JsonParseException, JsonMappingException, IOException {
		String query = "PREFIX ro: <http://purl.org/wf4ever/ro#> " +
				"" +
				" SELECT ?ro ?p ?s ?name WHERE { " +
				"?ro a ?x ;" +
				"    ?p ?s ." +
				"?s ro:name ?name ." +
				" FILTER REGEX(?name, \"t2flow$\") } ";
		
		for (JsonNode binding : sparql(query)) {
			System.out.print( binding.path("ro").path("value").asText());
			System.out.print( binding.path("p").path("value").asText());
			System.out.print( binding.path("s").path("value").asText());
			System.out.println( binding.path("name").path("value").asText());

		}
		
		
	}

	public JsonNode sparql(String query) throws JsonParseException, JsonMappingException, IOException {
		WebResource sparql = base.path("sparql");
		String json = sparql.queryParam("query", query).accept("application/sparql-results+json", "application/json").type("application/json").get(String.class);
		System.out.println(json);
		JsonNode jsonNode = mapper.readValue(json, JsonNode.class);
		return jsonNode.path("results").path("bindings");		
	}
	
}
