package uk.org.taverna.scufl2.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import uk.org.taverna.scufl2.api.iterationstrategy.CrossProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.DotProduct;
import uk.org.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import uk.org.taverna.scufl2.api.iterationstrategy.PortNode;

public class EqualsOnArrayListsTest {
    
    @Test
    public void crossProductEqualIfEmpty() throws Exception {
        CrossProduct crossProd1 = new CrossProduct();
        CrossProduct crossProd2 = new CrossProduct();
        assertEquals(crossProd1, crossProd2);                
    }
        
    @Test
    public void crossProductNotEqual() throws Exception {
        CrossProduct crossProd1 = new CrossProduct();
        CrossProduct crossProd2 = new CrossProduct();
        crossProd2.add(new PortNode());
        assertNotEquals(crossProd1, crossProd2);                
    }
    
    
    @Test
    public void crossProductNotEqualToDotProduct() throws Exception {
        DotProduct dotProd = new DotProduct();
        CrossProduct crossProd = new CrossProduct();
        assertNotEquals(crossProd, dotProd);
        assertNotEquals(dotProd, crossProd);
    }

    @Test
    public void dotProductEqualIfEmpty() throws Exception {
        DotProduct dotProd1 = new DotProduct();
        DotProduct dotProd2 = new DotProduct();
        assertEquals(dotProd1, dotProd2);                
    }

    @Test
    public void dotProductNotEqual() throws Exception {
        DotProduct dotProd1 = new DotProduct();
        DotProduct dotProd2 = new DotProduct();
        dotProd2.add(new PortNode());
        assertNotEquals(dotProd1, dotProd2);                
    }
    
    @Test
    public void iterationStackEqualIfEmpty() throws Exception {
        IterationStrategyStack itStack1 = new IterationStrategyStack();
        IterationStrategyStack itStack2 = new IterationStrategyStack();        
        assertEquals(itStack1, itStack2);                
    }

    @Test
    public void iterationStackNotEqualToCrossProduct() throws Exception {
        IterationStrategyStack itStack = new IterationStrategyStack();
        CrossProduct crossProd = new CrossProduct();
        assertNotEquals(itStack, crossProd);                
    }    

    @Test
    public void iterationStackNotEqualToDotProduct() throws Exception {
        IterationStrategyStack itStack = new IterationStrategyStack();
        DotProduct dotProd = new DotProduct();
        assertNotEquals(itStack, dotProd);                
    }
}

