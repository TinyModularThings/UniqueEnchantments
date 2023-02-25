package uniquebase.api.jei;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public abstract class EnchantmentTarget
{
	Component description;
	Enchantment ench;
	
	public EnchantmentTarget(Component description, Enchantment ench)
	{
		this.description = description;
		this.ench = ench;
	}

	public Component getDescription()
	{
		return description;
	}
	
	public Enchantment getEnchantment()
	{
		return ench;
	}
	
	public abstract List<ItemStack> getItems(List<ItemStack> itemPool);
}
