package br.ufrj.ppgi.greco.dataRDFStore;

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
 * Scheduler para o serviço de armazenamento de dados RDF no repositório de triplas
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class RDFStoreScheduler
{
	private static Scheduler sched;
	
	/**
	 *Inicia o serviço de armazenamento de dados em RDF no repositório de triplas
	 */
	public static void run() 
	{
		try
		{
			SchedulerFactory schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();
			
			sched.start();
			
			JobDetail job = JobBuilder.newJob(RDFStoreJob.class).withIdentity("job-rdfstore", "group-rdfstore").build();
			
				
				
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-rdfstore", "group-rdfstore")
								.withSchedule(CronScheduleBuilder.cronSchedule("0/1" + " * * * * ?"))
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
	 *Pára o serviço de armazenamento de dados em RDF no repositório de triplas
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
