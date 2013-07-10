package br.ufrj.ppgi.greco.dataTransformation.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

public interface ITransformation {
	
	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String data, String group, HashMap<String, String> transformationsIndexedByLabel);
	
}
