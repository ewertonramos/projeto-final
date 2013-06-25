package br.ufrj.ppgi.greco.util.queue;

import java.util.ArrayList;

import br.ufrj.ppgi.greco.util.pkg.PackageDataCapture;


/**
 * Representa um buffer de armazenamento de dados recebidos dos serviços REST
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class QueueDataCapture
{

	private static final ArrayList<PackageDataCapture> queue = new ArrayList<PackageDataCapture>();
	
	
	/**
	 *Retorna a referência para o buffer de dados 
	 *@return ArrayList<PackageDataCapture> referência do buffer
	 */
	public static ArrayList<PackageDataCapture> getQueue()
	{
		return queue;
	}
	
	
}
