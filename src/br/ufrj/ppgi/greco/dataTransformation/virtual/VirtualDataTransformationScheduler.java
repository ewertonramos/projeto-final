package br.ufrj.ppgi.greco.dataTransformation.virtual;

import java.util.HashMap;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import br.ufrj.ppgi.greco.util.config.ConfigVirtualTransformation;

/**
 * Scheduler para o serviço de geração de dados virtuais
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class VirtualDataTransformationScheduler
{
	public static Scheduler sched;

	

	/**
	 *Inicia o serviço de geração de dados virtuais
	 *@param transf HashMap com as configurações de geração de dados virtuais
	 */
	public static void run(HashMap<String,ConfigVirtualTransformation> transf) 
	{
		try
		{
			SchedulerFactory schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();

			sched.start();
			
			for(String key : transf.keySet())
			{	
				ConfigVirtualTransformation cvt = transf.get(key);
				
				JobDetail job = JobBuilder.newJob(VirtualDataTransformationJob.class)
										  .withIdentity("job-virtual-transformation"+cvt.getParentUri(), "group-virtual-transformation"+cvt.getParentUri())
										  .build();
				
				job.getJobDataMap().put("config",cvt);
				
				Trigger trigger = TriggerBuilder.newTrigger()
												.withIdentity("trigger-virtual-transformation"+cvt.getParentUri(), "group-virtual-transformation"+cvt.getParentUri())
												.withSchedule(CronScheduleBuilder.cronSchedule("0/"+cvt.getRefreshRate()+" * * * * ?")) //esse tempo � par�metro
												.build();
	
				sched.scheduleJob(job, trigger);
			}
			
		} 
		catch (Exception e)
		{
			System.out.println("erro");
			e.printStackTrace();
		}
	}
	
	/**
	 *Pára o serviço de geração de dados virtuais
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
