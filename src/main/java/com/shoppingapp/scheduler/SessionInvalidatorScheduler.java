package com.shoppingapp.scheduler;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.KeyMatcher;

import com.shoppingapp.joblisteners.SessionInvalidatorJobListener;
import com.shoppingapp.jobs.SessionInvalidatorJob;
import com.shoppingapp.utils.BeanFactoryWrapper;
import com.shoppingapp.utils.DateManipulatorUtil;


	
public class SessionInvalidatorScheduler {
    private String NAME_OF_JOB = "JOB+";  
    private String NAME_OF_GROUP = "authentication";  
	private String NAME_OF_TRIGGER = "TRIGGER+";  
	private Scheduler scheduler;
	
	public SessionInvalidatorScheduler(String email){
		NAME_OF_JOB+=email;
		NAME_OF_GROUP+=email;
		try {
		  scheduler=(Scheduler)((SchedulerInterface) BeanFactoryWrapper.getBeanFactory().getBean("scheduler")).getScheduler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean submitJob(String sessionID) {
		try {
			JobDetail jobInstance = JobBuilder
					.newJob(SessionInvalidatorJob.class)
					.withIdentity(NAME_OF_JOB, NAME_OF_GROUP)
					.usingJobData("id",sessionID)
					.build();
			 Trigger triggerNew =(SimpleTrigger) TriggerBuilder.newTrigger().withIdentity(NAME_OF_TRIGGER, NAME_OF_GROUP)  
		                .startAt(new DateManipulatorUtil().addSeconds(new Date(),30))  
		                .build();
			 scheduler.scheduleJob(jobInstance, triggerNew);
			 scheduler.getListenerManager().addJobListener(new SessionInvalidatorJobListener(),KeyMatcher.keyEquals(jobInstance.getKey()));
			 return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	
}
