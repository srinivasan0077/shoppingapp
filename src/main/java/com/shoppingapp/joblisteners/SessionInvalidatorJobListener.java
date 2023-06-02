package com.shoppingapp.joblisteners;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class SessionInvalidatorJobListener implements JobListener {

	public String getName() {
		// TODO Auto-generated method stub
		return SessionInvalidatorJobListener.class.getName();
	}

	public void jobToBeExecuted(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	public void jobExecutionVetoed(JobExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
		
		System.out.println("After Job Execution");
		Scheduler scheduler=context.getScheduler();
		if(context.getNextFireTime()==null) {
			try {
				scheduler.deleteJob(context.getJobDetail().getKey());
				scheduler.unscheduleJob(context.getTrigger().getKey());
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		
	}

}
