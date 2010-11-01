package uk.org.taverna.scufl2.usecases;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.xml.bind.JAXBException;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.configurations.Configuration;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Processor;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.api.profiles.ProcessorBinding;
import uk.org.taverna.scufl2.translator.t2flow.ParseException;
import uk.org.taverna.scufl2.translator.t2flow.T2FlowParser;

public class ProcessorNames {

	public static void main(String[] args) throws JAXBException, IOException,
			ParseException {
		T2FlowParser t2flowParser = new T2FlowParser();
		ProcessorNames processorNames = new ProcessorNames();
		for (String filename : args) {
			WorkflowBundle ro = t2flowParser.parseT2Flow(new File(
					filename));
			System.out.print(filename + ": ");
			System.out.println(processorNames.showProcessorNames(ro));
			System.out.println(processorNames.showProcessorTree(ro));
		}
	}

	public List<String> showProcessorNames(WorkflowBundle ro)
			throws JAXBException, IOException, ParseException {
		ArrayList<String> names = new ArrayList<String>();
		for (Processor processor : ro.getMainWorkflow().getProcessors()) {
			names.add(processor.getName());
		}
		Collections.sort(names);
		return names;
	}

	public String showProcessorTree(WorkflowBundle ro)
			throws JAXBException, IOException, ParseException {
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

	public TreeModel makeProcessorTree(WorkflowBundle ro)
			throws JAXBException, IOException, ParseException {
		Workflow workflow = ro.getMainWorkflow();
		TreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(
				workflow.getName()));
		DefaultMutableTreeNode parent = (DefaultMutableTreeNode) treeModel
				.getRoot();

		findProcessors(ro, workflow, parent);
		return treeModel;
	}

	private void findProcessors(WorkflowBundle ro, Workflow workflow,
			DefaultMutableTreeNode parent) {
		for (Processor processor : workflow.getProcessors()) {
			DefaultMutableTreeNode processorNode = new DefaultMutableTreeNode(
					processor.getName());
			parent.add(processorNode);
			// Look for nested workflows
			for (Profile profile : ro.getProfiles()) {
				for (ProcessorBinding pb : profile.getProcessorBindings()) {
					if (pb.getBoundProcessor().equals(processor)) {
						Activity boundActivity = pb.getBoundActivity();
						if (!boundActivity
								.getType()
								.getName()
								.equals(
										"http://ns.taverna.org.uk/2010/activity/nested-workflow")) {
							continue;
						}
						// ConfigurableProperty prop = boundActivity
						// .getConfigurableProperties()
						// .getByName(
						// "http://ns.taverna.org.uk/2010/activity/nested-workflow#workflow");
						// for (Configuration co : profile.getConfigurations())
						// {
						// if (!co.getConfigured().equals(boundActivity)) {
						// continue;
						// }
						// for (ConfigurablePropertyConfiguration propConfig :
						// co
						// .getConfigurablePropertyConfigurations()) {
						// if (!propConfig.getConfiguredProperty().equals(
						// prop)) {
						// continue;
						// }
						// String workflowId = (String) propConfig
						// .getValue();
						// Workflow wf = ro.getWorkflows().getByName(
						// workflowId);
						// findProcessors(ro, wf, processorNode);
						//
						// }
						// }
					}
				}
			}
		}
	}
}
