/**
 * Scufl2 Workflow language API
 * <p>
 * A {@link org.apache.taverna.scufl2.api.container.WorkflowBundle} is
 * the main entry point for description of a workflow. It contains
 * several {@link org.apache.taverna.scufl2.api.core.Workflow Workflows}, one
 * of which is the
 * {@link org.apache.taverna.scufl2.api.container.WorkflowBundle#getMainWorkflow()}.
 * A workflow is configured for execution in a
 * {@link org.apache.taverna.scufl2.api.profiles.Profile Profile}, one of which is
 * the {@link org.apache.taverna.scufl2.api.container.WorkflowBundle#getMainProfile()}.
 * <p>
 * You can load and save WorkflowBundle instances using 
 * {@link org.apache.taverna.scufl2.api.io.WorkflowBundleIO} as long as the 
 * implementations from the modules scufl2-t2flow and scufl2-rdfxml are
 * discoverable the classpath or available as OSGi services, alternatively
 * implementations of {@link org.apache.taverna.scufl2.api.io.WorkflowBundleReader}
 * and {@link org.apache.taverna.scufl2.api.io.WorkflowBundleWriter} can be used independently.
 * <p>
 * Also see <a href="http://dev.mygrid.org.uk/wiki/display/developer/SCUFL2+API">SCUFL 2 API in myGrid wiki</a> and 
 * the <a href="https://github.com/myGrid/scufl2">scufl2 github projecT</a>.
 *  
 *  @author Stian Soiland-Reyes
 *  @author Alan Williams
 *  @author David Withers
 *  @author Paolo Missier
 */
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


