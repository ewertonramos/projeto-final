package br.ufrj.ppgi.greco.util.db;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

/**
 * Classe para comunicação com o repositório de triplas
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class UtilTripleStoreConnection
{

	private static RepositoryConnection conn;

	/**
	 *Retorna um objeto de conexão com o repositório de triplas
	 *@return RepositoryConnection objeto de conexão com o repositório de triplas
	 */
	public static RepositoryConnection getConnection()
	{

		if (conn == null)
		{
			String sesameServer = "http://localhost:8080/openrdf-sesame";
			String repositoryID = "semantichub";
			
		
			Repository myRepository = new HTTPRepository(sesameServer, repositoryID);
			
			try
			{
				conn = myRepository.getConnection();
			} 
			catch (RepositoryException e)
			{
				e.printStackTrace();
			}

		}

		return conn;
	}

}
