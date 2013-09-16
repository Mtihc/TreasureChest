package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

/**
 * Class that represents reward info. This can be saved/loaded. 
 * 
 * <p>Reward factories need objects of this type to create rewards. </p>
 * 
 * @author Mitch
 *
 */
@SerializableAs("RewardInfo")
public class RewardInfo implements ConfigurationSerializable {

	private String label;
	private Map<String, Object> data;

	/**
	 * Constructor.
	 * 
	 * @param label the label that represents the reward type
	 * @param data the data
	 */
	public RewardInfo(String label, Map<String, Object> data) {
		this.label = label;
		this.data = data;
	}
	
	/**
	 * Constructor. The label should be in the data, at key "label".
	 * 
	 * @param data the data
	 */
	private RewardInfo(Map<String, Object> data) {
		this.label = (String) data.get("label");
		
		Map<?, ?> dataSection = (Map<?, ?>) data.get("data");
		Set<?> dataEntries = (Set<?>) dataSection.entrySet();
		this.data = new LinkedHashMap<String, Object>();
		for (Object object : dataEntries) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) object;
			this.data.put((String) entry.getKey(), entry.getValue());
		}
		
	}
	
	/**
	 * Deserialize the reward info object.
	 * @param values the loaded values
	 * @return the created reward info
	 */
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

	/**
	 * The label that represents the reward type.
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Set some data
	 * @param key the key string
	 * @param value the value
	 */
	public void setData(String key, Object value) {
		if(value == null) {
			data.remove(key);
		}
		else {
			data.put(key, value);
		}
	}
	
	/**
	 * Get some data
	 * @param key the key string
	 * @return the value
	 */
	public Object getData(String key) {
		return data.get(key);
	}

}
