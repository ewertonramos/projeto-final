package br.ufrj.ppgi.greco.util.rdf;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufrj.ppgi.greco.util.db.UtilTripleStoreConnection;



/**
 * Representa um Driver para acesso aos dados do repositório de triplas
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class TripleStoreDriver {
	static final Logger log = LoggerFactory.getLogger(TripleStoreDriver.class);
	
	/**
	 *Remove uma tripla no formato sujeito-predicado-objeto
	 *@param sub sujeito
	 *@param pred predicado
	 *@param obj objeto
	 */
	public static void deleteSPO(String sub , String pred , String obj)
	{
		RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

		ValueFactory f = conn.getValueFactory();
		URI subject = f.createURI(sub);
		URI predicate = f.createURI(pred);
		URI object = f.createURI(obj);

		try
		{
			conn.remove(subject, predicate, object);
		} catch (RepositoryException e)
		{
			e.printStackTrace();
		}

	}

	
	/**
	 *Remove uma tripla no formato sujeito-predicado-literal
	 *@param sub sujeito
	 *@param pred predicado
	 *@param lit literal
	 */
	public static void deleteSPL(String sub , String pred , String lit)
	{
		RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

		ValueFactory f = conn.getValueFactory();
		URI subject = f.createURI(sub);
		URI predicate = f.createURI(pred);
		Literal literal = f.createLiteral(lit);

		try
		{
			conn.remove(subject, predicate, literal);
		} catch (RepositoryException e)
		{
			e.printStackTrace();
		}

	}

	/**
	 *Remove todas as triplas que tenham como sujeito uma determinada URI
	 *@param uri URI do sujeito
	 */
	public static void deleteAllByUri(String uri)
	{
		RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

		ValueFactory f = conn.getValueFactory();
		URI subject = f.createURI(uri);

		try
		{
			conn.remove(subject, null, null); 
			conn.commit();	
			
		} catch (RepositoryException e)
		{
			e.printStackTrace();
		}
		
		
		//get relations with deleted subject
		String querySparql = "select distinct ?resource ?property where {?resource ?property <"+subject.stringValue()+">}";
		try
		{
			TupleQuery query = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, querySparql);
			TupleQueryResult qres = query.evaluate();
			while (qres.hasNext())
			{
				BindingSet b = qres.next();
				URI resource = f.createURI(b.getValue("resource").toString());
				URI prop = f.createURI(b.getValue("property").toString());
				conn.remove(resource, prop,subject);
				conn.commit();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
        
	}

	
	/**
	 *Retorna um conjunto de triplas RDF após a execução de uma consulta sparql
	 *@param qs consulta sparql
	 *@return LinkedList<HashMap<String> conjunto de valores associados a uma URI
	 */
	public static LinkedList<HashMap<String, Value>> runSPARQL(String qs)
	{


		LinkedList<HashMap<String, Value>> resList = new LinkedList<HashMap<String, Value>>();
		try
		{
			RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

			TupleQuery query = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, qs);
			TupleQueryResult qres = query.evaluate();
			while (qres.hasNext())
			{
				BindingSet b = qres.next();
				Set<String> names = b.getBindingNames();
				HashMap<String, Value> hm = new HashMap<String, Value>();
				for (String n : names)
				{
					hm.put(n, b.getValue(n));
				}
				resList.add(hm);
			}
		} catch (Exception e) {
			log.error("Error", e);
			//e.printStackTrace();
			//System.out.println("runSPARQL: "+ qs);
			log.debug("runSPARQL: "+ qs);
			System.exit(0);
		}

		return resList;
	}
	
	
	/**
	 *Retorna um literal proveniente de uma consulta Sparql
	 *@param sparql consulta sparql
	 *@return String literal
	 */
	public static String sparqlExecGetLiteral(String sparql)
	{
		String result = "";
		try
		{
			RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

			TupleQuery query = conn.prepareTupleQuery(org.openrdf.query.QueryLanguage.SPARQL, sparql);
			TupleQueryResult qres = query.evaluate();

			while (qres.hasNext())
			{
				BindingSet b = qres.next();
				Set<String> names = b.getBindingNames();

				for (String n : names)
				{
					Value value = b.getValue(n);
					result = value.stringValue();
				}
			}

		} catch (Exception e) {
			log.error("Problem.", e);
			//e.printStackTrace();
			log.error("sparqlExecGetLiteral: "+ sparql);
			//System.out.println("sparqlExecGetLiteral: "+ sparql);
			System.exit(0);
		}

		return result.replaceAll("\"", "");

	}

	
	/**
	 *Insere uma tripla RDF no formato sujeito-predicado-objeto
	 *@param sub sujeito
	 *@param pred predicado
	 *@param obj objeto
	 */
	public static void insertTriplesSPO(String sub , String pred , String obj)
	{
		//System.out.println("SPO: " + sub + " - " + pred + " - " + obj);
		if(log.isDebugEnabled()) {
			log.debug("SPO: " + sub + " - " + pred + " - " + obj);
		}
		try
		{
			RepositoryConnection conn = UtilTripleStoreConnection.getConnection();
			ValueFactory f = UtilTripleStoreConnection.getConnection().getRepository().getValueFactory();
			URI subject = f.createURI(sub);
			URI property = f.createURI(pred);
			URI object = f.createURI(obj);
			conn.add(subject, property, object);

		} catch (RepositoryException e) {
			log.error("Problem.", e);
			//e.printStackTrace();
			log.error("insertTriplesSPO: "+ sub+" "+pred+" "+obj);
			//System.out.println("insertTriplesSPO: "+ sub+" "+pred+" "+obj);
			System.exit(0);
		}
	}

	
	/**
	 *Insere uma tripla RDF no formato sujeito-predicado-literal
	 *@param sub sujeito
	 *@param pred predicado
	 *@param lit literal
	 */
	public static void insertTriplesSPL(String sub , String pred , String lit) {
		if(log.isDebugEnabled()) {
			log.debug("SPL:" + sub + " - " + pred + " - " + lit);
		}
		//System.out.println("SPL:" + sub + " - " + pred + " - " + lit);
		try
		{
			RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

			ValueFactory f = UtilTripleStoreConnection.getConnection().getRepository().getValueFactory();
			URI subject = f.createURI(sub);
			URI property = f.createURI(pred);
			Literal literal = f.createLiteral(lit);

			conn.add(subject, property, literal);

		} catch (RepositoryException e) {
			log.error("Insetion problem.", e);
			//e.printStackTrace();
			//System.out.println("insertTriplesSPL: "+ sub+" "+pred+" "+lit);
			log.error("insertTriplesSPL: "+ sub+" "+pred+" "+lit);
			System.exit(0);
		}
	}

	
	/**
	 *Insere uma tripla RDF no formato sujeito-predicado-literal removendo outra tripla caso já exista
	 *@param sub sujeito
	 *@param pred predicado
	 *@param lit literal
	 */
	public static void insertUniqueTriplesSPL(String sub , String pred , String lit) {
		if(log.isDebugEnabled()) {
			log.debug("Unique SPL:" + sub + " - " + pred + " - " + lit);
		}
		//System.out.println("Unique SPL:" + sub + " - " + pred + " - " + lit);
		try
		{
			RepositoryConnection conn = UtilTripleStoreConnection.getConnection();

			ValueFactory f = UtilTripleStoreConnection.getConnection().getRepository().getValueFactory();
			URI subject = f.createURI(sub);
			URI property = f.createURI(pred);
			Literal literal = f.createLiteral(lit);

			conn.remove(subject, property, null);
			conn.add(subject, property, literal);

		} catch (RepositoryException e) {
			log.error("Insertion problem.", e);
			//e.printStackTrace();
			log.error("insertUniqueTriplesSPL: "+ sub+" "+pred+" "+lit);
			//System.out.println("insertUniqueTriplesSPL: "+ sub+" "+pred+" "+lit);
			System.exit(0);
		}
	}

	
	/**
	 *Commita o resultado de uma inserção ou deleção para o banco
	 */
	public static void commit()
	{
		RepositoryConnection conn = UtilTripleStoreConnection.getConnection();
		try
		{
			conn.commit();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}
