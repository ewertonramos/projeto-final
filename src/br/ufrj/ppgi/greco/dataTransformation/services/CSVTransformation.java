package br.ufrj.ppgi.greco.dataTransformation.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class CSVTransformation {
	
	public CSVTransformation() {
		super();	
	}
	
	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String data, String group, HashMap<String, String> transformationsIndexedByLabel) {
		ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();
		try {
			CSVReader reader = new CSVReader(new StringReader(data), group.toCharArray()[0]);
			Map<Integer,Set<String>> csv = new LinkedHashMap<Integer, Set<String>>(); // <columns, values>
			String[] header = reader.readNext();
			if(header != null) {
				
			}
			String[] lines = reader.readNext();
			if(lines != null) {
				do {
					for (int i = 0; i < lines.length; i++) {
						if (csv.containsKey(i)) {
							csv.get(i).add(lines[i]);
						} else {
							Set<String> values = new LinkedHashSet<String>();
							values.add(lines[i]);
							csv.put(i, values);
						}
					}
					
					lines = reader.readNext();
				} while (lines != null);
			}
			HashMap<String, ArrayList<String>> mapping = new HashMap<String, ArrayList<String>>();
			
			for(Entry<String, String> entry: transformationsIndexedByLabel.entrySet() ){
				ArrayList<String> values = new ArrayList<String>();
				values.addAll(csv.get(Integer.parseInt(entry.getKey())));
				mapping.put(entry.getValue(), values );
			}
			result.add(mapping);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
