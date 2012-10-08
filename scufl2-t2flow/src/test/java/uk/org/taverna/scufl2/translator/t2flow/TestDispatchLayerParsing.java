package uk.org.taverna.scufl2.translator.t2flow;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.dispatchstack.DispatchStackLayer;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.translator.t2flow.defaultdispatchstack.RetryParser.Defaults;

public class TestDispatchLayerParsing {

	private static final String WF_DISPATCH_LAYERS = "/dispatchlayers.t2flow";
	private static Scufl2Tools scufl2Tools = new Scufl2Tools();
	private T2FlowParser parser;
	private WorkflowBundle wfBundle;
	private Profile profile;
	private Workflow workflow;
	private NamedSet<Processor> processors;
	
	private URI DISPATCH_LAYER = URI.create("http://ns.taverna.org.uk/2010/scufl2/taverna/dispatchlayer/");
	
	@Before
	public void readWorkflow() throws Exception {		
		parser = new T2FlowParser();
		parser.setValidating(true);
		URL wfResource = getClass().getResource(WF_DISPATCH_LAYERS);
		assertNotNull("Could not find workflow " + WF_DISPATCH_LAYERS,
				wfResource);		
		// parser.setStrict(true);
		wfBundle = parser
				.parseT2Flow(wfResource.openStream());
		profile = wfBundle.getMainProfile();
		workflow = wfBundle.getMainWorkflow();
		processors = workflow.getProcessors();
	}
	

	@Test
	public void whichLayers() throws Exception {
		Processor parallelise = processors.getByName("retries");
		URI RETRY = DISPATCH_LAYER.resolve("Retry");
		// As inspected in /scufl2-t2flow/src/test/resources/dispatchlayers-xsd.t2flow
		List<String> expectedNames = Arrays.asList("Parallelize", "ErrorBounce", "Failover", "Retry", "Invoke");
		List<String> foundNames = new ArrayList<String>();
		
		for (DispatchStackLayer layer : parallelise.getDispatchStack()) {
			URI type = layer.getConfigurableType();
			foundNames.add(DISPATCH_LAYER.relativize(type).toASCIIString());
		}		
		assertEquals(expectedNames, foundNames);
	}
	
	@Test
	public void retriesDefault() throws Exception {
		Processor parallelise = processors.getByName("parallelise");
		URI RETRY = DISPATCH_LAYER.resolve("Retry");
		DispatchStackLayer retry = scufl2Tools.dispatchStackByType(parallelise, RETRY);
		Configuration retryConfig = scufl2Tools.configurationFor(retry, profile);
		assertEquals(RETRY.resolve("#Config"), retryConfig.getType());
		assertTrue(retryConfig.getPropertyResource().getProperties().isEmpty());				
	}
	
	@Test
	public void retriesDefaultFromT1() throws Exception {
		Processor alternates = processors.getByName("alternates");
		URI RETRY = DISPATCH_LAYER.resolve("Retry");
		DispatchStackLayer retry = scufl2Tools.dispatchStackByType(alternates, RETRY);
		Configuration retryConfig = scufl2Tools.configurationFor(retry, profile);
		assertEquals(RETRY.resolve("#Config"), retryConfig.getType());
		assertTrue(retryConfig.getPropertyResource().getProperties().isEmpty());				
	}
	
	
	@Test
	public void parallelizeDefault() throws Exception {
		Processor retry = processors.getByName("retries");
		URI PARALLELIZE = DISPATCH_LAYER.resolve("Parallelize");
		DispatchStackLayer parallelize = scufl2Tools.dispatchStackByType(retry, PARALLELIZE);
		Configuration parallelizeConfig = scufl2Tools.configurationFor(parallelize, profile);
		assertEquals(PARALLELIZE.resolve("#Config"), parallelizeConfig.getType());
		assertTrue(parallelizeConfig.getPropertyResource().getProperties().isEmpty());				
	}
	
	@Test
	public void errorBounceEmpty() throws Exception {
		Processor retry = processors.getByName("retries");
		URI ERRORBOUNCE = DISPATCH_LAYER.resolve("ErrorBounce");
		DispatchStackLayer errorbounce = scufl2Tools.dispatchStackByType(retry, ERRORBOUNCE);
		assertNotNull(errorbounce);
		assertTrue(scufl2Tools.configurationsFor(errorbounce, profile).isEmpty());
	}
	

