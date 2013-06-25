package br.ufrj.ppgi.greco.bootProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import br.ufrj.ppgi.greco.util.config.ConfigResources;
import br.ufrj.ppgi.greco.util.config.ConfigServices;
import br.ufrj.ppgi.greco.util.config.ConfigTransformation;
import br.ufrj.ppgi.greco.util.config.ConfigVirtualTransformation;
import br.ufrj.ppgi.greco.util.db.UtilDatabase;
import br.ufrj.ppgi.greco.util.rdf.CommonProperties;
import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;


/**
 *Inicializador do servidor a partir dos parâmetros
 *no Banco de Dados
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class BootInitializerDB
{

	private HashMap<String, ConfigResources> resourcesConfig;
	private HashMap<String, ConfigServices> servicesConfig;
	private HashMap<String, ArrayList<ConfigTransformation>> transformationsConfig;
	private HashMap<String, ConfigVirtualTransformation> virtualTransf;

	
	/**
     *Inicia as configurações de boot a partir das definições do banco de dados.
     */
	public BootInitializerDB()
	{
		this.resourcesConfig = new HashMap<String, ConfigResources>();
		this.servicesConfig = new HashMap<String, ConfigServices>();
		this.transformationsConfig = new HashMap<String, ArrayList<ConfigTransformation>>();
		this.virtualTransf = new HashMap<String, ConfigVirtualTransformation>();
	}

	
	/**
	 *Faz a leitura das configurações do banco de dados
	 *@throws SQLException
	 */
	private void loadConfigParameters() throws SQLException
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}  

		// getting services
		String queryServices = "select id,'regular' as 'type',service as 'url',updateRate as 'refresh-rate', returnType as 'return-type' from Service";
		ResultSet rs = UtilDatabase.executeQuery(queryServices);

		while (rs.next())
		{
			ConfigServices serviceConf = new ConfigServices();

			serviceConf.setId(rs.getString("id"));
			serviceConf.setType(rs.getString("type"));
			serviceConf.setService(rs.getString("url"));
			serviceConf.setRefreshRate(rs.getString("refresh-rate"));
			serviceConf.setReturnType(rs.getString("return-type"));
			this.servicesConfig.put(serviceConf.getType() + serviceConf.getId(), serviceConf);
		}

		// getting hooks
		String queryHooks = "select id,'webhook' as 'type',url,'' as 'refresh-rate', returnType as 'return-type' from WebHook";
		ResultSet rh = UtilDatabase.executeQuery(queryHooks);

		while (rh.next())
		{
			ConfigServices serviceConf = new ConfigServices();

			serviceConf.setId(rh.getString("id"));
			serviceConf.setType(rh.getString("type"));
			serviceConf.setService(rh.getString("url"));
			serviceConf.setRefreshRate(rh.getString("refresh-rate"));
			serviceConf.setReturnType(rh.getString("return-type"));
			this.servicesConfig.put(serviceConf.getType() + serviceConf.getId(), serviceConf);
		}

		
		String queryResources = "select id,IF(isStatic = 1,'static','dynamic') as 'type',rdfType as 'rdf-type',uri  from Resource";
		String queryStaticProperty = "select sdp.value as 'value', sdp.parentProperty as 'parent-property'  from StaticDataProperty sdp where sdp.fkResource = ";
		String queryObjectProperty = "select r.uri as 'resource',op.predicate as 'predicate', op.objectUri as 'object-uri', if(op.isDominant = 0, 'nondominant','dominant') as 'type' from Resource r, ObjectProperty op where op.fkResource = r.id and r.id = ";
		
		String queryVirtualDataProperty = "select  vdp.id as 'id', vdp.refreshRate as 'virtual-refresh-rate',  vdp.parentProperty as 'parent-property', vdp.formula as 'formula' from VirtualDataProperty vdp where vdp.fkResource = ";
		String queryFormulaParameters = "select fp.paramLabel as 'formula-param', fp.paramQuery as 'parameter' from FormulaParameter fp where fp.fkVirtualDataProperty = ";
		String queryVirtualConditional = "select c.conditionRestriction as 'case', c.value as 'content' from Conditional c where c.fkVirtualDataProperty = ";
		
		String queryDynamicDataProperty = "select distinct if(isUnique = 1, 'true','false') as isUnique, sourceType, xPathGroup as 'group', if(ddp.sourceType = 'service',s.id, wh.id) as 'source-id', if(ddp.sourceType = 'service',s.returnType, wh.returnType) as 'return-type', if(ddp.sourceType = 'service',s.service, wh.url) as 'url' from   DynamicDataProperty ddp  left join Service s ON ddp.fkService = s.id left join WebHook wh ON ddp.fkWebHook = wh.id where  ddp.fkResource = ";
		String queryDynamicLiteralService = "select distinct xPath, parentProperty from   DynamicDataProperty ddp where  ddp.fkResource = %d and ddp.fkService = %d  and ddp.xPathGroup = '%s' ";
		String queryDynamicLiteralHook ="select distinct xPath, parentProperty from   DynamicDataProperty ddp where  ddp.fkResource = %d and ddp.fkWebHook = %d  and ddp.xPathGroup = '%s' ";
		
		
		// getting information about resources
		ResultSet rr = UtilDatabase.executeQuery(queryResources);
		while (rr.next())
		{
			ConfigResources resourceConf = new ConfigResources();
			
			String resourceId = rr.getString("id");
			
			resourceConf.setType(rr.getString("type"));
			resourceConf.setRdfType(rr.getString("rdf-type"));
			resourceConf.setUri(rr.getString("uri"));
			
			//getting static data properties
			ResultSet rsp = UtilDatabase.executeQuery(queryStaticProperty+resourceId);
			while(rsp.next())
			{
				resourceConf.insertStaticResources(rsp.getString("parent-property"), rsp.getString("value") );
			}
			
			
			//getting object properties
			ResultSet ropd = UtilDatabase.executeQuery(queryObjectProperty+resourceId);
			while(ropd.next())
			{
				resourceConf.insertObjectProperties(ropd.getString("type"), ropd.getString("predicate"), ropd.getString("object-uri"));
			}	
			
				
			
			//getting virtual data properties
			ResultSet rvdp = UtilDatabase.executeQuery(queryVirtualDataProperty+resourceId);
			while(rvdp.next())
			{
				ConfigVirtualTransformation cvt = new ConfigVirtualTransformation();

				String idVirtualDP = rvdp.getString("id");
				cvt.setParentUri(resourceConf.getUri());
				cvt.setParentProperty(rvdp.getString("parent-property"));
				cvt.setFormula(rvdp.getString("formula"));
				cvt.setRefreshRate(rvdp.getString("virtual-refresh-rate"));
				
				
				//reading formula parameters
				ResultSet rfp = UtilDatabase.executeQuery(queryFormulaParameters+idVirtualDP);
				HashMap<String,String> params = new HashMap<String, String>();
				while(rfp.next())
				{
					params.put(rfp.getString("formula-param"), rfp.getString("parameter"));
				}	
				cvt.setFormulaParameters(params);

				//getting virtual conditions
				ResultSet rvc = UtilDatabase.executeQuery(queryVirtualConditional+idVirtualDP);
				HashMap<String,String> cond = new HashMap<String, String>();
				while(rvc.next())
				{
					cond.put(rvc.getString("case"), rvc.getString("content"));
				}
				
				if(cond.size() > 0)
				{
					cvt.setConditional(true);
					cvt.setConditions(cond);
				}
				else
				{
					cvt.setConditional(false);
				}
				
				this.virtualTransf.put(cvt.getParentUri(), cvt);
			}
			
			
			
			//getting dynamic data properties
			ResultSet rdp = UtilDatabase.executeQuery(queryDynamicDataProperty+resourceId);
			while(rdp.next())
			{
				ConfigTransformation confTrans = new ConfigTransformation();
				
				confTrans.setFormatType(rdp.getString("return-type"));
				confTrans.setGroup(rdp.getString("group"));
				confTrans.setService(rdp.getString("url"));
				confTrans.setUnique(rdp.getString("isUnique").equals("true"));
 				confTrans.setResourceURL(resourceConf.getUri());
 				
 				//--------reading each transformation grouped by xpath Group-----/
				String sourceType = rdp.getString("sourceType");
				Object[] queryArgs = {Integer.parseInt(resourceId),Integer.parseInt(rdp.getString("source-id")),confTrans.getGroup()};
				ResultSet transfs = null;
				if(sourceType.equals("service") )
				{
					
					transfs = UtilDatabase.executeQuery(queryDynamicLiteralService,queryArgs); 
				}else if(sourceType.equals("hook"))
				{
					transfs = UtilDatabase.executeQuery(queryDynamicLiteralHook,queryArgs);
				}
				
				while(transfs.next())
				{
					confTrans.insertTransformation(transfs.getString("parentProperty"), transfs.getString("xPath"));	
				}
				
				if (transformationsConfig.containsKey(confTrans.getService()))
				{
					ArrayList<ConfigTransformation> trans = this.transformationsConfig.get(confTrans.getService());
					trans.add(confTrans);
				} 
				else
				{
					ArrayList<ConfigTransformation> trans = new ArrayList<ConfigTransformation>();
					trans.add(confTrans);
					this.transformationsConfig.put(confTrans.getService(), trans);
				}
			}	
			
			System.out.println(resourceConf);
			System.out.println("-----------------");
			
			this.resourcesConfig.put(resourceConf.getUri(), resourceConf);
			
		}
		

	}

	/**
	 *Faz a carga dos recursos dinâmcios no repositório de triplas
	 */
	private void loadStaticResources()
	{
		for (String key : resourcesConfig.keySet())
		{
			ConfigResources confRes = resourcesConfig.get(key);

			if (confRes.getType().equals("static"))
			{
				TripleStoreDriver.insertTriplesSPO(confRes.getUri(), CommonProperties.RDFTYPE, confRes.getRdfType());

				for (String staticLit : confRes.getStaticLiterals())
				{
					String[] params = staticLit.split(confRes.getSeparator());

					String property = params[0];
					String value = params[1];
					TripleStoreDriver.insertTriplesSPL(confRes.getUri(), property, value);

				}

				for (String objectProperty : confRes.getObjectProperties())
				{
					String params[] = objectProperty.split(confRes.getSeparator());

					String type = params[0];
					String objProperty = params[1];
					String uri = params[2];

					if (type.equals("dominant"))
					{
						TripleStoreDriver.insertTriplesSPO(confRes.getUri(), objProperty, uri);

					} else
					{
						TripleStoreDriver.insertTriplesSPO(uri, objProperty, confRes.getUri());
					}
				}
			}
		}
	}

	/**
	 *Executa a carga dos parâmetros de configuração
	 */
	public void loadDatabaseConfig()
	{
		try
		{
			this.loadConfigParameters();// loading parameter from config
		} catch (Exception e)
		{
			System.out.println("Impossible to read database ");
			e.printStackTrace();
		}
		
		this.loadStaticResources(); // loading static triples into repository
	}

	/**
     *Retorna um HashMap com as configurações dos recursos indexadas por suas URIs
     *@return a HashMap<String,ConfigResources>
     */
	public HashMap<String, ConfigResources> getResourcesConfig()
	{
		return resourcesConfig;
	}

	/**
     *Retorna um HashMap com as configurações dos serviços REST indexadas por suas URIs
     *@return a HashMap<String,ConfigServices>
     */
	public HashMap<String, ConfigServices> getServicesConfig()
	{
		return servicesConfig;
	}

	/**
	 *Retorna um HashMap com as configurações dos transformações de dados indexadas pelas URIs dos serviços
	 *@return a HashMap<String,ConfigServices>
	 */
	public HashMap<String, ArrayList<ConfigTransformation>> getTransformationsConfig()
	{
		return transformationsConfig;
	}

	
	/**
	 *Retorna um HashMap com as configurações dos transformações virtuais indexadas pelas URIs dos serviços
	 *@return a HashMap<String,ConfigServices>
	 */
	public HashMap<String, ConfigVirtualTransformation> getVirtualTransformationsConfig()
	{
		return this.virtualTransf;
	}

}
