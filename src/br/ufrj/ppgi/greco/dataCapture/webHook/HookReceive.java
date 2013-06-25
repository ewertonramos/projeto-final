package br.ufrj.ppgi.greco.dataCapture.webHook;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import br.ufrj.ppgi.greco.util.pkg.PackageDataCapture;
import br.ufrj.ppgi.greco.util.queue.QueueDataCapture;

/**
 * 
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class HookReceive
{
	public static Server server;

	/**
	 *Processa o recebimento de dados através de WebHooks
	 */
	public static Handler getHookReceiveHandler()
	{
		Handler handler = new AbstractHandler()
		{
			public void handle(String target , Request req , HttpServletRequest request , HttpServletResponse response) throws IOException, ServletException
			{
				if (target.equals("/serviceHook"))
				{
					System.out.println(request.getParameter("network_id") + " : " + request.getParameter("data"));
					response.setContentType("text/html");
					response.setStatus(HttpServletResponse.SC_OK);
					((Request) request).setHandled(true);

					String service = "http://" + request.getLocalName() + ":" + request.getLocalPort() + target + "?network_id=" + request.getParameter("network_id");
					String data = request.getParameter("data");

					PackageDataCapture pdc = new PackageDataCapture();
					pdc.setService(service);
					pdc.setData(data);
					java.util.Date date = new java.util.Date();
					pdc.setDateTime((new Timestamp(date.getTime())).toString());

					// adding a package in the queue
					ArrayList<PackageDataCapture> queue = QueueDataCapture.getQueue();
					queue.add(pdc);

				}
			}
		};
		
		return handler;

	}

}
