package br.ufrj.ppgi.greco.entityReconciliation;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Scheduler para o serviço de reconciliação de dados
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class EntityReconciliationScheduler
{
	
	public static Scheduler sched;
	
	/**
	 *Inicia o serviço de reconciliação de dados
	 */
	public static void run() 
	{
		try
		{
			SchedulerFactory schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();

			sched.start();
			
				
				JobDetail job = JobBuilder.newJob(EntityReconciliationJob.class)
										  .withIdentity("job-entity-reconciliation", "group-entity-reconciliation")
										  .build();
				
				
				
				Trigger trigger = TriggerBuilder.newTrigger()
												.withIdentity("trigger-entity-reconciliation", "group-entiy-reconciliation")
												.withSchedule(CronScheduleBuilder.cronSchedule("0/20"+" * * * * ?")) 
												.build();
	
				sched.scheduleJob(job, trigger);
		} 
		catch (Exception e)
		{
			System.out.println("erro");
			e.printStackTrace();
		}

	}
	
	/**
	 *Pára o serviço de captura de reconciliação de dados
	 */
	public static void stop()
	{
		try
		{
			sched.shutdown();
		} 
		catch (SchedulerException e)
		{
			e.printStackTrace();
		}
	}
	
}	
