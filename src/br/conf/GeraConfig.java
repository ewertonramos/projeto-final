package br.conf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeraConfig {
	private final static Logger logger = LoggerFactory.getLogger(GeraConfig.class);

	public static void main(String[] args) {
		
		String point = GeraConfig.class.getResource("template-point.xml").getPath().substring(1);
		String stop = GeraConfig.class.getResource("template-stop.xml").getPath().substring(1);
		String direction = GeraConfig.class.getResource("template-direction.xml").getPath().substring(1);
		
		List<String> line = geraEntradas();
		int i = 1;
		for (String l : line) {
			geraXML(point, "saida-point.xml", i, l);
			geraXML(stop, "saida-stop.xml", i+100, l);
			geraXML(direction, "saida-direction.xml", i+200, l);
			++i;
		}
	}

	private static List<String> geraEntradas() {
		StringBuilder sb = new StringBuilder();
		sb.append("Alewife           ,42.395261      ,-71.14244      ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Davis             ,42.39662       ,-71.122527     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Porter Square     ,42.388353      ,-71.119159     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Harvard Square    ,42.373936      ,-71.118917     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Central Square    ,42.365314      ,-71.103666     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Kendall           ,42.362427      ,-71.086058     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Charles/MGH       ,42.361279      ,-71.070493     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Park Street       ,42.356332      ,-71.062202     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Downtown Crossing ,42.355453      ,-71.060465     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("South             ,42.352573      ,-71.055428     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Broadway          ,42.342793      ,-71.057117     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("Andrew            ,42.329752      ,-71.056979     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("JFK/UMass         ,42.321065      ,-71.052545     ,Ashmont     ,Braintree ,Alewife #");
		sb.append("North Quincy      ,42.274957      ,-71.029307     ,Braintree   ,Alewife   ,        #");
		sb.append("Wollaston         ,42.265972      ,-71.019721     ,Braintree   ,Alewife   ,        #");
		sb.append("Quincy Center     ,42.250879      ,-71.004798     ,Braintree   ,Alewife   ,        #");
		sb.append("Quincy Adams      ,42.232848      ,-71.007034     ,Braintree   ,Alewife   ,        #");
		sb.append("Braintree         ,42.20855       ,-71.00085      ,Braintree   ,Alewife   ,        #");
		sb.append("Savin Hill        ,42.311099      ,-71.053175     ,Ashmont     ,Alewife   ,        #");
		sb.append("Fields Corner     ,42.299992      ,-71.061516     ,Ashmont     ,Alewife   ,        #");
		sb.append("Shawmut           ,42.293712      ,-71.065912     ,Ashmont     ,Alewife   ,        #");
		sb.append("Ashmont           ,42.284219      ,-71.063229     ,Ashmont     ,Alewife   ,        #");
		sb.append("Cedar Grove       ,42.279712      ,-71.060327     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Butler            ,42.272253      ,-71.062453     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Milton            ,42.270093      ,-71.067612     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Central Avenue    ,42.269965      ,-71.073249     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Valley Road       ,42.267772      ,-71.083025     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Capen Street      ,42.267622      ,-71.087436     ,Ashmont     ,Mattapan  ,        #");
		sb.append("Mattapan          ,42.267586      ,-71.092021     ,Ashmont     ,Mattapan  ,        #");
		String[] linhas = sb.toString().split("#");
		List<String> lines = new ArrayList<String>();
		for (int i = 0; i < linhas.length; i++) {
			lines.add(linhas[i]);
		}
		return lines;
	}

	private static void geraXML(String entrada, String saida, int resNum, String params) {
		String[] param = trataParams(params);
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(entrada));
		} catch (FileNotFoundException e) {
			logger.error("Arquivo não encontrado", e);
		}
		try {
			bw = new BufferedWriter(new FileWriter(saida, (resNum!=1&&resNum!=101&&resNum!=201)));
			String line = br.readLine();
			while (line != null) {
				String linha = replaceTokens(line, resNum, param);
				if(!"\n".equals(linha)) {
					bw.write(linha);
				}
				line = br.readLine();
			}
		} catch (IOException e) {
			logger.error("Erro de IO",e);
		} finally {
			try {
				bw.close();
				br.close();
			} catch (IOException e) {
				logger.error("Erro ao fechar arquivo", e);
			}
		}
	}

	private static String[] trataParams(String params) {
		String[] split = params.split(",");
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		return split;
	}

	private static String replaceTokens(String line, int resNum, String[] param) {
		line = line.replace("**NUMERO**", Integer.toString(resNum));
		line = line.replace("**NUMERO1**", Integer.toString(resNum+100));
		line = line.replace("**NUMERO2**", Integer.toString(resNum+200));
		line = line.replace("**ESTACAO**", param[0]);
		line = line.replace("XXXX", param[1]);
		line = line.replace("YYYY", param[2]);
		line = line.replace("**DESTINO1**", param[3]);
		line = line.replace("**DESTINO2**", param[4]);
		if(line.contains("<!--DEPENDE-->")) {
			if(!"".equals(param[5])) {
				line = line.replace("<!--DEPENDE-->", "");
				line = line.replace("**DESTINO3**", param[5]);
			} else {
				line = "";
			}
		}
		
		return line+"\n";
	}
}
