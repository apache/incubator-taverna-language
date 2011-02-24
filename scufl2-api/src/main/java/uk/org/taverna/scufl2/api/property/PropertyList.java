package uk.org.taverna.scufl2.api.property;

import java.util.ArrayList;
import java.util.List;

import uk.org.taverna.scufl2.api.common.Visitor;

public class PropertyList extends ArrayList<PropertyObject> implements
PropertyObject, List<PropertyObject> {

	@Override
	public boolean accept(Visitor visitor) {
		if (visitor.visitEnter(this)) {
			for (PropertyObject po : this) {
				if (!po.accept(visitor)) {
					break;
				}
			}
		}
		return visitor.visitLeave(this);
	}

	@Override
	public String toString() {
		return "PropertyList [" + super.toString() + "]";
	}

}
