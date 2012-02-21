package com.mtihc.minecraft.treasurechest.persistance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.bukkit.plugin.Plugin;

import com.mtihc.minecraft.treasurechest.util.Db;

public class MemoryDb extends Db {

	private static final String TABLE = "memory";
	private static final String PLAYER = "player";
	private static final String FOUND = "found";
	private static final String TIME = "time";
	
	public MemoryDb(Plugin plugin) {
		super(plugin, plugin.getDataFolder() + "/memory.db");
		query("CREATE TABLE IF NOT EXISTS " + TABLE + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + PLAYER + " TEXT, " + FOUND + " TEXT, " + TIME + " REAL)");
	}
	
	public List<String> getAllPlayerFound(String playerName) {
		String lower = playerName.toLowerCase();
		ResultSet resultSet = query("SELECT " + FOUND + " FROM " + TABLE + " WHERE " + PLAYER + " = " + "'" + lower + "'", true);
		List<String> result = new ArrayList<String>();
		if(resultSet != null) {
			boolean hasNext;
			try {
				hasNext = resultSet.next();
			} catch (SQLException e) {
				return result;
			}
			while(hasNext) {
				
				String found;
				try {
					found = resultSet.getString(1);
				} catch (SQLException exception) {
					continue;
				}
				result.add(found);
				try {
					hasNext = resultSet.next();
				} catch (SQLException exception) {
					return result;
				}
			}
			try {
				resultSet.close();
			} catch (SQLException e) {
				// dan niet he
			}
		}
		return result;
	}
	
	public long whenHasPlayerFound(String playerName, String chestName) {
		String playerLower = playerName.toLowerCase();
		String chestLower = chestName.toLowerCase();
		ResultSet resultSet = query("SELECT " + TIME + " FROM " + TABLE + " WHERE " + PLAYER + " = " + "'" + playerLower + "'" + " AND " + FOUND + " = " + "'" + chestLower + "'", true);
		try {
			if(resultSet.next()) {
				long result = resultSet.getLong(1);
				resultSet.close();
				return result;
			}
			else {
				return 0;
			}
		} catch(SQLException e) {
			return 0;
		}
	}
	
	public boolean hasPlayerFound(String playerName, String chestName) {
		String playerLower = playerName.toLowerCase();
		String chestLower = chestName.toLowerCase();
		ResultSet resultSet = query("SELECT " + PLAYER + " FROM " + TABLE + " WHERE " + PLAYER + " = " + "'" + playerLower + "'" + " AND " + FOUND + " = " + "'" + chestLower + "'", true);
		try {
			boolean result = resultSet.next();
			resultSet.close();
			return result;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void rememberPlayerFound(String playerName, String chestName) {
		String playerLower = playerName.toLowerCase();
		String chestLower = chestName.toLowerCase();
		long time = Calendar.getInstance().getTimeInMillis();
		query("INSERT OR IGNORE INTO " + TABLE + " (" + PLAYER + "," + FOUND + "," + TIME + ")" + " VALUES('" + playerLower + "','" + chestLower + "'," + time + ")", true);
		query("UPDATE " + TABLE + " SET " + TIME + " = " + time + " WHERE " + PLAYER + " = '" + playerLower + "' AND " + FOUND + " = '" + chestLower + "'");
	}
	
	public void forgetPlayerFound(String playerName, String chestName) {
		String playerLower = playerName.toLowerCase();
		String chestLower = chestName.toLowerCase();
		query("DELETE FROM " + TABLE + " WHERE " + PLAYER + " = " + "'" + playerLower + "'" + " AND " + FOUND + " = " + "'" + chestLower + "'", true);
	}
	
	public void forgetPlayerFoundAll(String playerName) {
		String playerLower = playerName.toLowerCase();
		query("DELETE FROM " + TABLE + " WHERE " + PLAYER + " = " + "'" + playerLower + "'", true);
	}
	
	public void forgetChest(String chestName) {
		String chestLower = chestName.toLowerCase();
		query("DELETE FROM " + TABLE + " WHERE " + FOUND + " = " + "'" + chestLower + "'", true);
	}
}
