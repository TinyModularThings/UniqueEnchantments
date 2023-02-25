package uniquebase.api.jei;

import java.util.List;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ItemTarget extends EnchantmentTarget
{
	Predicate<ItemStack> filter;
	
	public ItemTarget(Component description, Enchantment ench, Predicate<ItemStack> filter)
	{
		super(description, ench);
		this.filter = filter;
	}

	@Override
	public List<ItemStack> getItems(List<ItemStack> itemPool)
	{
		List<ItemStack> result = new ObjectArrayList<>();
		for(int i = 0,m=itemPool.size();i<m;i++) {
			ItemStack stack = itemPool.get(i);
			if(filter.test(stack)) result.add(stack);
		}
		return result;
	}
}