	@Test
	public void failoverEmpty() throws Exception {
		Processor retry = processors.getByName("retries");
		URI FAILOVER = DISPATCH_LAYER.resolve("Failover");
		DispatchStackLayer failover = scufl2Tools.dispatchStackByType(retry, FAILOVER);
		assertNotNull(failover);
		assertTrue(scufl2Tools.configurationsFor(failover, profile).isEmpty());
	}
	
	@Test
	public void invokeEmpty() throws Exception {
		Processor retry = processors.getByName("retries");
		URI INVOKE = DISPATCH_LAYER.resolve("Failover");
		DispatchStackLayer invoke = scufl2Tools.dispatchStackByType(retry, INVOKE);
		assertNotNull(invoke);
		assertTrue(scufl2Tools.configurationsFor(invoke, profile).isEmpty());
	}
	

	@Test
	public void parallelizeDefaultFromT1() throws Exception {
		Processor alternates = processors.getByName("alternates");
		URI PARALLELIZE = DISPATCH_LAYER.resolve("Parallelize");
		DispatchStackLayer parallelize = scufl2Tools.dispatchStackByType(alternates, PARALLELIZE);
		Configuration parallelizeConfig = scufl2Tools.configurationFor(parallelize, profile);
		assertEquals(PARALLELIZE.resolve("#Config"), parallelizeConfig.getType());
		assertTrue(parallelizeConfig.getPropertyResource().getProperties().isEmpty());				
	}
	
	@Test
	public void parallelize() throws Exception {
		Processor retry = processors.getByName("parallelise");
		URI PARALLELIZE = DISPATCH_LAYER.resolve("Parallelize");
		DispatchStackLayer parallelize = scufl2Tools.dispatchStackByType(retry, PARALLELIZE);
		Configuration parallelizeConfig = scufl2Tools.configurationFor(parallelize, profile);
		assertEquals(PARALLELIZE.resolve("#Config"), parallelizeConfig.getType());
		assertFalse(parallelizeConfig.getPropertyResource().getProperties().isEmpty());				
		assertEquals(5, parallelizeConfig.getPropertyResource().getPropertyAsLiteral(PARALLELIZE.resolve("#maxJobs")).getLiteralValueAsInt());
	}
	
	
	@Test
	public void retriesCustom() throws Exception {
		Processor retries = processors.getByName("retries_custom");
		URI RETRY = DISPATCH_LAYER.resolve("Retry");
		DispatchStackLayer retry = scufl2Tools.dispatchStackByType(retries, RETRY);
		Configuration retryConfig = scufl2Tools.configurationFor(retry, profile);
		assertEquals(RETRY.resolve("#Config"), retryConfig.getType());

		assertTrue(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#maxRetries")));
		assertTrue(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#initialDelay")));
		assertTrue(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#maxDelay")));
		assertTrue(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#backoffFactor")));

		assertEquals(5, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#maxRetries")).getLiteralValueAsInt());
		assertEquals(1337, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#initialDelay")).getLiteralValueAsInt());
		assertEquals(7000, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#maxDelay")).getLiteralValueAsInt());
		assertEquals(1.13, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#backoffFactor")).getLiteralValueAsDouble(), 0.001);
		
	}
	

	@Test
	public void retries() throws Exception {
		Processor retries = processors.getByName("retries");
		URI RETRY = DISPATCH_LAYER.resolve("Retry");
		DispatchStackLayer retry = scufl2Tools.dispatchStackByType(retries, RETRY);
		Configuration retryConfig = scufl2Tools.configurationFor(retry, profile);
		assertEquals(RETRY.resolve("#Config"), retryConfig.getType());
		assertEquals(3, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#maxRetries")).getLiteralValueAsInt());

		assertFalse(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#initialDelay")));
		assertFalse(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#maxDelay")));
		assertFalse(retryConfig.getPropertyResource().hasProperty(RETRY.resolve("#backoffFactor")));

//		assertEquals(1000, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#initialDelay")).getLiteralValueAsInt());
//		assertEquals(5000, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#maxDelay")).getLiteralValueAsInt());
//		assertEquals(1.0, retryConfig.getPropertyResource().getPropertyAsLiteral(RETRY.resolve("#backoffFactor")).getLiteralValueAsDouble(), 0.001);
		
	}

	
}

