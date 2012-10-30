package uk.org.taverna.scufl2.api.annotation;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.AbstractNamedChild;
import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.URITools;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.property.PropertyObject;
import uk.org.taverna.scufl2.api.property.PropertyResource;

/**
 * An annotation of a WorkflowBean.
 * <p>
 * Modelled after http://openannotation.org/spec/core/20120509
 * 
 * @author Stian Soiland-Reyes
 *
 */
public class Annotation extends AbstractNamed implements Named, Child<WorkflowBundle> {
	private Calendar annotated;
	private URI annotator;
	private List<PropertyObject> bodyStatements = new ArrayList<PropertyObject>();
	private Calendar generated = new GregorianCalendar();
	private URI generator;
	private WorkflowBean target;
	private WorkflowBundle parent;
	private URI body;
	
	
	public Annotation(WorkflowBean target) {
		setTarget(target);
	}

	public Annotation() {
	}
	
	@Override
	public boolean accept(Visitor visitor) {
		visitor.visitEnter(this);
		for (WorkflowBean b : getBodyStatements()) {
			if (! b.accept(visitor)) {
				break;
			}
		}
		return visitor.visitLeave(this);		
	}
	public Calendar getAnnotated() {
		return annotated;
	}
	public URI getAnnotator() {
		return annotator;
	}
	public List<PropertyObject> getBodyStatements() {
		return bodyStatements;
	}
	public Calendar getGenerated() {
		return generated;
	}
	public URI getGenerator() {
		return generator;
	}
	public WorkflowBean getTarget() {
		if (target == null) {
			return this;
		}
		return target;
	}
	public void setAnnotated(Calendar annotated) {
		this.annotated = annotated;
	}
	public void setAnnotator(URI annotator) {
		this.annotator = annotator;
	}
	public void setBodyStatements(List<PropertyObject> bodyStatements) {
		if (bodyStatements == null) { 
			throw new NullPointerException("bodyStatements can't be null");
		}
		this.bodyStatements = bodyStatements;
	}

	public void setGenerated(Calendar generated) {
		this.generated = generated;
	}
	public void setGenerator(URI generator) {
		this.generator = generator;
	}
	public void setTarget(WorkflowBean target) {
		if (target == null) {
			throw new NullPointerException("Target can't be null");
		}
		this.target = target;
	}

	@Override
	public WorkflowBundle getParent() {
		return this.parent;
	}

	@Override
	public void setParent(WorkflowBundle parent) {
		if (this.parent != null && this.parent != parent) {
			this.parent.getAnnotations().remove(this);
		}
		this.parent = parent;
		if (parent != null) {
			parent.getAnnotations().add(this);
		}

	}

	public URI getBody() {
		return body;
	}

	public void setBody(URI body) {
		this.body = body;
	}

	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		Annotation cloneAnnotation = (Annotation)clone;
		if (getAnnotated() != null) {
			cloneAnnotation.setAnnotated((Calendar) getAnnotated().clone());
		}
		cloneAnnotation.setAnnotator(getAnnotator());
		cloneAnnotation.setBody(getBody());
		for (PropertyObject statement : getBodyStatements()) {
			cloneAnnotation.getBodyStatements().add(cloning.cloneIfNotInCache(statement));
		}		
		if (getGenerated() != null) {
			cloneAnnotation.setGenerated((Calendar) getGenerated().clone());
		}
		cloneAnnotation.setGenerator(getGenerator());
		cloneAnnotation.setTarget(cloning.cloneOrOriginal(getTarget()));		
	}
	
}
