package br.ufrj.ppgi.greco.dataTransformation.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Gerenciador do fluxo de transformação de dados em XML 
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class XMLTransformation
{

	DocumentBuilderFactory docFactory;
	XPathFactory xPathfactory;

	
	public XMLTransformation()
	{
		this.docFactory = DocumentBuilderFactory.newInstance();
		this.docFactory.setNamespaceAware(true);
		this.xPathfactory = XPathFactory.newInstance();

	}

	/**
	 *Processa a transformação de dados em XML
	 *@param xmlInput dado retornado pelo serviço REST em XMl
	 *@param groupPath grupo base para extração de dados
	 *@param transformation conjunto de transformações a serem aplicados a cada item de grupo
	 *@return ArrayList<HashMap<String, ArrayList<String>>> lista de dados extraídos indexados pela URI do serviço de origem
	 */
	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String xmlInput , String groupPath , HashMap<String, String> transformation)
	{
		ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();

		try
		{
			// System.out.println("Com: "+xmlInput);
			xmlInput = xmlInput.replaceAll("xmlns=\"(.*?)\"", "");
			// System.out.println("Sem: "+xmlInput);

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xmlInput));
			is.setEncoding("UTF-8");
			DocumentBuilder builder = this.docFactory.newDocumentBuilder();
			Document doc = builder.parse(is);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();

			XPathExpression expr = xpath.compile(groupPath);
			NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

			// for each group
			for (int i = 0; i < nodes.getLength(); i++)
			{
				Element e = (Element) nodes.item(i);
				HashMap<String, ArrayList<String>> res = new HashMap<String, ArrayList<String>>();

				// for each transformation path
				for (String transfPath : transformation.keySet())
				{
					expr = xpath.compile(transfPath);
					NodeList nodes2 = (NodeList) expr.evaluate(e, XPathConstants.NODESET);

					ArrayList<String> dataValues = new ArrayList<String>();
					for (int j = 0; j < nodes2.getLength(); j++)
					{
						dataValues.add(nodes2.item(j).getNodeValue());
					}
					res.put(transformation.get(transfPath), dataValues);

				}
				result.add(res);

			}

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		return result;
	}

}
