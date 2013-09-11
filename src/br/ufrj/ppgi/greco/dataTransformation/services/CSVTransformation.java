package br.ufrj.ppgi.greco.dataTransformation.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import br.ufrj.ppgi.greco.dataTransformation.interfaces.ITransformation;
import br.ufrj.ppgi.greco.util.Timer;

public class CSVTransformation implements ITransformation {

	private static final String FILTER_CHAR = "/";
	private String separatorChar;
	private String withHeader;
	private Map<String, String> headers;
	private final Map<Integer, String> groupMap = new HashMap<Integer,String>();
	final Logger log = LoggerFactory.getLogger(CSVTransformation.class);
	
	public CSVTransformation(String separatorChar, String withHeader, Map<String, String> headers) {
		this.separatorChar = separatorChar;
		this.withHeader = withHeader;
		this.headers = headers;
	}

	@Override
	public ArrayList<HashMap<String, ArrayList<String>>> getValue(String data, String group, HashMap<String, String> transformationsIndexedByLabel) {
		log.info("Starting CSV transformation...");
		ArrayList<HashMap<String, ArrayList<String>>> result = new ArrayList<HashMap<String, ArrayList<String>>>();
		Timer t = new Timer();
		try {
			Character separator = ',';
			if (this.separatorChar != null && !"".equals(this.separatorChar) && this.separatorChar.length() == 1) {
				separator = this.separatorChar.charAt(0);
			}
			CSVReader reader = new CSVReader(new StringReader(data), separator);
			
			Map<String, List<ColunaValor<String, String>>> map = new LinkedHashMap<String, List<ColunaValor<String, String>>>();

			if (hasHeader()) {
				configureHeader(reader.readNext());
			}
			List<String> groupList = Arrays.asList(group.split(separator.toString()));
			ArrayList<Integer> groupIndexList = buildGroupMap(groupList);
			
			Collection<String> usedColumns = transformationsIndexedByLabel.keySet();
			ArrayList<Integer> usedColumnsIndex= asColumnIndexList(usedColumns);
			
			List<String> lineList = null;
			String[] line = reader.readNext();
			while (line != null) {
				lineList = Arrays.asList(line);
				line = reader.readNext();
				
				boolean filterValues = false;
				
				StringBuilder mapKey = new StringBuilder();
				for (Integer groupIndexColumn : groupIndexList) {
					String value = lineList.get(groupIndexColumn - 1);
					if(hasValueFilter(this.groupMap.get(groupIndexColumn))) {
						String g = this.groupMap.get(groupIndexColumn);
						
						String filter = g.substring(g.indexOf(FILTER_CHAR)+1);
						
						if(value.equals(filter)) {
							mapKey.append(this.separatorChar).append(value);
						} else {
							filterValues = true;
							break;
						}
					} else {
						mapKey.append(this.separatorChar).append(value);
					}
				}
				if(!filterValues) {
					ArrayList<ColunaValor<String, String>> values = new ArrayList<ColunaValor<String, String>>();
					for (Integer usedColumnIndex : usedColumnsIndex) {
						if(lineList.size() > usedColumnIndex - 1) {
							values.add(new ColunaValor<String, String>(usedColumnIndex.toString(), lineList.get(usedColumnIndex - 1)));
						}
					}
					
					String key = mapKey.substring(1);
					if(map.containsKey(key)) {
						map.get(key).addAll(values);
					} else {
						map.put(key, values);
					}
				}
			}
			//Transform
			for (Entry<String, List<ColunaValor<String, String>>> groupEntry : map.entrySet()) {
				HashMap<String, ArrayList<String>> mapping = new HashMap<String, ArrayList<String>>();
				for (Entry<String, String> transEntry : transformationsIndexedByLabel.entrySet()) {
					ArrayList<String> values = new ArrayList<String>();
					for (ColunaValor<String, String> groupValue : groupEntry.getValue()) {
						
						String key = transEntry.getKey();
						if(hasValueFilter(key)) {
							String filter = key.substring(key.indexOf(FILTER_CHAR)+1); 
							key = key.substring(0,key.indexOf(FILTER_CHAR));
							if (groupValue.column.equals(asIndex(key).toString()) && filter.equals(groupValue.value)) {
								values.add(groupValue.value);
							}
						} else {
							if (groupValue.column.equals(asIndex(transEntry.getKey()).toString())) {
								values.add(groupValue.value);
							}
						}
						
					}
					mapping.put(transEntry.getValue(), values);
				}
				result.add(mapping);
			}

		} catch (IOException e) {
			log.error("CSV Transformation problem.",e);
		}
		log.info("Transformação CSV concluída. {} linhas em {}ms", result.size(), t.getTime());
		return result;
	}

	private ArrayList<Integer> buildGroupMap(List<String> groupList) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (String groupItem : groupList) {
			String groupItemWithFilter = groupItem;
			if(hasValueFilter(groupItem)){
				groupItem = groupItem.substring(0, groupItem.indexOf(FILTER_CHAR));
			}
			
			Integer index = null;
			if (hasHeader()) {
				index = asIndex(groupItem);
				result.add(index);
				this.groupMap.put(index, groupItemWithFilter);
			} else {
				index = Integer.parseInt(groupItem);
				result.add(index);
				this.groupMap.put(index, groupItemWithFilter);
			}
		}
		return asColumnIndexList(groupList);
	}

	private boolean hasValueFilter(String key) {
		return key.contains(FILTER_CHAR);
	}

	private boolean hasHeader() {
		return this.withHeader!= null && ("true".equalsIgnoreCase(this.withHeader) || "1".equals(this.withHeader));
	}
	
	private void configureHeader(String[] header) {
		Map<String, String> newHeader = new HashMap<String, String>();
		for (Entry<String, String> entry : this.headers.entrySet()) {
			newHeader.put(entry.getKey(), entry.getValue());
			newHeader.put(entry.getValue(), entry.getKey());
		}
		this.headers = newHeader;
	}

	private ArrayList<Integer> asColumnIndexList(Collection<String> comumnList) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (String groupItem : comumnList) {
			if(hasValueFilter(groupItem)){
				groupItem = groupItem.substring(0, groupItem.indexOf(FILTER_CHAR));
			}
			if (hasHeader()) {
				result.add(asIndex(groupItem));
			} else {
				result.add(Integer.parseInt(groupItem));
			}
		}
		return result;
	}
	
	private Integer asIndex(String header) {
		String h;
		if(!hasHeader()) {
			h = header; 
		} else {
			h = this.headers.get(header);
		}
		Integer hindex = null;
		if(h != null) {
			try {
				hindex = Integer.parseInt(h);
			} catch (NumberFormatException e) {
				hindex = Integer.parseInt(header);
			}
		}
		return hindex;
	}

	public String getSeparatorChar() {
		return separatorChar;
	}

	public String getWithHeader() {
		return withHeader;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	private class ColunaValor<C, V>{
		private final C column;
		private final V value;
		public ColunaValor(final C column, final V value) {
			this.column = column;
			this.value = value;
		}
		@Override
		public String toString() {
			return "<"+column+","+value+">";
		}
	}
}
