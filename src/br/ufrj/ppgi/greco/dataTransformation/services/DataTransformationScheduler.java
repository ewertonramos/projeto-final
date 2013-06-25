package br.ufrj.ppgi.greco.dataTransformation.services;

import java.util.ArrayList;
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

import br.ufrj.ppgi.greco.util.config.ConfigResources;
import br.ufrj.ppgi.greco.util.config.ConfigTransformation;

/**
 * Scheduler para o serviço de transformação de dados provenientes dos serviços REST
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class DataTransformationScheduler
{
	
	public static Scheduler sched;

	/**
	 *Inicia o serviço de transformação de dados
	 *@param transfParameters parâmetros de configuração das transformações
	 *@param resourceConfig configuração dos recursos 
	 */
	public static void run(HashMap<String, ArrayList<ConfigTransformation>> transfParameters, HashMap<String,ConfigResources> resourceConfig ) 
	{
		try
		{
			SchedulerFactory schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();

			sched.start();
			
			

			JobDetail job = JobBuilder.newJob(DataTransformationJob.class)
									  .withIdentity("job-transformation", "group-transformation")
									  .build();
			
			job.getJobDataMap().put("transfParameters", transfParameters);
			job.getJobDataMap().put("resourcesConfig", resourceConfig);

			Trigger trigger = TriggerBuilder.newTrigger()
											.withIdentity("trigger-transformation", "group-transformation")
											.withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?"))
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
	 *Pára o serviço de transformação de dados
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
