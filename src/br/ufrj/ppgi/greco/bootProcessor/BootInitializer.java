package br.ufrj.ppgi.greco.bootProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.ufrj.ppgi.greco.util.config.ConfigResources;
import br.ufrj.ppgi.greco.util.config.ConfigServices;
import br.ufrj.ppgi.greco.util.config.ConfigTransformation;
import br.ufrj.ppgi.greco.util.config.ConfigVirtualTransformation;
import br.ufrj.ppgi.greco.util.rdf.CommonProperties;
import br.ufrj.ppgi.greco.util.rdf.TripleStoreDriver;

/**
 *Inicializador do servidor a partir dos parâmetros
 *em um arquivo de configuração XML
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class BootInitializer
{

	private HashMap<String, ConfigResources> resourcesConfig;
	private HashMap<String, ConfigServices> servicesConfig;
	private HashMap<String, ArrayList<ConfigTransformation>> transformationsConfig;
	private HashMap<String, ConfigVirtualTransformation> virtualTransf;

	public BootInitializer()
	{
		this.resourcesConfig = new HashMap<String, ConfigResources>();
		this.servicesConfig = new HashMap<String, ConfigServices>();
		this.transformationsConfig = new HashMap<String, ArrayList<ConfigTransformation>>();
		this.virtualTransf = new HashMap<String,ConfigVirtualTransformation>();
	}

	/**
	 *Faz a leitura das configurações do arquivo xml
	 *@param file arquivo de configuração em xml
	 */
	private void loadConfigParameters(String file)
	{
		
		try
		{
			// readin xml file into a inputstream
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			docFactory.setNamespaceAware(true);
			FileInputStream inputStreamXmlConfig = new FileInputStream(new File(file));
			DocumentBuilder builder = docFactory.newDocumentBuilder();

			// parsing file
			Document doc = builder.parse(inputStreamXmlConfig);

			// getting services information
			NodeList services = doc.getElementsByTagName("services");
			NodeList serviceConfig = ((Element) services.item(0)).getElementsByTagName("service");

			for (int i = 0; i < serviceConfig.getLength(); i++)
			{
				Element service = (Element) serviceConfig.item(i);
				ConfigServices serviceConf = new ConfigServices();

				serviceConf.setId(service.getAttribute("id"));
				serviceConf.setType(service.hasAttribute("type")?service.getAttribute("type"):"regular");
				serviceConf.setService(service.getElementsByTagName("url").item(0).getTextContent());
				serviceConf.setRefreshRate(service.getElementsByTagName("refresh-rate").item(0).getTextContent());
				serviceConf.setReturnType(service.getElementsByTagName("return-type").item(0).getTextContent());

				servicesConfig.put(service.getAttribute("id"), serviceConf);
			}

			// getting information about  resources
			NodeList resources = doc.getElementsByTagName("resources");
			NodeList resourceConfig = ((Element) resources.item(0)).getElementsByTagName("resource");

			for (int i = 0; i < resourceConfig.getLength(); i++)
			{
				Element resource = (Element) resourceConfig.item(i);
				ConfigResources resourceConf = new ConfigResources();

				resourceConf.setType(resource.getAttribute("type"));
				resourceConf.setRdfType(resource.getElementsByTagName("rdf-type").item(0).getTextContent());
				resourceConf.setUri(resource.getElementsByTagName("uri").item(0).getTextContent());

				// reading static literals
				NodeList staticLiterals = resource.getElementsByTagName("static-literal");
				for (int j = 0; j < staticLiterals.getLength(); j++)
				{
					Element staticLiteral = (Element) staticLiterals.item(j);
					
					String value = staticLiteral.getElementsByTagName("value").item(0).getTextContent();
					String parentProperty = staticLiteral.getElementsByTagName("parent-property").item(0).getTextContent();
					resourceConf.insertStaticResources(parentProperty, value);
				}

				// getting information about object properties
				NodeList objectProperties = resource.getElementsByTagName("object-property");
				for (int j = 0; j < objectProperties.getLength(); j++)
				{
					Element objProp = (Element) objectProperties.item(j);
					String type = objProp.getAttribute("type");
					String objProperty = objProp.getElementsByTagName("property").item(0).getTextContent();
					String objUri = objProp.getElementsByTagName("object-uri").item(0).getTextContent();

					resourceConf.insertObjectProperties(type, objProperty, objUri);
				}

				// adding resource information in the list
				resourcesConfig.put(resourceConf.getUri(), resourceConf);

				
				//getting virtual mappings
				NodeList virtualMappings = resource.getElementsByTagName("virtual-mapping");
			
				for(int j = 0 ; j < virtualMappings.getLength(); j ++)
				{
					ConfigVirtualTransformation cvt = new ConfigVirtualTransformation();
					
					Element virtualMapping = (Element) virtualMappings.item(j);
					String refreshRate = virtualMapping.getElementsByTagName("virtual-refresh-rate").item(0).getTextContent();
					String parentProperty = virtualMapping.getElementsByTagName("parent-property").item(0).getTextContent();
					String formula = virtualMapping.getElementsByTagName("formula").item(0).getTextContent();
					
					cvt.setParentUri(resourceConf.getUri());
					cvt.setParentProperty(parentProperty);
					cvt.setFormula(formula);
					cvt.setRefreshRate(refreshRate);
					
					
					NodeList formulaParameters = virtualMapping.getElementsByTagName("param-mapping");
					HashMap<String,String> params = new HashMap<String, String>();
					for(int n =0 ; n < formulaParameters.getLength() ; n++)
					{
						Element formulaParam = (Element) formulaParameters.item(n);
						String param =  formulaParam.getElementsByTagName("formula-param").item(0).getTextContent();
						String query = 	formulaParam.getElementsByTagName("parameter").item(0).getTextContent();
						params.put(param, query);
					}
					cvt.setFormulaParameters(params);
					
					
					//if has conditional value
					if(virtualMapping.getElementsByTagName("conditional-value").getLength() > 0)
					{
						cvt.setConditional(true);
						
						Element condValue = (Element) virtualMapping.getElementsByTagName("conditional-value").item(0); 
						String type = condValue.getAttribute("type");
						cvt.setConditionalType(type);
						
						NodeList conditions = condValue.getElementsByTagName("condition");
						HashMap<String,String> cond = new HashMap<String, String>();
						for(int k = 0 ; k < conditions.getLength(); k++)
						{
							Element condCase = (Element) conditions.item(k);
							String conditionCase = condCase.getElementsByTagName("case").item(0).getTextContent();
							String value = condCase.getElementsByTagName("content").item(0).getTextContent();
							cond.put(conditionCase, value);
						}
						cvt.setConditions(cond);
					}
					else
					{
						cvt.setConditional(false);
					}
					 
					this.virtualTransf.put(cvt.getParentUri(), cvt);
				}
				
				
				
				
				// getting dynamic literals
				NodeList dynamicLiterals = resource.getElementsByTagName("dynamic-literal");
				for (int j = 0; j < dynamicLiterals.getLength(); j++)
				{
					Element dynLit = (Element) dynamicLiterals.item(j);
					ConfigTransformation confTrans = new ConfigTransformation();

					String idService = dynLit.getAttribute("service-id");
					ConfigServices serviceConf = servicesConfig.get(idService);
					String serviceReturnType = serviceConf.getReturnType();
					String serviceUrl = serviceConf.getService();

					String parentUri = resourceConf.getUri();
					String type = (dynLit.hasAttribute("type") ? dynLit.getAttribute("type") : "not-unique");
					String group = dynLit.getElementsByTagName("group").item(0).getTextContent();

					confTrans.setFormatType(serviceReturnType);
					confTrans.setGroup(group);
					confTrans.setUnique(!type.equals("not-unique"));
					confTrans.setResourceURL(parentUri);
					confTrans.setService(serviceUrl);

					NodeList literalsTransformations = dynLit.getElementsByTagName("literal");
					for (int n = 0; n < literalsTransformations.getLength(); n++)
					{
						Element literalTrans = (Element) literalsTransformations.item(n);

						String value = literalTrans.getElementsByTagName("value").item(0).getTextContent();
						String parentProperty = literalTrans.getElementsByTagName("parent-property").item(0).getTextContent();
						confTrans.insertTransformation(parentProperty, value);
					}

					if (transformationsConfig.containsKey(serviceConf.getService()))
					{
						ArrayList<ConfigTransformation> trans = transformationsConfig.get(serviceConf.getService());
						trans.add(confTrans);
					} 
					else
					{
						ArrayList<ConfigTransformation> trans = new ArrayList<ConfigTransformation>();
						trans.add(confTrans);
						transformationsConfig.put(serviceConf.getService(), trans);
					}
				}

			}

		} catch (Exception e)
		{
			e.printStackTrace();
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
				TripleStoreDriver.insertTriplesSPO(confRes.getUri(), CommonProperties.RDFTYPE,confRes.getRdfType() );
				
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
						TripleStoreDriver.insertTriplesSPO(uri, objProperty,confRes.getUri() );
					}
				}
			}
		}
	}

	/**
	 *Executa a carga dos parâmetros de configuração
	 *@param configFile arquivo de configuração
	 */
	public void loadConfigFile(String configFile)
	{
		this.loadConfigParameters(configFile);//loading parameter from config file
		this.loadStaticResources(); //loading static triples into repository
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
	public HashMap<String,ConfigVirtualTransformation> getVirtualTransformationsConfig()
	{
		return this.virtualTransf;
	}
}
