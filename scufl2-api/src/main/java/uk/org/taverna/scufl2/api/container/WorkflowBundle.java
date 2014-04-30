package uk.org.taverna.scufl2.api.container;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.org.taverna.scufl2.api.activity.Activity;
import uk.org.taverna.scufl2.api.annotation.Annotation;
import uk.org.taverna.scufl2.api.annotation.Revisioned;
import uk.org.taverna.scufl2.api.common.AbstractNamed;
import uk.org.taverna.scufl2.api.common.AbstractRevisioned;
import uk.org.taverna.scufl2.api.common.Named;
import uk.org.taverna.scufl2.api.common.NamedSet;
import uk.org.taverna.scufl2.api.common.Root;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.io.WorkflowBundleIO;
import uk.org.taverna.scufl2.api.profiles.Profile;
import uk.org.taverna.scufl2.ucfpackage.UCFPackage;

/**
 * A workflow bundle is a collection of workflows, profiles and their
 * annotations.
 * <p>
 * A Workflow Bundle represents these resources as a whole, and have its own
 * identifier.
 * <p>
 * Workflows are listed under {@link #getWorkflows()}, one of which SHOULD be
 * {@link #setMainWorkflow(Workflow)}.
 * <p>
 * Similarly, profiles are listed under {@link #getProfiles()}, one of which
 * SHOULD be {@link #setMainProfile(Profile)}.
 * <p>
 * Annotations are included under {@link #getAnnotations()}, and additional
 * resources inside {@link #getResources()}.
 * 
 * @author Alan R Williams
 * @author Stian Soiland-Reyes
 * 
 */
public class WorkflowBundle extends AbstractRevisioned implements WorkflowBean,
		Named, Root, Revisioned {

	public static final String APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE = "application/vnd.taverna.scufl2.workflow-bundle";
	
	public static final URI WORKFLOW_BUNDLE_ROOT = URI
			.create("http://ns.taverna.org.uk/2010/workflowBundle/");

	public static URI generateIdentifier() {
		return WORKFLOW_BUNDLE_ROOT.resolve(UUID.randomUUID().toString() + "/");
	}

    private NamedSet<Annotation> annotations = new NamedSet<Annotation>();
	private final NamedSet<Profile> profiles = new NamedSet<Profile>();
	private final NamedSet<Workflow> workflows = new NamedSet<Workflow>();
	private Workflow mainWorkflow;
	private Profile mainProfile;
	private UCFPackage resources;


    /**
     * Construct a new WorkflowBundle with a randomly generated name.
     * <p>
     * A WorkflowBundle can also be created using
     * {@link WorkflowBundleIO#createBundle()} or loaded from an existing bundle
     * using {@link WorkflowBundleIO#readBundle(java.io.File, String)},
     * {@link WorkflowBundleIO#readBundle(java.io.InputStream, String)} or
     * {@link WorkflowBundleIO#readBundle(java.net.URL, String)}.
     */
    public WorkflowBundle() {
        super();
    }

    /**
     * Construct a new WorkflowBundle with the specified name.
     * 
     * @param name
     *            The name of the <code>WorkflowBundle</code>. <strong>Must
     *            not</strong> be <code>null</code> or an empty String.
     */
    public WorkflowBundle(String name) {
        super(name);
    }
	
	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			List<Iterable<? extends WorkflowBean>> children = new ArrayList<Iterable<? extends WorkflowBean>>();
			children.add(getWorkflows());
			children.add(getProfiles());
			children.add(getAnnotations());
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
				resources.setPackageMediaType(APPLICATION_VND_TAVERNA_SCUFL2_WORKFLOW_BUNDLE);
			} catch (IOException e) {
				throw new IllegalStateException(
						"Can't create new UCF package, no access to tmpdir?", e);
			}
		}
		return resources;
	}

	@Override
	public URI getGlobalBaseURI() {
		return getIdentifier();
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

	@Override
	public void setGlobalBaseURI(URI globalBaseURI) {
		setIdentifier(globalBaseURI);
	}

	public void setWorkflows(Set<Workflow> workflows) {
		this.workflows.clear();
		this.workflows.addAll(workflows);
	}

	/**
	 * WorkflowBundles are only equal by instance identity.
	 * <p>
	 * Thus, if you load or construct the same workflow bundle twice, say as
	 * <code>wb1</code> and <code>wb2</code>, then
	 * <code>wb1.equals(wb2) == false</code>.
	 * <p>
	 * There are two reasons for this. Firstly, a workflow bundle is a complex
	 * object, as it bundles not just the {@link #getWorkflows()} and
	 * {@link #getProfiles()}, but also arbitrary resources in
	 * {@link #getResources()}. Two workflow bundles can for most purposes be
	 * assumed "equal" if they have the same identifier in
	 * {@link #getGlobalBaseURI()} - they might however vary in which
	 * annotations they carry.
	 * <p>
	 * The second is that applications might use {@link WorkflowBundle}
	 * instances as keys in a {@link Map} of open workflows, and want to
	 * distinguish between two workflow bundles from two different (but possibly
	 * identical) files; for instance a .t2flow and a .wfbundle.
	 * <p>
	 * Note that contained workflow beans such as {@link Workflow} and
	 * {@link Activity} will likewise not be
	 * {@link AbstractNamed#equals(Object)} across workflow bundles, as a named
	 * bean is considered equal only if its name matches and its parents are
	 * (recursively) equal. You may however detach the children by setting their
	 * parents to <code>null</code> and check for equality in isolation.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		return this == obj;
	}

	@Override
	public int hashCode() {
		// TODO: Is there a way to call Object.hashCode() from here?
		// Our super is hiding it.

		// Possible workaround:
		/*
		 * private int hashCode = new Object().hashCode();
		 * 
		 * @Override public int hashCode() { return hashCode; }
		 */

		// We're overriding hashCode() as we also override equals(Object)  
		return super.hashCode();
	}

	public NamedSet<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(NamedSet<Annotation> annotations) {
		this.annotations.clear();
		this.annotations.addAll(annotations);
	}
	
	@Override
	protected void cloneInto(WorkflowBean clone, Cloning cloning) {
		super.cloneInto(clone, cloning);
		WorkflowBundle cloneBundle = cloning.getCloned(this);
		cloneBundle.setGlobalBaseURI(getGlobalBaseURI());
		cloneBundle.setMainWorkflow(cloning.cloneIfNotInCache(getMainWorkflow()));
		cloneBundle.setMainProfile(cloning.cloneIfNotInCache(getMainProfile()));
		cloneBundle.setResources(getResources().clone());
		
	}

	@Override
	protected URI getIdentifierRoot() {
		return WORKFLOW_BUNDLE_ROOT;
	}

}
