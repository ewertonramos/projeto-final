package br.ufrj.ppgi.greco.util.config;

import java.util.HashMap;


/**
 * Representa a configuração de transformação de dados
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ConfigTransformation
{
	private String resourceURL;
	private String formatType; // Json or XML
	private String service;
	private String group; 
	private boolean unique;
	private HashMap<String,String> configProperties; //<transformationString,parentProperty>
	
	
	public ConfigTransformation()
	{
		this.configProperties = new HashMap<String, String>();
		
	}

	public String getService()
	{
		return service;
	}

	/**
	 *Define o serviço para o qual a transformação está associada
	 *@param service serviço associado a transformação
	 */
	public void setService(String service)
	{
		this.service = service;
	}

	
	/**
	 *Define uma determinada transformação de dados 
	 *@param parentProperty propriedade associada a transformação
	 *@param transformation transformação de dados em si
	 */
	public void insertTransformation( String parentProperty, String transformation)
	{	
		this.configProperties.put(transformation, parentProperty);
	}
	
	/**
	 *Retorna as transformações de dados associadas a seus respectivos serviços REST
	 *@return HashMap<String,String> tranformações de dados associadas aos serviços REST
	 */
	public HashMap<String,String> getTransformation()
	{
		return this.configProperties;
	}
	
	/**
	 *Retorna o grupo de dados associado a cada transformação
	 *@return String grupo associado a transformação.
	 */
	public String getGroup()
	{
		return group;
	}
	

	/**
	 *Define o grupo de dados associado a cada transformação
	 *@param group grupo de dados 
	 */
	public void setGroup(String group)
	{
		this.group = group;
	}

	/**
	 *Retorna o formato de dados (XML ou JSON)
	 *@return String formato de dados
	 */
	public String getFormatType()
	{
		return formatType;
	}

	/**
	 *Define o  tipo de representação dados (XML ou JSON) onde a transformação será aplicada
	 *@param formatType o tipo de representação de dados (XML ou JSON)
	 */
	public void setFormatType(String formatType)
	{
		this.formatType = formatType;
	}
	
	/**
	 *Retorna se a transformação de dados corresponde a uma propriedade única ou não
	 *@return Boolean se o tipo de transformação corresponde a uma propriedade única ou não
	 */
	public boolean isUnique()
	{
		return unique;
	}

	/**
	 *Define se a transformação está associada a uma propriedade única ou não
	 *@param unique tipo de transformação
	 */
	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}

	/**
	 *Retorna a URL do recurso associado a tranformação
	 *@return String URL do recurso associado a tranformação 
	 */
	public String getResourceURL()
	{
		return resourceURL;
	}

	
	/**
	 *Define a URL do recurso associado a tranformação
	 *@param resourceURL URL do recurso
	 */
	public void setResourceURL(String resourceURL)
	{
		this.resourceURL = resourceURL;
	}
	
	/**
	 *Retorna a representação da configuração da transformação como String 
	 *@return String representação da configuração
	 */
	public String toString()
	{
		String result="";
		result+= "Resource: "+this.resourceURL+"\n";
		result+= "Service: "+this.service+"\n";
		result+= "GROUP: "+this.group+"\n";
		result+= "Format type: "+this.formatType+"\n";
		 
		result+= "Unique: "+this.unique+"\n";
		
		result+="Configs: \n";
		for(String key : this.configProperties.keySet())
			result+="\t"+key+" : "+this.configProperties.get(key)+"\n";

		return result;
	}

}
