package br.ufrj.ppgi.greco.dataTransformation.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufrj.ppgi.greco.util.config.ConfigResources;
import br.ufrj.ppgi.greco.util.config.ConfigTransformation;
import br.ufrj.ppgi.greco.util.pkg.PackageDataCapture;
import br.ufrj.ppgi.greco.util.pkg.PackageDataTransformed;
import br.ufrj.ppgi.greco.util.queue.QueueDataCapture;
import br.ufrj.ppgi.greco.util.queue.QueueDataTransformed;
import br.ufrj.ppgi.greco.util.rdf.CommonProperties;

/**
 * Job para o serviço de transformação de dados
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class DataTransformationJob implements Job
{
	
	/**
	 *Job da tarefa de transformação de dados
	 *@param context Contexto de execução do Job.
	 *@throws JobExecutionException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		JobDataMap dm = context.getJobDetail().getJobDataMap();
		HashMap<String, ArrayList<ConfigTransformation>> transfConfigs = (HashMap<String, ArrayList<ConfigTransformation>>) dm.get("transfParameters");
		HashMap<String, ConfigResources> configResources = (HashMap<String, ConfigResources>) dm.get("resourcesConfig");

		ArrayList<PackageDataCapture> queue = QueueDataCapture.getQueue();

		while (queue.size() > 0)
		{
			PackageDataCapture dpc = queue.get(0);
			queue.remove(0);
			ArrayList<ConfigTransformation> ct = transfConfigs.get(dpc.getService());
			transform(ct, dpc.getData(), configResources);
		}
	}

	/**
	 *Executa a transformação de dados propriamente dita
	 *@param transfConfs conjunto de índices que serão aplicados aos dados
	 *@param data dado recebido de um serviço ou WebHook
	 *@param configResources conjunto de transformações de dados a serem aplicadas
	 */
	private void transform(ArrayList<ConfigTransformation> transfConfs , String data , HashMap<String, ConfigResources> configResources)
	{

		ArrayList<PackageDataTransformed> queueDTF = QueueDataTransformed.getQueue();

		//XMLTransformation xmlTrans = new XMLTransformation();
		CSVTransformation objectTrans = new CSVTransformation();
		
		for (ConfigTransformation conf : transfConfs)
		{
			// getting resouce object
			ConfigResources confRes = configResources.get(conf.getResourceURL());
			
			if(objectTrans instanceof CSVTransformation) {
				objectTrans.setSeparatorChar(confRes.getSeparatorChar());
				objectTrans.setWithHeader(confRes.getWithHeader());
			}
			
			HashMap<String, String> transformationsIndexedByLabel = conf.getTransformation();
			ArrayList<HashMap<String, ArrayList<String>>> result = objectTrans.getValue(data, conf.getGroup(), transformationsIndexedByLabel);


			for (HashMap<String, ArrayList<String>> transformed : result)
			{
				String uriResource = confRes.getUri();
				if (confRes.getType().equals("dynamic"))
				{
					uriResource = uriResource + System.nanoTime();

					// saving the resource
					PackageDataTransformed resource = new PackageDataTransformed();
					resource.insertSPO(uriResource, CommonProperties.RDFTYPE, confRes.getRdfType());
					queueDTF.add(resource);

					// for each static literal from this dynamic Resource
					for (String staticLit : confRes.getStaticLiterals())
					{
						String[] params = staticLit.split(confRes.getSeparator());
						String property = params[0];
						String value = params[1];

						PackageDataTransformed pdt = new PackageDataTransformed();
						pdt.insertSPL(uriResource, property, value);
						queueDTF.add(pdt);

						// DataRDFDriver.insertTriplesSPL(uriResource, property,
						// value);
					}

					// for each object property from this dynamic Resource
					for (String objectProperty : confRes.getObjectProperties())
					{
						String params[] = objectProperty.split(confRes.getSeparator());
						String type = params[0];
						String objProperty = params[1];
						String uri = params[2];

						if (type.equals("dominant"))
						{
							PackageDataTransformed pdt = new PackageDataTransformed();
							pdt.insertSPO(uriResource, objProperty, uri);
							queueDTF.add(pdt);

						}
						else if(type.equals("nondominant"))
						{
							PackageDataTransformed pdt = new PackageDataTransformed();
							pdt.insertSPO(uri, objProperty, uriResource);
							queueDTF.add(pdt);
						}
					}
				}

				for (String pred : transformed.keySet())
				{
					for (String transformedResult : transformed.get(pred))
					{
						PackageDataTransformed pdt = new PackageDataTransformed();

						if (conf.isUnique())
							pdt.setUnique(true);
						else
							pdt.setUnique(false);

						pdt.insertSPL(uriResource, pred, transformedResult);
						queueDTF.add(pdt);
						// DataRDFDriver.insertTriplesSPL(uriResource, pred,
						// transformedResult);
					}
				}
			}

		}

	}
}
