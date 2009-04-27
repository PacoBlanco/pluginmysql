package es.itecban.deployment.environment.runner.tasks.database;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public BundleContext ctx;
	private static Activator act;
	
	public void start(BundleContext context) throws Exception {
		ctx = context;
		Activator.act = this;

	}

	public void stop(BundleContext context) throws Exception {
		Activator.act=null;

	}
	
	public static Activator getPlugin(){
		return Activator.act;
	}

}
