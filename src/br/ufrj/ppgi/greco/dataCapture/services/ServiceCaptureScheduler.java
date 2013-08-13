package br.ufrj.ppgi.greco.dataCapture.services;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufrj.ppgi.greco.util.config.ConfigServices;

/**
 * Scheduler para o serviço de captura de dados de serviços REST
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ServiceCaptureScheduler
{
	private static Scheduler sched;
	static final Logger log = LoggerFactory.getLogger(ServiceCaptureScheduler.class);
	/**
	 *Inicia o serviço de captura de dados de sensores
	 *@param params HashMap com as configurações dos serviços REST a serem consumidos.
	 */
	public static void run(HashMap<String, ConfigServices> params) {// params <serviceuri,  timestamp>
		try {
			SchedulerFactory schedFact = new StdSchedulerFactory();
			sched = schedFact.getScheduler();

			sched.start();

			for (String key : params.keySet()) {
				ConfigServices serviceConf = params.get(key);

				JobDetail job = JobBuilder.newJob(ServiceCaptureJob.class).withIdentity("job-" + serviceConf.getId(), "group-" + serviceConf.getId()).build();
				log.info("Service ID: {}, Type: {}\n", serviceConf.getId(), serviceConf.getType());
				
				if (!serviceConf.getType().equals("webhook")) {
					// setting parameters to job
					job.getJobDataMap().put("url", serviceConf.getService());
					job.getJobDataMap().put("apikey", "eOPKg7QxTQwnj_8PLyMK97sw9X2SAKx0NUM4SzdsTEhncz0g");

					String intervalSeconds = serviceConf.getRefreshRate();
					// "0/" + intervalSeconds + " * * * * ?"
					Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger-" + serviceConf.getId(), "group-" + serviceConf.getId()).withSchedule(CronScheduleBuilder.cronSchedule(intervalSeconds)).build();
					sched.scheduleJob(job, trigger);
				}
			}
		} catch (Exception e) {
			log.error("Problem.",e);
		}
	}
	
	/**
	 *Pára o serviço de captura de dados de sensores
	 */
	public static void stop() {
		try {
			sched.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

}
