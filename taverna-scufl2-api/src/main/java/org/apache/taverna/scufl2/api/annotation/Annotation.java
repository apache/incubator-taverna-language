package org.apache.taverna.scufl2.api.annotation;

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


import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.taverna.scufl2.api.common.AbstractNamed;
import org.apache.taverna.scufl2.api.common.Child;
import org.apache.taverna.scufl2.api.common.Named;
import org.apache.taverna.scufl2.api.common.Visitor;
import org.apache.taverna.scufl2.api.common.WorkflowBean;
import org.apache.taverna.scufl2.api.container.WorkflowBundle;


/**
 * An annotation of a WorkflowBean.
 * <p>
 * Modelled after http://openannotation.org/spec/core/20120509
 * 
 * @author Stian Soiland-Reyes
 */
public class Annotation extends AbstractNamed implements Named,
		Child<WorkflowBundle> {
	private Calendar annotatedAt;
	private URI annotatedBy;
	private Calendar serializedAt = new GregorianCalendar();
	private URI serializedBy;
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
		return visitor.visit(this);
	}

	public Calendar getAnnotatedAt() {
		return annotatedAt;
	}

	public URI getAnnotatedBy() {
		return annotatedBy;
	}

	public Calendar getSerializedAt() {
		return serializedAt;
	}

	public URI getSerializedBy() {
		return serializedBy;
	}

	public WorkflowBean getTarget() {
		if (target == null)
			return this;
		return target;
	}

	public void setAnnotatedAt(Calendar annotatedAt) {
		this.annotatedAt = annotatedAt;
	}

	public void setAnnotatedBy(URI annotatedBy) {
		this.annotatedBy = annotatedBy;
	}

	public void setSerializedAt(Calendar serializedAt) {
		this.serializedAt = serializedAt;
	}

	public void setSerializedBy(URI serializedBy) {
		this.serializedBy = serializedBy;
	}

	public void setTarget(WorkflowBean target) {
		if (target == null)
			throw new NullPointerException("Target can't be null");
		this.target = target;
	}

	@Override
	public WorkflowBundle getParent() {
		return this.parent;
	}

	@Override
	public void setParent(WorkflowBundle parent) {
		if (this.parent != null && this.parent != parent)
			this.parent.getAnnotations().remove(this);
		this.parent = parent;
		if (parent != null)
			parent.getAnnotations().add(this);
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
		Annotation cloneAnnotation = (Annotation) clone;
		if (getAnnotatedAt() != null)
			cloneAnnotation.setAnnotatedAt((Calendar) getAnnotatedAt().clone());
		cloneAnnotation.setAnnotatedBy(getAnnotatedBy());
		cloneAnnotation.setBody(getBody());
		if (getSerializedAt() != null)
			cloneAnnotation.setSerializedAt((Calendar) getSerializedAt()
					.clone());
		cloneAnnotation.setSerializedBy(getSerializedBy());
		cloneAnnotation.setTarget(cloning.cloneOrOriginal(getTarget()));
	}

	/**
	 * Gets the standard path for the resource in the bundle associated with
	 * this annotation. The resource will be expected to contain a TTL-encoded
	 * RDF model.
	 * 
	 * @return a resource path.
	 */
	public String getResourcePath() {
		return "annotation/" + getName() + ".ttl";
	}

	/**
	 * Gets the content of this annotation. Note that this method does not cache
	 * the value it reads.
	 * 
	 * @return a TTL-encoded RDF model.
	 * @throws IOException
	 *             If anything goes wrong reading the resource
	 */
	public String getRDFContent() throws IOException {
		return getParent().getResources()
				.getResourceAsString(getResourcePath());
	}
}
