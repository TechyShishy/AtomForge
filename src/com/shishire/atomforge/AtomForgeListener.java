package com.shishire.atomforge;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class AtomForgeListener implements Listener {

	@EventHandler
	public void onSignChange(SignChangeEvent event)
	{
		@SuppressWarnings("unused")
		Logger log = Logger.getLogger("AtomForge");
		// First line should look like this "[AtomForge]
		if(event.getLine(0).equals("[AtomForge]"))
		{
			String inputItemString = event.getLine(1);
			String outputItemString = event.getLine(2);
			@SuppressWarnings("unused")
			ItemStack inputItem = null;
			@SuppressWarnings("unused")
			ItemStack outputItem = null;
			try {
				inputItem = parseItem(inputItemString);
				outputItem = parseItem(outputItemString);
			}
			catch (UnparseableMaterialException e)
			{
				event.getPlayer().sendMessage(ChatColor.RED + "Invalid AtomForge Sign.");
				event.setCancelled(true);
			}
			/*
			log.info(String.valueOf(inputItem.getQuantity()));
			log.info(String.valueOf(inputItem.getMaterial()));
			log.info(String.valueOf(outputItem.getQuantity()));
			log.info(String.valueOf(outputItem.getMaterial()));*/
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if(!event.hasBlock())
			return;
		Block block = event.getClickedBlock();
		if(!(block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)))
			return;
		Sign sign = (Sign) block.getState();
		if(!sign.getLine(0).equals("[AtomForge]"))
			return;
		
		if(event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;
		event.setCancelled(true);
		ItemStack input = parseItem(sign.getLine(1));
		ItemStack output = parseItem(sign.getLine(2));
		Player player = event.getPlayer();
		PlayerInventory inv = player.getInventory();
		if(!containsItem(inv, input))
			return;
		@SuppressWarnings("unused")
		Integer amountRemoved = new Integer(removeItem(inv, input));
		inv.addItem(output);
		player.updateInventory();
		
	}

	private ItemStack parseItem(String inputItemString) {
		Pattern quantItem = Pattern.compile("^(\\d+?)\\s+?(\\w+\\s*\\w*):?(\\d*?)$");
		Matcher matcher = quantItem.matcher(inputItemString);
		if(matcher.find())
		{
			Integer quantity = Integer.parseInt(matcher.group(1), 10);
			Material material = Material.matchMaterial(matcher.group(2));
			if(quantity == null || material == null)
				throw new UnparseableMaterialException();
			String group3 = matcher.group(3);
			if(group3.isEmpty())
				group3 = "0";
			Byte data = Byte.parseByte(group3, 10);
			
			
			MaterialData materialData = new MaterialData(material, data.byteValue());
			return materialData.toItemStack(quantity);
			//return new InputOutputItem(quantity, material);
		}
		else
		{
			throw new UnparseableMaterialException();
		}
		
	}
	
	/**
	 * @author Pasukaru
	 * @author Shishire
	 * @param inventory A Player Inventory
	 * @param id The id of the item to remove
	 * @param meta Data Value, allowing you to remove only a specific type of a material
	 * @param quantity Total amount of items removed
	 * @return Total Amount of Material which could not be removed (probably due to the specified inventory not containing enough of the material)
	 * 
	 * Removes the specified number of items from a players inventory, automatically handling multiple stacks, and partial stacks.
	 * Origin credit goes to Pasukaru, at <http://forums.bukkit.org/threads/removing-set-amount-of-an-item-from-players-inventory.40182/#post-727975>
	 * Modified by Shishire.
	 */
	public static int removeItem(Inventory inventory, ItemStack remove) {
		int rest = remove.getAmount();
		int quantity = remove.getAmount();
		
        for( int i = 0 ; i < inventory.getSize() ; i++ ){
            ItemStack stack = inventory.getItem(i);
            if( stack == null || stack.getTypeId() != remove.getTypeId() )
                continue;
        	
            // TODO: This needs to be cleaned, it makes several unsafe assumptions
            if( stack.getType().getMaxDurability() < 0 && !stack.getData().equals(remove.getData()) ){
                continue;
            }
            if( rest >= stack.getAmount() ){
                rest -= stack.getAmount();
                inventory.clear(i);
            } else if( rest>0 ){
                    stack.setAmount(stack.getAmount()-rest);
                    rest = 0;
            } else {
                break;
            }
        }
        return quantity-rest;
    }
	
	public static boolean containsItem(Inventory inventory, ItemStack check) {
		int rest = check.getAmount();
		
        for( int i = 0 ; i < inventory.getSize() ; i++ ){
            ItemStack stack = inventory.getItem(i); 
            if( stack == null || stack.getTypeId() != check.getTypeId() )
                continue;
            // TODO: This needs to be cleaned, it makes several unsafe assumptions
            if( stack.getType().getMaxDurability() < 0 && !stack.getData().equals(check.getData()) ){
                continue;
            }
            if( rest >= stack.getAmount() ){
                rest -= stack.getAmount();
            } else if( rest>0 ){
                    rest = 0;
            } else {
                break;
            }
        }
        return rest == 0;
    }
}
