package br.ufrj.ppgi.greco.util.queue;

import java.util.ArrayList;

import br.ufrj.ppgi.greco.util.pkg.PackageDataTransformed;


/**
 * Representa um buffer de armazenamento de dados transformados em RDF
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class QueueDataTransformed
{
	private static final ArrayList<PackageDataTransformed> queue = new ArrayList<PackageDataTransformed>();
	
	

	/**
	 *Retorna a referência para o buffer de dados transformados
	 *@return ArrayList<PackageDataTransformed> referência do buffer
	 */
	public static ArrayList<PackageDataTransformed> getQueue()
	{
		return queue;
	}

}
