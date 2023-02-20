package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class FilterEntry
{
	List<ItemStack> display = new ObjectArrayList<>();
	List<ItemStack>[] items = createLists(36);
	Component description;
	
	public FilterEntry(Enchantment enchantment, List<ItemStack> items, Component description)
	{
		for(int i = enchantment.getMinLevel();i<=enchantment.getMaxLevel();i++) {
			display.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i)));
		}
		for(int i = 0,m=items.size();i<m;i++) {
			this.items[i%36].add(items.get(i));
		}
		this.description = description;
	}
	
	@SuppressWarnings("unchecked")
	private static <T> List<T>[] createLists(int amount) {
		List<T>[] items = new List[amount];
		for(int i = 0;i<amount;i++) {
			items[i] = new ObjectArrayList<>();
		}
		return items;
	}

	public List<ItemStack> getDisplay()
	{
		return display;
	}

	public List<ItemStack>[] getItems()
	{
		return items;
	}

	public Component getDescription()
	{
		return description;
	}
	
}
