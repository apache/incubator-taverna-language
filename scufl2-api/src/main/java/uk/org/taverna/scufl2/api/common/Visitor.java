package uk.org.taverna.scufl2.api.common;


/**
 * See http://c2.com/cgi/wiki?HierarchicalVisitorPattern
 *
 * @author Stian Soiland-Reyes
 *
 */
public interface Visitor {
	public boolean visit(WorkflowBean node);

	boolean visitEnter(WorkflowBean node); // going into a branch

	boolean visitLeave(WorkflowBean node); // coming out
}