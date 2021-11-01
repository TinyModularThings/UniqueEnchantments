package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import uniquebase.api.ICustomItemEnchantment;

public class WrappedEnchantment implements Comparable<WrappedEnchantment>
{
	Enchantment ench;
	List<Enchantment> incompats = new ObjectArrayList<>();
	List<ItemStack> validItems = new ObjectArrayList<>();
	
	public WrappedEnchantment(Enchantment ench)
	{
		this.ench = ench;
		for(Enchantment entry : ForgeRegistries.ENCHANTMENTS)
		{
			if(ench.isCompatibleWith(entry) && entry.isCompatibleWith(ench)) continue;
			incompats.add(entry);
		}
		for(Item item : ForgeRegistries.ITEMS)
		{
			NonNullList<ItemStack> temp = NonNullList.create();
			item.getSubItems(CreativeTabs.SEARCH, temp);
			for(int i = 0,m=temp.size();i<m;i++)
			{
				if(ench.canApply(temp.get(i))) validItems.add(temp.get(i));
			}
		}
		if(ench instanceof ICustomItemEnchantment)
		{
			((ICustomItemEnchantment)ench).addCustomItems(validItems);
		}
	}
	
	public String getName()
	{
		return I18n.format(ench.getName());
	}
	
	@Override
	public int compareTo(WrappedEnchantment o)
	{
		return getName().compareToIgnoreCase(o.getName());
	}
}