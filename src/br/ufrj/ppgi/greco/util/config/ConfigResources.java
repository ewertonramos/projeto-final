package br.ufrj.ppgi.greco.util.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Representa a configuração de um recurso
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ConfigResources
{
	
	private String separator;
	private String separatorChar;
	private String withHeader;
	private Map<String, String> headers;
	private String type;
	private String uri;
	private String rdfType;
	
	private ArrayList<String> staticLiterals;
	private ArrayList<String> objectProperties;
	
	
	public ConfigResources()
	{
		this.separator ="separator123";
		this.staticLiterals = new ArrayList<String>();
		this.objectProperties = new ArrayList<String>();
	}
	
	/**
	 *Retorna o tipo de serviço
	 *@return String o tipo do recurso (estático ous dinâmico).
	 */
	public String getType()
	{
		return type;
	}

	
	/**
	 *Define o tipo de recurso
	 *@param type o tipo do recurso (estático, dinâmico ou virtual).
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	
	/**
	 * Retorna a URI do recurso
	 * @return String URI do recurso
	 */
	public String getUri()
	{
		return uri;
	}

	/**
	 * Define a URI do recurso
	 * @param uri URI do recurso
	 */
	public void setUri(String uri)
	{
		this.uri = uri;
	}

	/**
	 *Retorna a tipo RDF do recurso
	 *@return String rdf:type
	 */
	public String getRdfType()
	{
		return rdfType;
	}

	/**
	 *Define o tipo RDF do recurso
	 *@param rdfType URI do recurso
	 */
	public void setRdfType(String rdfType)
	{
		this.rdfType = rdfType;
	}

	public String getSeparatorChar() {
		return separatorChar;
	}

	public void setSeparatorChar(String separatorChar) {
		this.separatorChar = separatorChar;
	}

	public String getWithHeader() {
		return withHeader;
	}

	public void setWithHeader(String withHeader) {
		this.withHeader = withHeader;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void addHeader(String index, String name) {
		if(this.headers == null) {
			this.headers = new HashMap<String, String>();
			this.headers.put(index, name);
		} else {
			this.headers.put(index, name);
		}
	}
	
	/**
	 *Define as propriedades de dados estáticas do recurso
	 *@param parentProperty propriedade
	 *@param value o valor da propriedade de dado
	 */
	public void insertStaticResources(String  parentProperty, String value)
	{
		this.staticLiterals.add(parentProperty+this.separator+value);
	}

	/**
	 *Define as propriedades de objetos do recurso
	 *@param type se o recurso será o sujeito (tipo dominante) ou o objeto (tipo não dominante).
	 *@param objectProperty propriedade
	 *@param uri URI do recurso
	 */
	public void insertObjectProperties(String type,String objectProperty, String uri )
	{
		this.objectProperties.add(type+this.separator+objectProperty+this.separator+uri);
	}
	
	/**
	 *Retorna as propriedades de dados estáticas associadas ao recurso
	 *@return ArrayList<String>
	 */
	public ArrayList<String> getStaticLiterals()
	{
		return this.staticLiterals;
	}

	/**
	 *Retorna as propriedades de objetos associados aos recursos
	 *@return ArrayList<String>
	 */
	public ArrayList<String> getObjectProperties()
	{
		return this.objectProperties;
	}
	
	public String getSeparator()
	{
		return this.separator;
	}
	
	public String toString()
	{
		String result="";
		
		result += "URI: "+this.uri;
		result += "\nRDF TYPE:"+this.rdfType;
		
		result+="\n\nOBJ Properties:";
		for(String objProp: this.objectProperties)
			result+="\n\t"+objProp;
		
		result+="\n\n Static Literals: ";
		for(String staticLit: this.staticLiterals)
			result+="\n\t"+staticLit;
		
		return result;
	}

}
