package br.ufrj.ppgi.greco.dataTransformation.services;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class CSVTransformation {

	private String separatorChar;
	private String withHeader;

	public CSVTransformation() {
		super();
	}

	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String data, String group, HashMap<String, String> transformationsIndexedByLabel) {
		ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();
		try {
			Character separator = ',';
			if (this.separatorChar != null && !"".equals(this.separatorChar) && this.separatorChar.length() == 1) {
				separator = this.separatorChar.charAt(0);
			}
			CSVReader reader = new CSVReader(new StringReader(data), separator);
			Map<Integer, Set<String>> csvGroup = new LinkedHashMap<Integer, Set<String>>(); // <columns, values>
			Map<Integer, List<String>> csv = new LinkedHashMap<Integer, List<String>>();

			if (this.withHeader!= null && "1".equals(this.withHeader)) {
				String[] header = reader.readNext();

			}
			List<String> groupList = Arrays.asList(group.split(separator.toString()));
			String[] lines = reader.readNext();
			while (lines != null) {
				for (int csvIndex = 1; csvIndex <= lines.length; csvIndex++) {
					int arrayIndex = csvIndex-1;
					String istring = "" + csvIndex;
					if (groupList.contains(istring)) {

						if (csvGroup.containsKey(csvIndex)) {
							csvGroup.get(csvIndex).add(lines[arrayIndex]);
						} else {
							Set<String> values = new LinkedHashSet<String>();
							values.add(lines[arrayIndex]);
							csvGroup.put(csvIndex, values);
						}
					}
					
					if (csv.containsKey(csvIndex)) {
						csv.get(csvIndex).add(lines[arrayIndex]);
					} else {
						List<String> values = new LinkedList<String>();
						values.add(lines[arrayIndex]);
						csv.put(csvIndex, values);
					}

				}
				lines = reader.readNext();
			}
			
			for (Entry<Integer, Set<String>> csvGroupEntry : csvGroup.entrySet()) {
				for (String groupValue : csvGroupEntry.getValue()) {
					HashMap<String, ArrayList<String>> mapping = new HashMap<String, ArrayList<String>>();

					for (Entry<String, String> entry : transformationsIndexedByLabel.entrySet()) {
						ArrayList<String> values = new ArrayList<String>();
						
						List<String> value = csv.get(Integer.parseInt(entry.getKey()));
						for(int i = 0; i < value.size(); i++) {
							if(csv.get(csvGroupEntry.getKey()).get(i).equals(groupValue)) {
								values.add(value.get(i));
							}
						}
						//values.addAll(value);
						mapping.put(entry.getValue(), values);
					}

					result.add(mapping);
				}
			}

		} catch (Exception e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public String getSeparatorChar() {
		return separatorChar;
	}

	public void setSeparatorChar(String separatorChar) {
		this.separatorChar = separatorChar;
	}

	public String getWithHeader() {
		return withHeader;
	}

	public void setWithHeader(String withHeader) {
		this.withHeader = withHeader;
	}
}
