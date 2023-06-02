package com.shoppingapp.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerImpl implements SchedulerInterface {

	private Scheduler scheduler;
	
	public SchedulerImpl() throws SchedulerException {
		scheduler= new StdSchedulerFactory().getScheduler();
	}
	
	public Scheduler getScheduler() {
		return scheduler;
	}
	
	public void startScheduler() throws SchedulerException{
		scheduler.start();
	}

	public void destroyScheduler() throws SchedulerException {
		scheduler.shutdown(true);
	}

}
