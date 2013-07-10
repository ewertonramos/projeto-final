package br.ufrj.ppgi.greco.dataTransformation.services;

import java.util.ArrayList;
import java.util.HashMap;

import br.ufrj.ppgi.greco.dataTransformation.interfaces.ITransformation;


/**
 *Gerenciador do fluxo de transformação de dados em JSON 
 * @author Fabrício Firmino de Faria
 * @version 1.0
 */
public class JSONTransformation implements ITransformation {

	@Override
	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String data, String group, HashMap<String, String> transformationsIndexedByLabel) {
		// TODO !
		return new ArrayList<HashMap<String, ArrayList<String>>>();
	}

}
