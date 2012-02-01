package uk.org.taverna.scufl2.usecases;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.common.Scufl2Tools;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.ReaderException;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.property.PropertyException;
import uk.org.taverna.scufl2.api.property.PropertyResource;

public class ProcessorNames {

	public static void main(String[] args) throws JAXBException, IOException,
			ReaderException {
		WorkflowBundleIO io = new WorkflowBundleIO();
		ProcessorNames processorNames = new ProcessorNames();
		for (String filename : args) {
			WorkflowBundle ro = io.readBundle(new File(filename),
					"application/vnd.taverna.t2flow+xml");
			System.out.print(filename + ": ");
			System.out.println(processorNames.showProcessorNames(ro));
			System.out.println(processorNames.showProcessorTree(ro));
		}
	}
	private Scufl2Tools scufl2Tools = new Scufl2Tools();

	private URITools uriTools = new URITools();

	private Workflow findNestedWorkflow(Processor processor) {
		URI NESTED_WORKFLOW = URI
				.create("http://ns.taverna.org.uk/2010/activity/nested-workflow");

		WorkflowBundle bundle = processor.getParent().getParent();
		// Look for nested workflows
		Profile mainProfile = bundle.getMainProfile();
		Configuration activityConfig = scufl2Tools
				.configurationForActivityBoundToProcessor(processor,
						mainProfile);
		if (activityConfig != null
				&& activityConfig.getConfigurableType().equals(
						NESTED_WORKFLOW.resolve("#Config"))) {
			PropertyResource props = activityConfig.getPropertyResource();
			try {
				URI nestedWfRel = props
						.getPropertyAsResourceURI(NESTED_WORKFLOW
								.resolve("#workflow"));
				URI nestedWf = uriTools.uriForBean(mainProfile).resolve(
						nestedWfRel);
				Workflow wf = (Workflow) uriTools.resolveUri(nestedWf, bundle);
				return wf;
			} catch (PropertyException ex) {
			}
		}
		return null;
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