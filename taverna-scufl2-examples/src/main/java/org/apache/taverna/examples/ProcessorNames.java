package org.apache.taverna.examples;

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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.xml.bind.JAXBException;

import org.apache.taverna.scufl2.api.common.Scufl2Tools;
import org.apache.taverna.scufl2.api.common.URITools;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;
import org.apache.taverna.scufl2.api.core.Processor;
import org.apache.taverna.scufl2.api.core.Workflow;
import org.apache.taverna.scufl2.api.io.ReaderException;
import org.apache.taverna.scufl2.api.io.WorkflowBundleIO;
import org.apache.taverna.scufl2.api.profiles.Profile;

public class ProcessorNames {

	public static void main(String[] args) throws JAXBException, IOException,
			ReaderException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		ProcessorNames processorNames = new ProcessorNames();
		for (String filename : args) {
			// NOTE: We give 'null' as filetype so it will guess based on filename
			WorkflowBundle ro = io.readBundle(new File(filename), null);
//			System.out.print(filename + ": ");
//			System.out.println(processorNames.showProcessorNames(ro));
			System.out.println(processorNames.showProcessorTree(ro));
		}
	}
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	private URITools uriTools = new URITools();

	private Workflow findNestedWorkflow(Processor processor) {
		Profile profile = processor.getParent().getParent().getMainProfile();
		return scufl2Tools.nestedWorkflowForProcessor(processor, profile);
	}

	private void findProcessors(WorkflowBundle ro, Workflow workflow,
			DefaultMutableTreeNode parent) {
		for (Processor processor : workflow.getProcessors()) {
			DefaultMutableTreeNode processorNode = new DefaultMutableTreeNode(
					processor.getName());
			parent.add(processorNode);
			Workflow wf = findNestedWorkflow(processor);
			if (wf != null) {
				findProcessors(ro, wf, processorNode);
			}
		}

	}

	public TreeModel makeProcessorTree(WorkflowBundle workflowBundle)
			throws JAXBException, IOException {
		Workflow workflow = workflowBundle.getMainWorkflow();
		TreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(
				workflow.getName()));
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
				.getRoot();

		findProcessors(workflowBundle, workflow, parent);
		return treeModel;
	}

	public List<String> showProcessorNames(WorkflowBundle ro)
			throws JAXBException, IOException {
		ArrayList<String> names = new ArrayList<String>();
		for (Processor processor : ro.getMainWorkflow().getProcessors()) {
			names.add(processor.getName());
		}
		Collections.sort(names);
		return names;
	}

	public String showProcessorTree(WorkflowBundle ro) throws JAXBException,
			IOException {
		TreeModel treeModel = makeProcessorTree(ro);
		return treeModelAsString(treeModel);
	}

	public String treeModelAsString(TreeModel treeModel) {
		StringBuffer sb = new StringBuffer();
		Object root = treeModel.getRoot();
		treeModelAsString(treeModel, root, sb, "");
		return sb.toString();
	}

	protected void treeModelAsString(TreeModel treeModel, Object parent,
			StringBuffer sb, String indentation) {
		sb.append(indentation);
		int childCount = treeModel.getChildCount(parent);
		if (childCount == 0) {
			sb.append("- ");
		} else {
			sb.append("+ ");
			indentation = indentation + "  ";
		}
		sb.append(parent);
		sb.append("\n");
		for (int i = 0; i < childCount; i++) {
			Object child = treeModel.getChild(parent, i);
			treeModelAsString(treeModel, child, sb, indentation);
		}
	}
}
