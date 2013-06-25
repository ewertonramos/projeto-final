package br.ufrj.ppgi.greco.entityReconciliation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.openrdf.model.Value;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;



/**
 * Job para o serviço de reconciliação de dados
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class EntityReconciliationJob implements Job
{

	/**
	 *Executa a tarefa de reconciliação de dados
	 *@param context Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		//getting all resources
		String query = " select distinct ?resource "+
					   " where{ "+
					   		"?resource <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?c. "+
					   		"?resource ?prop ?d."+
					   		"filter( ?prop !=  <http://www.w3.org/2000/01/rdf-schema#subClassOf>)."+
					   "} ORDER BY ?resource ";
		
		 LinkedList<HashMap<String, Value>> resources = TripleStoreDriver.runSPARQL(query);

		//for each resource orderly by the timestamps seek and destroy equals resources
		Iterator<HashMap<String, Value>> iterator = resources.iterator();
		while(iterator.hasNext())
		{
			String uriResource = iterator.next().get("resource").stringValue();
									
			String queryNumberOfAttribs = "select ( count(distinct *) as ?count) "+
										  "where {"+  
										  "<"+uriResource+"> ?p  ?o. " +
										  "filter(?p != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>)"+
										  "}";
			
			String queryCandidatesResources = "select  ?res2 (count(distinct ?o) as ?count) "+
										  "where{ "+
										  "<"+uriResource+">  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "+
										  "?res2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "+
										  "<"+uriResource+">  ?p ?o. "+
										  "?res2 ?p ?o. "+
										  "filter ( <"+uriResource+"> != ?res2 && isLiteral(?o) && ?p != <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) "+
										  "}group by  ?res2";


			
			
			
			int numberOfTriples = Integer.parseInt(TripleStoreDriver.sparqlExecGetLiteral(queryNumberOfAttribs));

			
			//processing each candidate to reconsolidation
			LinkedList<HashMap<String, Value>> repeatedResources = TripleStoreDriver.runSPARQL(queryCandidatesResources);			
			Iterator<HashMap<String, Value>> repeated = repeatedResources.iterator();
			
			while(repeated.hasNext())
			{	
				
				HashMap<String,Value> res = repeated.next();
				
				int count = Integer.parseInt(res.get("count").stringValue());
				if(count == 0)
					break;
				
				String rec = res.get("res2").stringValue();
				
				//se o num de triplas do resource avaliado � igual ao n�mero de triplas do resource candidato a ser o mesmo
				if(count == numberOfTriples)
				{	
					System.out.println("DELETING: "+ rec);
					TripleStoreDriver.deleteAllByUri(rec);
				}	
			}
			
		}	
		
	}
	
}
