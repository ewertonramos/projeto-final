package br.ufrj.ppgi.greco.util.db;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe para comunicação com o repositório de triplas
 * 
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class UtilTripleStoreConnection {
	private static final Logger log = LoggerFactory.getLogger(UtilTripleStoreConnection.class);
	private static RepositoryConnection conn;

	/**
	 * Retorna um objeto de conexão com o repositório de triplas
	 * @return RepositoryConnection objeto de conexão com o repositório de triplas
	 */
	public static RepositoryConnection getConnection() {
		if (conn == null) {
			String sesameServer = "http://localhost:8080/openrdf-sesame";
			String repositoryID = "semantichub";

			Repository myRepository = new HTTPRepository(sesameServer, repositoryID);

			try {
				conn = myRepository.getConnection();
				conn.setAutoCommit(false);
			} catch (RepositoryException e) {
				log.error("Error intantiating connection.",e);
			}

		}

		return conn;
	}

}