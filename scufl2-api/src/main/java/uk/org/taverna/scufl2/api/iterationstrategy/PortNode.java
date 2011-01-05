package uk.org.taverna.scufl2.api.iterationstrategy;

import java.util.List;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.port.InputProcessorPort;

public class PortNode implements IterationStrategyNode,
Child<IterationStrategyParent> {
	private InputProcessorPort inputProcessorPort;

	private IterationStrategyParent parent;

	public InputProcessorPort getInputProcessorPort() {
		return inputProcessorPort;
	}

	@Override
	public IterationStrategyParent getParent() {
		return parent;
	}

	public void PortNode() {
	}

	public void PortNode(InputProcessorPort inputProcessorPort) {
		setInputProcessorPort(inputProcessorPort);
	}

	public void setInputProcessorPort(InputProcessorPort inputProcessorPort) {
		this.inputProcessorPort = inputProcessorPort;
	}

	@Override
	public void setParent(IterationStrategyParent newParent) {
		if (parent == newParent) {
			return;
		}

		if (parent != null) {
			// Remove from old parent
			if (!(parent instanceof DotProduct)
					&& !(parent instanceof CrossProduct)) {
				throw new IllegalArgumentException(
						"Old PortNode parent must be a DotProduct or CrossProduct: "
						+ parent);
			}
			@SuppressWarnings("unchecked")
			List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
			parentList.remove(this);
		}

		parent = newParent;

		if (!(parent instanceof DotProduct)
				&& !(parent instanceof CrossProduct)) {
			throw new IllegalArgumentException(
					"PortNode parent must be a DotProduct or CrossProduct: "
					+ parent);
		}
		@SuppressWarnings("unchecked")
		List<IterationStrategyNode> parentList = (List<IterationStrategyNode>) parent;
		if (!parentList.contains(this)) {
			parentList.add(this);
		}

	}
}
