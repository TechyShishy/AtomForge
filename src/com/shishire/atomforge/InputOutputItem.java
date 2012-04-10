package com.shishire.atomforge;

import org.bukkit.Material;

public class InputOutputItem {
	private final Integer quantity;
	private final Material material;
	
	public InputOutputItem(Integer quantity, Material material)
	{
		this.quantity = quantity;
		this.material = material;
	}
	
	public Integer getQuantity()
	{
		return this.quantity;
	}
	
	public Material getMaterial()
	{
		return this.material;
	}

}
