package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

@SerializableAs("RewardInfo")
public class RewardInfo implements ConfigurationSerializable {

	private String label;
	private Map<String, Object> data;

	public RewardInfo(String label, Map<String, Object> data) {
		this.label = label;
		this.data = data;
	}
	
	private RewardInfo(Map<String, Object> values) {
		this.label = (String) values.get("label");
		
		Map<?, ?> dataSection = (Map<?, ?>) values.get("data");
		Set<?> dataEntries = (Set<?>) dataSection.entrySet();
		this.data = new LinkedHashMap<String, Object>();
		for (Object object : dataEntries) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			this.data.put((String) entry.getKey(), entry.getValue());
		}
		
	}
	
	public static RewardInfo deserialize(Map<String, Object> values) {
		return new RewardInfo(values);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("label", label);
		result.put("data", data);
		
		return result;
	}

	public String getLabel() {
		return label;
	}
	
	public void setData(String key, Object value) {
		if(value == null) {
			data.remove(key);
		}
		else {
			data.put(key, value);
		}
	}
	
	public Object getData(String key) {
		return data.get(key);
	}

}
