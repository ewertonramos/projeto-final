package br.ufrj.ppgi.greco.dataRDFStore;

import java.util.ArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufrj.ppgi.greco.util.Timer;
import br.ufrj.ppgi.greco.util.pkg.PackageDataTransformed;
import br.ufrj.ppgi.greco.util.queue.QueueDataTransformed;
import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;


/**
 * Job para o serviço de captura de dados de serviços
 * 
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class RDFStoreJob implements Job {
	final Logger log = LoggerFactory.getLogger(RDFStoreJob.class);
	
	/**
	 *Job da tarefa de armazenamento dados em RDF no repositório de triplas
	 *@param arg0  Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		ArrayList<PackageDataTransformed> queueDT = QueueDataTransformed.getQueue();
		int count = 0;
		int alive = 0;
		Timer t = new Timer();
		while (queueDT.size() > 0) {
			PackageDataTransformed pdt = queueDT.get(0);
			if(pdt == null) {
				continue;
			}
			queueDT.remove(0);

			if ("literal".equals(pdt.getType())) {
				if (pdt.isUnique()) {
					TripleStoreDriver.insertUniqueTriplesSPL(pdt.getSubject(), pdt.getPredicate(), pdt.getLiteral(), false);
					++count;
				} else {
					TripleStoreDriver.insertTriplesSPL(pdt.getSubject(), pdt.getPredicate(), pdt.getLiteral(), false);
					++count;
				}
			} else {
				TripleStoreDriver.insertTriplesSPO(pdt.getSubject(), pdt.getPredicate(), pdt.getObject(), false);
				++count;
			}
			++alive;
			if(alive >= 10000) {
				log.info("Commit: {} Tempo: {}ms", count, t.getTime());
				TripleStoreDriver.commit();
				alive = 0;
			}
		}
		 /*
		  */
		 if(count > 0) {
			TripleStoreDriver.commit();
			log.info("Finalizada inserção dos recursos dinâmicos. Inseridos: {} Tempo: {}ms", count, t.getTime());
		 }
	}
}