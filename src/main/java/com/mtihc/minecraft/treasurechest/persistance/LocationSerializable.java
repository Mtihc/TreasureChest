package com.mtihc.minecraft.treasurechest.persistance;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.Vector;

public class LocationSerializable implements ConfigurationSerializable {

	private Location location;
	
	public LocationSerializable(Location location) {
		this.location = location;
	}
	
	public LocationSerializable(Map<String, Object> map) {
		location = toLocation(map);
	}
	
	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public Map<String, Object> serialize() {
		return toMap(location);
	}

	public static LocationSerializable deserialize(Map<String, Object> map) {
		return new LocationSerializable(map);
	}
	
	private Location toLocation(Map<String, Object> map) {
		Vector vec = (Vector) map.get("vec");
		float yaw = Float.parseFloat(map.get("yaw").toString());
		float pitch = Float.parseFloat(map.get("pitch").toString());
		String worldName = map.get("world").toString();
		return new Location(Bukkit.getWorld(worldName), vec.getX(), vec.getY(), vec.getZ(), yaw, pitch);
	}
	
	private Map<String, Object> toMap(Location loc) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("vec", loc.toVector());
		map.put("yaw", loc.getYaw());
		map.put("pitch", loc.getPitch());
		map.put("world", loc.getWorld().getName());
		return map;
	}
	
}
