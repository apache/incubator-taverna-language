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


import java.net.URI;
import java.util.GregorianCalendar;

import org.apache.taverna.scufl2.api.common.WorkflowBean;


/**
 * A WorkflowBean that is revisioned.
 * <p>
 * Revisions are expressed as a chain of {@link Revision}s linking to the
 * {@link Revision#getPreviousRevision()}s. The Revision metadata also may
 * include when and who did the revision.
 * 
 * @author Stian Soiland-Reyes
 */
public interface Revisioned extends WorkflowBean {
	/**
	 * Get the current Revision metadata.
	 * <p>
	 * The {@link Revision} typically contains information about when it was
	 * made, and links to the previous revision chain.
	 * 
	 * @return
	 */
	Revision getCurrentRevision();

	/**
	 * Set the current Revision.
	 * <p>
	 * To preserve the existing revision chain, the new revision should point to
	 * the current revision using {@link Revision#setPreviousRevision(Revision)}
	 * 
	 * @param currentRevision
	 *            The {@link Revision} to be set
	 */
	void setCurrentRevision(Revision currentRevision);

	/**
	 * Make a new Revision to mark structural changes to this workflow bean.
	 * <p>
	 * The identifier of the new {@link #getCurrentRevision()} will also be
	 * identifying the Revisioned workflow bean and be returned from
	 * {@link #getIdentifier()}.
	 * <p>
	 * The new revision will include the previous Revision as
	 * {@link Revision#getPreviousRevision()} and
	 * {@link Revision#getGeneratedAtTime()} on the new revision will match the
	 * current {@link GregorianCalendar} by default.
	 * 
	 * @return The new {@link #getCurrentRevision()}, for setting any further
	 *         details.
	 */
	Revision newRevision();

	/**
	 * Make a new Revision to mark structural changes to this workflow bean with
	 * the given identifier.
	 * <p>
	 * {@link #getIdentifier()} will match the new identifier. The new
	 * {@link #getCurrentRevision()} will include the previous revision as
	 * {@link Revision#getPreviousRevision()}.
	 * <p>
	 * Note, unlike the convenience method {@link #newRevision()} this method
	 * will not update {@link Revision#getGeneratedAtTime()}.
	 * 
	 * @param revisionIdentifier
	 *            The new workflow identifier
	 * @return The new {@link #getCurrentRevision()}, for setting any further
	 *         details.
	 */
	Revision newRevision(URI revisionIdentifier);

	/**
	 * Set the identifier.
	 * <p>
	 * This will delete any previous revisions in {@link #getCurrentRevision()}.
	 * To avoid loosing history, you might instead want to use
	 * {@link #newRevision(URI)}.
	 * 
	 * @see #getIdentifier()
	 * @see #getCurrentRevision()
	 * 
	 * @param workflowIdentifier
	 *            the identifier
	 */
	void setIdentifier(URI workflowIdentifier);

	/**
	 * Returns the identifier of this bean.
	 * <p>
	 * This identifier matches the {@link Revision#getIdentifier()} of
	 * {@link #getCurrentRevision()}.
	 * 
	 * @see {@link #setIdentifier(URI)}
	 * @return the identifier
	 */
	URI getIdentifier();
}
