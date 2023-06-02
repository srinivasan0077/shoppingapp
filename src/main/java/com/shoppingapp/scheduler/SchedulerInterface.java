package com.shoppingapp.scheduler;


public interface SchedulerInterface {

	
	public void startScheduler() throws Exception;
	
	public void destroyScheduler() throws Exception;
	
	public Object getScheduler();
	
}
