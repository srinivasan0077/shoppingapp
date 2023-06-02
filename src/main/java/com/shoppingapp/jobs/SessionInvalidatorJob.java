package com.shoppingapp.jobs;



import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;




public class SessionInvalidatorJob implements Job {

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		JobDataMap map=context.getJobDetail().getJobDataMap();
		String key=map.getString("id");
		
		System.out.println("Job Run By "+Thread.currentThread().getName());
		

	}

}
