package br.ufrj.ppgi.greco.util.pkg;

/**
 * Representa um pacote de dados transformado em RDF
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class PackageDataTransformed
{
	
	
	private final String TYPELITERAL= "literal";
	private final String TYPEOBJECT= "object";
	private String type=""; // if a literal or a object in third sentence
	private boolean unique;
	
	
	private String subj="";
	private String pred="";
	private String literal;
	private String object;
	
	
	/**
	 *Insere um tripla RDF no formato (sujeito, predicado, objeto)
	 *@param subj URI do sujeito
	 *@param pred URI predicado
	 *@param object URI do objeto
	 */
	public void insertSPO(String subj, String pred, String object)
	{
		this.type= this.TYPEOBJECT;
		this.subj=subj;
		this.pred=pred;
		this.object = object;
	}
	
	/**
	 *Insere um tripla RDF no formato (sujeito, predicado, literal)
	 *@param subj URI do sujeito
	 *@param pred URI do predicado
	 *@param literal valor do literal 
	 */
	public void insertSPL(String subj, String pred, String literal)
	{
		this.type = this.TYPELITERAL;
		this.subj = subj;
		this.pred = pred;
		this.literal = literal;
	}
	
	/**
	 *Retorna o tipo de tripla armazenada
	 *@return String tipo de tripla (SPO ou SPL) 
	 */
	public String getType()
	{
		return type;
	}
	
	/**
	 *Retorna o sujeito de uma tripla
	 *@return String URI do sujeito de uma tripla RDF 
	 */
	public String getSubject()
	{
		return this.subj;
	}
	
	/**
	 *Retorna o predicado de uma tripla
	 *@return String URI do predicado de uma tripla RDF 
	 */
	public String getPredicate()
	{
		return this.pred;
	}
	
	/**
	 *Retorna o literal de uma tripla
	 *@return String valor do literal 
	 */
	public String getLiteral()
	{
		return this.literal;
	}
	
	/**
	 *Retorna o objeto de uma tripla
	 *@return String URI do sujeito de uma tripla RDF 
	 */
	public String getObject()
	{
		return this.object;
	}

	/**
	 *Retorna se a tripla é única
	 *@return boolean se esta tripla é unica ou não 
	 */
	public boolean isUnique()
	{
		return unique;
	}

	
	/**
	 *Define se a tripla é única ou não
	 *@param unique booleano informando se a tripla é única ou não
	 */
	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}
	
	
	
}
