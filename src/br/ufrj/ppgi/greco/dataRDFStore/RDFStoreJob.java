package br.ufrj.ppgi.greco.dataRDFStore;

import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufrj.ppgi.greco.util.pkg.PackageDataTransformed;
import br.ufrj.ppgi.greco.util.queue.QueueDataTransformed;
import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;


/**
 * Job para o serviço de captura de dados de serviços
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class RDFStoreJob implements Job
{
	/**
	 *Job da tarefa de armazenamento dados em RDF no repositório de triplas
	 *@param arg0  Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException
	{
		ArrayList<PackageDataTransformed> queueDT = QueueDataTransformed.getQueue();
		
		 while(queueDT.size() > 0 )
		 {	 
			 //getting first element in the queue
			 PackageDataTransformed pdt = queueDT.get(0);
			 queueDT.remove(0);
			 
			 if(pdt.getType().equals("literal"))
			 {
				 if(pdt.isUnique())
					 TripleStoreDriver.insertUniqueTriplesSPL(pdt.getSubject(), pdt.getPredicate(), pdt.getLiteral());
				 else
					 TripleStoreDriver.insertTriplesSPL(pdt.getSubject(), pdt.getPredicate(), pdt.getLiteral());
			 }	
			 else
				 TripleStoreDriver.insertTriplesSPO(pdt.getSubject(), pdt.getPredicate(), pdt.getObject());
			 
			 TripleStoreDriver.commit();		 
		 }
		 

		 
	}
	
}
