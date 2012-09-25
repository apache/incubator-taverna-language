package uk.org.taverna.scufl2.api.container;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage.ResourceEntry;

/**
 * @author Alan R Williams
 * 
 */
public class WorkflowBundle extends AbstractNamed implements WorkflowBean,
		Named, Root {

	public static final URI WORKFLOW_BUNDLE_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflowBundle/");

	public static URI generateIdentifier() {
		return WORKFLOW_BUNDLE_ROOT.resolve(UUID.randomUUID().toString() + "/");
	}

	private URI globalBaseURI = generateIdentifier();
	private final NamedSet<Profile> profiles = new NamedSet<Profile>();
	private final NamedSet<Workflow> workflows = new NamedSet<Workflow>();
	private Workflow mainWorkflow;
	private Profile mainProfile;
	private UCFPackage resources;

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getWorkflows());
			children.add(getProfiles());
			outer: for (Iterable<? extends WorkflowBean> it : children) {
				for (WorkflowBean bean : it) {
					if (!bean.accept(visitor)) {
						break outer;
					}
				}
			}
		}
		return visitor.visitLeave(this);
	}

	public Profile getMainProfile() {
		return mainProfile;
	}

	public Workflow getMainWorkflow() {
		return mainWorkflow;
	}

	public NamedSet<Profile> getProfiles() {
		return profiles;
	}

	public UCFPackage getResources() {
		if (resources == null) {
			try {
				resources = new UCFPackage();
			} catch (IOException e) {
				throw new IllegalStateException(
						"Can't create new UCF package, no access to tmpdir?", e);
			}
		}
		return resources;
	}

	@Override
	public URI getGlobalBaseURI() {
		return globalBaseURI;
	}

	public NamedSet<Workflow> getWorkflows() {
		return workflows;
	}

	public void setMainProfile(Profile mainProfile) {
		if (mainProfile != null) {
			getProfiles().add(mainProfile);
		}
		this.mainProfile = mainProfile;
	}

	public void setMainWorkflow(Workflow mainWorkflow) {
		if (mainWorkflow != null) {
			getWorkflows().add(mainWorkflow);
		}
		this.mainWorkflow = mainWorkflow;
	}

	public void setProfiles(Set<Profile> profiles) {
		this.profiles.clear();
		this.profiles.addAll(profiles);
	}

	public void setResources(UCFPackage resources) {
		this.resources = resources;
	}

	/**
	 * Return the folder for annotation resources.
	 * <p>
	 * This folder name can be used with
	 * getResources().listResources(folderPath) to retrieve the annotation
	 * resources, or used with getResources().addResource(..) for adding
	 * annotation resources.
	 * <p>
	 * The annotation folder is normally fixed to be <code>"annotations/"</code>.
	 * 
	 * @return Folder name for annotations
	 */
	public String getAnnotationResourcesFolder() {
		return "annotations/";
	}
	

	@Override
	public void setGlobalBaseURI(URI globalBaseURI) {
		this.globalBaseURI = globalBaseURI;
	}

	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows.clear();
		this.workflows.addAll(workflows);
	}

	@Override
	public String toString() {
		final int maxLen = 6;
		return "TavernaResearchObject [" + "profiles="
				+ (profiles != null ? toString(profiles, maxLen) : null)
				+ ", mainWorkflow=" + mainWorkflow + "]";
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext()
				&& i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}
