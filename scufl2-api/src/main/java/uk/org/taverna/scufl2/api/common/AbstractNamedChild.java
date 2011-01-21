package uk.org.taverna.scufl2.api.common;

/**
 * FIXME: Can't implement {@link Child} without introducing type parameters
 *
 * @author Alan R Williams
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractNamedChild extends AbstractNamed {

	public AbstractNamedChild() {
		super();
	}

	public AbstractNamedChild(String name) {
		super(name);
	}

}
