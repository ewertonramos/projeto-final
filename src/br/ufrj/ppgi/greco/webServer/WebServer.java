package br.ufrj.ppgi.greco.webServer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;



/**
 * Representa o servidor web que provê os serviços REST a serem utilizados nos WEBHOOKS
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class WebServer
{
	private static Server server;
	
	/**
	 *Inicia o servidor WEB
	 *@param handlers definições dos caminhos de acessos para URLs do servidor
	 */
	public static void startWebServer(int port, HandlerCollection handlers)
	{
		server = new Server(port);
		server.setHandler(handlers);
		
		try
		{
			server.start();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *Pára o servidor WEB
	 */
	public static void stopWebServer()
	{
		if(server != null)
		{	
			try
			{
				server.stop();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}	
	}
}
