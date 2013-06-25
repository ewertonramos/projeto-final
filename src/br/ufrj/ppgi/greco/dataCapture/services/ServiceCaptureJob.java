package br.ufrj.ppgi.greco.dataCapture.services;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufrj.ppgi.greco.util.pkg.PackageDataCapture;
import br.ufrj.ppgi.greco.util.queue.QueueDataCapture;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * Job para o serviço de captura de dados de serviços
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ServiceCaptureJob implements Job
{
	
	/**
	 *Job da tarefa de captura de dados de serviços
	 *@param context Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
	
		JobDataMap dm = context.getJobDetail().getJobDataMap();
		
		ArrayList<PackageDataCapture> queue = QueueDataCapture.getQueue();
		
		String serviceURL = dm.getString("url");
		
		
		String apiKey ="";
		if(dm.containsKey("apikey"))
			apiKey = dm.getString("apikey");
		
		Client client = Client.create();

		// building resource to get feeds in COSM
		WebResource webResource = client.resource(serviceURL);

		// setting api key
		Builder b = webResource.getRequestBuilder();
		b.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		
		if(apiKey != null && apiKey != "")
			b.header("X-ApiKey",apiKey);

		// getting data
		String response = b.get(String.class);
		
		//building queue package
		PackageDataCapture pdc = new PackageDataCapture();
		pdc.setService(serviceURL);
		pdc.setData(response);
		java.util.Date date= new java.util.Date();
		pdc.setDateTime(   (new Timestamp(date.getTime())).toString());
		
		//System.out.println("Response: "+response);
		//adding a package in the queue
		queue.add(pdc);
	}

}
