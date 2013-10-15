package com.liyun.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.liyun.dataMinning.UserBaseCF;

public class Stat {
	public static void main(String[] args) throws Exception {
		File f = new File(UserBaseCF.class.getClassLoader().getResource("devices.data").getFile());
		BufferedReader reader = new BufferedReader(new FileReader(f));
		String line = null;
		HashMap<String, List<HashMap<String, Object>>> map = new HashMap<String, List<HashMap<String, Object>>>();
		while ((line = reader.readLine()) != null) {
			String[] data = line.trim().split("\t");
			if (data.length != 2)
				continue;
			if (map.containsKey(data[0])) {
				List<HashMap<String, Object>> user = map.get(data[0]);
				boolean get = false;
				for (HashMap<String, Object> hashMap : user) {
					if (hashMap.get("name").equals(data[1])) {
						hashMap.put("count", (Integer) hashMap.get("count") + 1);
						get = true;
						break;
					}
				}
				if (!get) {
					HashMap<String, Object> apps = new HashMap<String, Object>();
					apps.put("name", data[1]);
					apps.put("count", 1);
					user.add(apps);
				}
			} else {
				List<HashMap<String, Object>> user = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> apps = new HashMap<String, Object>();
				apps.put("name", data[1]);
				apps.put("count", 1);
				user.add(apps);
				map.put(data[0], user);
			}
		}
		Comparator<HashMap<String, Object>> c = new Comparator<HashMap<String, Object>>() {

			@Override
			public int compare(HashMap<String, Object> o1, HashMap<String, Object> o2) {
				return (Integer) o2.get("count") - (Integer) o1.get("count");
			}
		};
		
		HashMap<String, Integer> appCount = new HashMap<String, Integer>();
		for (String key : map.keySet()) {
			List<HashMap<String, Object>> data = map.get(key);
			
			Collections.sort(data, c);
			if (data.size() > 5) {
				data = data.subList(0, 5);
			}

			for (HashMap<String, Object> hashMap : data) {
				if (appCount.containsKey(hashMap.get("name"))) {
					appCount.put((String) hashMap.get("name"), appCount.get(hashMap.get("name")) + 1);
				} else {
					appCount.put((String) hashMap.get("name"), 1);
				}
			}
		}
		BufferedWriter writer=  new BufferedWriter(new FileWriter(new File("e:/result.dat")));
		for (String key : appCount.keySet()) {
			System.out.println(key + "," + appCount.get(key));
			writer.write(key + "," + appCount.get(key)+"\n");
		}
		writer.close();
		reader.close();
	}
}
