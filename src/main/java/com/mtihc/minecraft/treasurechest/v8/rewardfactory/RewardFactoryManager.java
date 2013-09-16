package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory.CreateCallback;

/**
 * The manager class that makes rewards work.
 * 
 * <p>This class represents a collection of reward factories. 
 * You can use the <code>create</code> methods to 
 * automatically search for the correct factory and create a reward.</p>
 * 
 * @author Mitch
 *
 */
public class RewardFactoryManager {
	
	private Map<String, RewardFactory> factories = new LinkedHashMap<String, RewardFactory>();
	
	/**
	 * Constructor.
	 */
	public RewardFactoryManager() {
		
	}
	
	/**
	 * Returns whether this manager has a factory with the specified label.
	 * @param label the label that represents a reward type
	 * @return true if the factory exists, false otherwise
	 */
	public boolean hasFactory(String label) {
		return factories.containsKey(label);
	}
	
	/**
	 * Returns the factory with the specified label. Or null.
	 * @param label the label that represents a reward type
	 * @return the reward factory or null
	 */
	public RewardFactory getFactory(String label) {
		return factories.get(label);
	}
	
	/**
	 * Register a reward factory
	 * @param factory the reward factory
	 */
	public void setFactory(RewardFactory factory) {
		this.factories.put(factory.getLabel(), factory);
	}
	
	/**
	 * Returns the labels of all registered factories.
	 * @return the labels of all registered factories.
	 */
	public String[] getFactoryLabels() {
		Set<String> keys = factories.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	/**
	 * Returns the total amount of registered factories
	 * @return the total amount of registered factories
	 */
	public int getFactoryTotal() {
		return factories.size();
	}

	/**
	 * Create a reward using reward info.
	 * @param info the reward info
	 * @return the reward
	 * @throws RewardException thrown 	thrown when there is no factory with the specified label, 
	 * 									or when the reward could not be created
	 */
	public IReward create(RewardInfo info) throws RewardException {
		RewardFactory f = getFactory(info.getLabel());
		if(f == null) {
			throw new RewardException("There is no factory for reward type \"" + info.getLabel() + "\".");
		}
		return f.createReward(info);
	}
	
	/**
	 * Create a reward using commands.
	 * @param sender the command sender
	 * @param label the label that represents a reward type
	 * @param args the command arguments
	 * @param callback the callback object, that is used to return a reward and catch errors
	 * @throws RewardException thrown when there is no factory with the specified label
	 */
	public void create(CommandSender sender, String label, String[] args, CreateCallback callback) throws RewardException {
		RewardFactory f = factories.get(label);
		if(f == null) {
			throw new RewardException("There is no factory for reward type \"" + label + "\".");
		}
		f.createReward(sender, args, callback);
	}
	
	
	
}
