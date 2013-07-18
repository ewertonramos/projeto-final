package br.ufrj.ppgi.greco.main;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import br.ufrj.ppgi.greco.bootProcessor.BootInitializer;
import br.ufrj.ppgi.greco.bootProcessor.BootInitializerDB;
import br.ufrj.ppgi.greco.dataCapture.services.ServiceCaptureScheduler;
import br.ufrj.ppgi.greco.dataCapture.webHook.HookReceive;
import br.ufrj.ppgi.greco.dataRDFStore.RDFStoreScheduler;
import br.ufrj.ppgi.greco.dataTransformation.services.DataTransformationScheduler;
import br.ufrj.ppgi.greco.dataTransformation.virtual.VirtualDataTransformationScheduler;
import br.ufrj.ppgi.greco.entityReconciliation.EntityReconciliationScheduler;


/**
 * Principal classe do servidor
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class SemanticHubServer
{
	/**
	 *Função principal do servidor
	 *@param args parametros de execução do servidor
	 */
	public static void main(String args[]) throws IOException
	{	
/*
		HandlerCollection handlers = new HandlerCollection();
		handlers.addHandler(SemanticHubServer.getHookReceiveHandler());
		handlers.addHandler(SemanticHubServer.getServerControllerHandler());
		
		WebServer.startWebServer(8080,handlers);
*/
		String file = "C:/workspaces/ufrj/semantichub-server/src/config3.xml";
		if(args.length > 0) {
			file = args[0];
		}
		SemanticHubServer shs = new SemanticHubServer();
		//shs.start("/Users/fabricio/Documents/mestrado/configCETIC.xml");
		shs.start(file);
	}
	
	/**
	 *Provê as interfaces de acesso para o servidor web
	 */
	public static Handler getServerControllerHandler()
	{
		Handler handler = new AbstractHandler()
		{
			SemanticHubServer shs  = new SemanticHubServer();
			
			public void handle(String target , Request req , HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException
			{
				if (target.equals("/server"))
				{
					
					System.out.println("ACTION: "+ request.getParameter("action"));
					
					if(request.getParameter("action").equals("start"))
						shs.start();
					
					if(request.getParameter("action").equals("stop"))
						shs.stop();
					
					response.setContentType("text/html");
					response.setStatus(HttpServletResponse.SC_OK);
					((Request) request).setHandled(true);

				}
			}
		};
		
		return handler;
	}

	/**
	 *Inicia o servidor
	 */
	public void start()
	{
		BootInitializerDB bi = new BootInitializerDB();
		bi.loadDatabaseConfig();

		ServiceCaptureScheduler.run(bi.getServicesConfig());
		DataTransformationScheduler.run(bi.getTransformationsConfig(),bi.getResourcesConfig());
		VirtualDataTransformationScheduler.run(bi.getVirtualTransformationsConfig());
		RDFStoreScheduler.run();
		EntityReconciliationScheduler.run();
		
	}
		
	/**
	 *Pára o servidor
	 */
	public void stop()
	{
		ServiceCaptureScheduler.stop();
		DataTransformationScheduler.stop();
		VirtualDataTransformationScheduler.stop();
		RDFStoreScheduler.stop();
		EntityReconciliationScheduler.stop();
		
	}
	
	/**
	 *Provê as interfaces de acesso para o webservice REST utilizado nos WebHooks
	 */
	public static Handler getHookReceiveHandler()
	{
		return HookReceive.getHookReceiveHandler();
	}
	
	
	/**
	 * Inicializa o servidor a partir de um arquivo de configuração
	 * @param file arquivo de configuração a ser carregado
	 */
	//with config file instead of database config parameters
	public void start(String file )
	{
		BootInitializer bi = new BootInitializer();
		bi.loadConfigFile(file);
		
		ServiceCaptureScheduler.run(bi.getServicesConfig());
		DataTransformationScheduler.run(bi.getTransformationsConfig(),bi.getResourcesConfig());
		VirtualDataTransformationScheduler.run(bi.getVirtualTransformationsConfig());
		RDFStoreScheduler.run();
		EntityReconciliationScheduler.run();
	}

}
