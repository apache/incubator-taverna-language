package uk.org.taverna.scufl2.api.common;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.Visitor.VisitorWithPath;
import uk.org.taverna.scufl2.api.property.PropertyVisit;

public class AllBeansVisitor extends VisitorWithPath implements Visitor {

	private final List<WorkflowBean> allBeans = new ArrayList<WorkflowBean>();
	
	@Override
	public boolean visit() {
		if (! (getCurrentNode() instanceof PropertyVisit)) {
			getAllBeans().add(getCurrentNode());
		}
		return true;
	}

	public List<WorkflowBean> getAllBeans() {
		return allBeans;
	}
	
	public static List<WorkflowBean> allBeansFrom(WorkflowBean bean) {
		AllBeansVisitor visitor = new AllBeansVisitor();
		bean.accept(visitor);
		return visitor.getAllBeans();
	}

	

}
