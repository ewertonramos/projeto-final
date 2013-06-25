package br.ufrj.ppgi.greco.util.config;

import java.util.HashMap;


/**
 * Representa a configuração de geração de dados virtuais
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class ConfigVirtualTransformation
{
	private String parentUri;
	private String parentProperty;
	private String refreshRate;
	
	private String formula;
	private HashMap<String,String> formulaParameters; //ex: <x,select ?a where {?a ?b ?c }>
	
	private boolean conditional;
	private String conditionalType; // literal or object
	private HashMap<String,String> conditions; //ex: ( > 3 , dangerous)  
	
	
	public String getParentUri()
	{
		return parentUri;
	}
	
	/**
	 *Define a URI do recurso para a qual a propriedade está associada
	 *@param parentUri URI do recurso
	 */
	public void setParentUri(String parentUri)
	{
		this.parentUri = parentUri;
	}
	
	/**
	 *Retorna a taxa de atualização da propriedade virtual
	 *@return String taxa de atualização
	 */
	public String getRefreshRate()
	{
		return refreshRate;
	}
	
	/**
	 *Define a taxa de atualização da propriedade virtual
	 *@param refreshRate taxa de atualização
	 */
	public void setRefreshRate(String refreshRate)
	{
		this.refreshRate = refreshRate;
	}
	
	
	/**
	 *Retorna a fórmula de geração da propriedade de dado virtual
	 *@return String fórmula
	 */
	public String getFormula()
	{
		return formula;
	}
	
	/**
	 *Define a fórmula de geração da propriedade de dado virtual 
	 *@param formula fórmula de agregação de dados
	 */
	public void setFormula(String formula)
	{
		this.formula = formula;
	}
	
	
	/**
	 *Retorna os parâmetros da fórmula de geração da propriedade de dado virtual
	 *@return HashMap<String,String> parâmetros da fórmula
	 */
	public HashMap<String, String> getFormulaParameters()
	{
		return formulaParameters;
	}
	
	
	/**
	 *Define os parâmetros da fórmula de geração da propriedade de dado virtual
	 *@param formulaParameters parâmetros da fórmula
	 */
	public void setFormulaParameters(HashMap<String, String> formulaParameters)
	{
		this.formulaParameters = formulaParameters;
	}
	
	
	/**
	 *Retorna um booleano dizendo se esta configuração virtual está associada a condições por faixa de valores
	 *@return Boolean se a configuração está associada a condições
	 */
	public boolean isConditional()
	{
		return conditional;
	}
	
	/**
	 *Define se a configuração de propriedade de dado virtual está associada a uma série de condições
	 *@param conditional se a propriedade de dado virtual é condicional ou não
	 */
	public void setConditional(boolean conditional)
	{
		this.conditional = conditional;
	}
	
	/**
	 *Retorna o tipo de condicional
	 *@return String tipo condicional
	 */
	public String getConditionalType()
	{
		return conditionalType;
	}
	
	/**
	 *Define o tipo de condicional
	 *@param conditionalType tipo de condicional
	 */
	public void setConditionalType(String conditionalType)
	{
		this.conditionalType = conditionalType;
	}
	
	/**
	 *Retorna uma lista com as condições por faixas de valores
	 *@return HashMap<String,String> lista de condiços por faixa de valores
	 */
	public HashMap<String, String> getConditions()
	{
		return conditions;
	}
	
	/**
	 *Define o tipo de condicional
	 *@param conditions valores das condições indexadas pelos intervalos de valores
	 */
	public void setConditions(HashMap<String, String> conditions)
	{
		this.conditions = conditions;
	}
	
	/**
	 *Retorna a propriedade que irá associar a propriedade de dados virtual ao recurso
	 *@return String  propriedade que irá associar a propriedade de dados virtual ao recurso
	 */
	public String getParentProperty()
	{
		return parentProperty;
	}
	
	/**
	 *Define a propriedade que irá associar a propriedade de dados virtual ao recurso
	 *@param parentProperty a propriedade que irá associar a propriedade de dados virtual ao recurso
	 */
	public void setParentProperty(String parentProperty)
	{
		this.parentProperty = parentProperty;
	}
	
}
