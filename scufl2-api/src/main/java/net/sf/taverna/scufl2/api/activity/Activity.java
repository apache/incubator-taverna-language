package net.sf.taverna.scufl2.api.activity;

import net.sf.taverna.scufl2.api.common.AbstractNamed;

public class Activity extends AbstractNamed {

	public Activity(String name) {
		super(name);
	}

	private ActivityType type;

	public ActivityType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Activity " + getType().getName() + " \"" + getName()
				+ '"';
	}

	public void setType(ActivityType type) {
		this.type = type;
	}

}
