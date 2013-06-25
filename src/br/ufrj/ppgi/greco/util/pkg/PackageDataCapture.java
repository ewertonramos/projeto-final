package br.ufrj.ppgi.greco.util.pkg;


/**
 * Representa um objeto de armazenamento para um pacote de dado recebido
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class PackageDataCapture
{
	private String service;
	private String data;
	private String dateTime;
	
	/**
	 *Retorna a URL de um serviço REST
	 *@return String URL de um serviço REST
	 */
	public String getService()
	{
		return service;
	}
	
	/**
	 *Define a URL de um serviço REST
	 *@param service URL do serviço
	 */
	public void setService(String service)
	{
		this.service = service;
	}
	
	/**
	 *Retorna um pacote de dado recebido de serviço
	 *@return String URL de um serviço REST
	 */
	public String getData()
	{
		return data;
	}
	
	/**
	 *Define o pacote de dados proveniente de um serviço
	 *@param data dados recebidos de um serviço REST
	 */
	public void setData(String data)
	{
		this.data = data;
	}
	
	/**
	 *Retorna o instante de tempo quando um determinado dado foi recebido
	 *@return String intante de tempo do recebimento de dado de um serviço
	 */
	public String getDateTime()
	{
		return dateTime;
	}
	
	/**
	 *Define o instante de tempo quando o dado de um serviço REST chegou ao Framework
	 *@param dateTime instante de tempo
	 */
	public void setDateTime(String dateTime)
	{
		this.dateTime = dateTime;
	}
	
	
	/**
	 *Retorna a representação do objeto de captura de dados como String
	 *@return String representação do objeto de captura de dados
	 */
	public String toString()
	{
		String result = this.dateTime+": "+this.service+"\n";
		result+= "\t"+this.data+"\n";
		result+="---------------------------------------\n";
		return result;
	}
	
}
