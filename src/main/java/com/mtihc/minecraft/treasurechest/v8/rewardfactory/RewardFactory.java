package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

import org.bukkit.command.CommandSender;

/**
 * Abstract base class for new reward factories. You can extend this class.
 * 
 * <p>Most reward factories also require a class that implements the reward interface.</p>
 * 
 * @author Mitch
 *
 */
public abstract class RewardFactory {

	/**
	 * Constructor.
	 */
	public RewardFactory() {
		
	}

	/**
	 * The label that represents the reward type
	 * 
	 * @return the label
	 */
	public abstract String getLabel();
	
	/**
	 * Returns a general description of the rewards this factory creates.
	 * 
	 * <p>For example: "Some amount of health"</p>
	 * 
	 * <p>The actual reward usually has a more detailed description.</p>
	 * 
	 * @return the general descriptio
	 */
	public abstract String getGeneralDescription();
	
	/**
	 * Create a reward using reward info.
	 * @param info the reward info
	 * @return the reward
	 * @throws RewardException thrown when the reward could not be created
	 */
	public abstract IReward createReward(
			RewardInfo info) throws RewardException;
	
	/**
	 * Create a reward using commands.
	 * 
	 * <p>This often requires multiple commands. 
	 * Bukkit's conversation API can be useful.</p>
	 * 
	 * <p>When the reward is finally created, <code>onCreate</code> is called on parameter callback. 
	 * And <code>onCreateException</code> should be called when an error occurs.</p>
	 * 
	 * @param sender the command sender
	 * @param args the command arguments
	 * @param callback the callback object for returning the reward
	 */
	public abstract void createReward(
			CommandSender sender, 
			String[] args, 
			CreateCallback callback);


	/**
	 * Returns command argument syntax.
	 * @return command argument syntax.
	 */
	public abstract String args();
	
	/**
	 * Returns command help messages.
	 * @return command help messages.
	 */
	public abstract String[] help();
	

	/**
	 * Interface that is used when creating a reward with commands. 
	 * 
	 * <p>You can create a new instance of this interface, using inline code.</p>
	 * 
	 * <pre>
	 * 	CreateCallback callback = new CreateCallback() {
	 *		&#64;Override
	 *		public void onCreateException(CommandSender sender, String[] args,
	 *				RewardException e) {
	 *			// error!
	 *			sender.sendMessage(ChatColor.RED + e.getMessage());
	 *		}
	 *		&#64;Override
	 *		public void onCreate(CommandSender sender, String[] args, IReward reward) {
	 *			// reward created!
	 *			sender.sendMessage(ChatColor.GOLD + reward.getDescription());
	 *		}
	 *	};
	 * </pre>
	 * 
	 * @author Mitch
	 *
	 */
	public interface CreateCallback {
		void onCreate(CommandSender sender, String[] args, IReward reward);
		void onCreateException(CommandSender sender, String[] args, RewardException e);
	}

	
}
