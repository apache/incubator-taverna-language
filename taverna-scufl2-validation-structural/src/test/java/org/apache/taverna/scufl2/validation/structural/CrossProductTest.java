/**
 * 
 */
package org.apache.taverna.scufl2.validation.structural;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.apache.taverna.scufl2.api.port.InputProcessorPort;
import org.apache.taverna.scufl2.validation.structural.StructuralValidator;
import org.junit.Test;



/**
 * @author alanrw
 *
 */
public class CrossProductTest {
	
	private InputProcessorPort a;
	private InputProcessorPort b;

	private CrossProduct getCross(int depthA, int depthB) {
		a = new InputProcessorPort();
		a.setName("a");
		a.setDepth(0);
		CrossProduct cp = new CrossProduct();
		PortNode nipn1 = new PortNode(cp, a);
		nipn1.setDesiredDepth(depthA);
		
		b = new InputProcessorPort();
		b.setName("b");
		b.setDepth(0);
		PortNode nipn2 = new PortNode(cp, b);
		nipn2.setDesiredDepth(depthB);

		return cp;
	}
	
	@Test
	public void testSingletonCrossUnstagedIteration(){
		CrossProduct cp = getCross(0, 0);
		
		StructuralValidator sv = new StructuralValidator();
		Map<InputProcessorPort, Integer> tempDepths = new HashMap<InputProcessorPort, Integer>();
		tempDepths.put(a, 1);
		tempDepths.put(b, 1);

		assertEquals(Integer.valueOf(2), sv.getIterationDepth(cp,
		tempDepths));
	}
	
	@Test
	public void testListCrossUnstagedIteration() {
		CrossProduct cp = getCross(0, 0);
		StructuralValidator sv = new StructuralValidator();
		Map<InputProcessorPort, Integer> tempDepths = new HashMap<InputProcessorPort, Integer>();
		tempDepths.put(a,2);
		tempDepths.put(b,2);
		
		assertEquals(Integer.valueOf(4), sv.getIterationDepth(cp,
		tempDepths));
	}
	
	@Test
	public void testListCrossDifferentIterationsUnstagedIteration() {
		CrossProduct cp = getCross(0, 0);
		StructuralValidator sv = new StructuralValidator();
		Map<InputProcessorPort, Integer> tempDepths = new HashMap<InputProcessorPort, Integer>();
		tempDepths.put(a,1);
		tempDepths.put(b,2);
		
		assertEquals(Integer.valueOf(3), sv.getIterationDepth(cp,
		tempDepths));
		
	}
	
	@Test
	public void testDifferentDepthsCrossUnstagedIteration() {
		CrossProduct cp = getCross(0, 1);
		StructuralValidator sv = new StructuralValidator();
		Map<InputProcessorPort, Integer> tempDepths = new HashMap<InputProcessorPort, Integer>();
		tempDepths.put(a,3);
		tempDepths.put(b,4);
		
		assertEquals(Integer.valueOf(6), sv.getIterationDepth(cp,
		tempDepths));
		
	}
	
	@Test
	public void testStagedCombinationOfCross() {
		
		Processor p = new Processor();
		IterationStrategyStack iss = new IterationStrategyStack(p);

	iss.add(getCross(1, 1));
	iss.add(getCross(0, 0));
	StructuralValidator sv = new StructuralValidator();
	sv.getValidatorState().setProcessor(p);
	Map<InputProcessorPort, Integer> tempDepths = new HashMap<InputProcessorPort, Integer>();
	tempDepths.put(a,2);
	tempDepths.put(b,2);

	assertEquals(Integer.valueOf(4), sv.calculateResultWrappingDepth(tempDepths));

}


}
