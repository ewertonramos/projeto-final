package br.ufrj.ppgi.greco.util.config;


/**
 * Representa a configuração de um serviço
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ConfigServices
{
	public String id;
	public String service;
	public String refreshRate;
	public String returnType;
	public String type; //if is a webhook or a regular service
	
	
	/**
	 *Retorna o identificador de um determinado serviço REST
	 *@return String identificador de um serviço REST
	 */
	public String getId()
	{
		return id;
	}
	
	/**
	 *Define o identificador de um determinado serviço REST
	 *@param id identificador do serviço REST
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	
	/**
	 *Retorna a URL de um serviço REST
	 *@return String URL de um serviço REST
	 */
	public String getService()
	{
		return service;
	}
	
	/**
	 *Define a URL do serviço REST
	 *@param service URL do serviço REST
	 */
	public void setService(String service)
	{
		this.service = service;
	}
	
	/**
	 *Retorna a taxa de transferência que um serviço é consumido
	 *@return String taxa de transferência
	 */
	public String getRefreshRate()
	{
		return refreshRate;
	}
	
	/**
	 *Define a taxa de atualização de um determinado serviço REST
	 *@param refreshRate taxa de atualização de um determinado serviço REST
	 */
	public void setRefreshRate(String refreshRate)
	{
		this.refreshRate = refreshRate;
	}
	
	/**
	 *Retorna o tipo de representação que os dados dos serviços estão representados (XML ou JSON)
	 *@return String retorna o tipo de representação de dados (XML ou JSON)
	 */
	public String getReturnType()
	{
		return returnType;
	}

	/**
	 *Define o tipo de representação que os dados do serviço utilizam (XML ou JSON)
	 *@param returnType tipo de representação de dados
	 */
	public void setReturnType(String returnType)
	{
		this.returnType = returnType;
	}
	
	/**
	 *Retorna o tipo de configuração serviço (se é um serviço REST consumido ou um serviço REST provido para um WEBHOOK).
	 *@return String o tipo de configuração do serviço (se é um serviço REST consumido ou um serviço REST provido para um WEBHOOK).
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *Define o tipo do serviço
	 *@param type tipo
	 */
	public void setType(String type)
	{
		this.type = type;
	}
	
	/**
	 *Retorna a representação de configuração do serviço como um String
	 *@return String representação da configuração do serviço como String
	 */
	public String toString()
	{
		String result= "";

		result+="Service URL: "+ this.getService();
		result+="\nReturn Type: "+this.getReturnType();
		result+="\nRefresh Rate: "+this.getRefreshRate();
		result+="\n--------------------";
		return result;
	}
	
}
