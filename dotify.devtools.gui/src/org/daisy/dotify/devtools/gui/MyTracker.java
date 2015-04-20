package org.daisy.dotify.devtools.gui;

import java.util.HashSet;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class MyTracker<T> extends ServiceTracker {
	private final HashSet<T> list;
	
	public MyTracker(BundleContext context, String name) {
		super(context, name, null);
		this.list = new HashSet<T>();
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		@SuppressWarnings("unchecked")
		T f = (T) context.getService(reference);
		list.add(f);
		return super.addingService(reference);
	}
	
	public T get() {
		if (list.size()>0) {
			return list.iterator().next();
		} else {
			return null;
		}
	}
	
	public int size() {
		return list.size();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removedService(ServiceReference reference, Object service) {
		list.remove((T) service);
		super.removedService(reference, service);
	}

}
