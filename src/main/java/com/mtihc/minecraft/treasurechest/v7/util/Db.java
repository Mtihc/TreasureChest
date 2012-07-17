package com.mtihc.minecraft.treasurechest.v7.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;

public class Db
{
	private final Plugin plugin;
	private final String url;
	private Logger log;
	
	/**
	 * Connect to a MySQL database
	 * 
	 * @param plugin the plugin handle
	 * @param host MySQL server address
	 * @param database MySQL database name
	 * @param user MySQL access username
	 * @param password MySQL access password
	 */
	public Db(final Plugin plugin, final String host, final String database, final String user, final String password)
	{
		this.plugin = plugin;
		url = "jdbc:mysql://" + host + "/" + database + "?user=" + user + "&password=" + password;
		log = plugin.getServer().getLogger();
		
		initDriver("com.mysql.jdbc.Driver");
	}
	
	/**
	 * Connect/create a SQLite database
	 * 
	 * @param plugin the plugin handle
	 * @param filePath database storage path/name.extension
	 */
	public Db(final Plugin plugin, final String filePath)
	{
		this.plugin = plugin;
		url = "jdbc:sqlite:" + new File(filePath).getAbsolutePath();
		log = plugin.getServer().getLogger();
		
		File file = plugin.getDataFolder();
		// create config file if it doesn't exist
		if (!file.exists()) {
			try {
				file.mkdir();

			} catch (Exception e) {
				// file could not be created
				e.printStackTrace();
			}
		}
		
		initDriver("org.sqlite.JDBC");
	}
	
	private void initDriver(final String driver)
	{
		try
		{
			Class.forName(driver);
		}
		catch(final Exception e)
		{
			log.severe("Database driver error:" + e.getMessage());
		}
	}
	
	/**
	 * Sends a query to the SQL
	 * Returns a ResultSet if there's anything to return
	 * 
	 * @param query the SQL query string
	 * @return ResultSet or null
	 */
	public ResultSet query(final String query)
	{
		return query(query, false);
	}
	
	/**
	 * Sends a query to the SQL
	 * Returns a ResultSet if there's anything to return
	 * 
	 * @param query the SQL query string
	 * @param retry set to true to retry query if locked
	 * @return ResultSet or null
	 */
	public ResultSet query(final String query, final boolean retry)
	{
		try
		{
			final Connection connection = DriverManager.getConnection(url);
			final PreparedStatement statement = connection.prepareStatement(query);
			
			if(statement.execute())
				return statement.getResultSet();
		}
		catch(final SQLException e)
		{
			final String msg = e.getMessage();
			
			log.severe("Database query error: " + msg);
			
			if(retry && msg.contains("_BUSY"))
			{
				log.severe("Retrying query...");
				
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
				{
					@Override
					public void run()
					{
						query(query);
					}
				}, 10);
			}
		}
		
		return null;
	}
}