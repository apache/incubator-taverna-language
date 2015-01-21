package org.apache.taverna.scufl2.api;

/*
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
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.apache.taverna.scufl2.api.iterationstrategy.CrossProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.DotProduct;
import org.apache.taverna.scufl2.api.iterationstrategy.IterationStrategyStack;
import org.apache.taverna.scufl2.api.iterationstrategy.PortNode;
import org.junit.Test;


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

