package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.ICustomItemEnchantment;

@OnlyIn(Dist.CLIENT)
public class WrappedEnchantment implements Comparable<WrappedEnchantment>
{
	Enchantment ench;
	List<Enchantment> incompats = new ObjectArrayList<>();
	List<ItemStack> validItems = new ObjectArrayList<>();
	int pageIndex = 0;
	GuiButtonExt left = new GuiButtonExt(1, 59, 10, 10, "<", T -> {});
	GuiButtonExt right = new GuiButtonExt(54, 59, 10, 10, ">", T -> {});
	
	
	public WrappedEnchantment(Enchantment ench)
	{
		this.ench = ench;
		for(Enchantment entry : ForgeRegistries.ENCHANTMENTS)
		{
			if(ench.isCompatibleWith(entry) && entry.isCompatibleWith(ench)) continue;
			if(ench == entry) continue;
			incompats.add(entry);
		}
		for(Item item : ForgeRegistries.ITEMS)
		{
			NonNullList<ItemStack> temp = NonNullList.create();
			item.fillItemGroup(ItemGroup.SEARCH, temp);
			for(int i = 0,m=temp.size();i<m;i++)
			{
				if(ench.canApply(temp.get(i))) validItems.add(temp.get(i));
			}
		}
		if(ench instanceof ICustomItemEnchantment)
		{
			((ICustomItemEnchantment)ench).addCustomItems(validItems);
		}
		validItems.removeIf(ItemStack::isEmpty);
	}
	
	@Override
	public String toString()
	{
		return "Wrapped Enchantment: "+ench.getRegistryName().toString();
	}
	
	public String getDescription()
	{
		String s = I18n.format("enchantment."+ench.getRegistryName().getNamespace()+"."+ench.getRegistryName().getPath()+".desc");
		if(s.startsWith("enchantment.")) return I18n.format("unique.base.jei.no.description");
		return s;
	}
	
	public TextFormatting getFormatting(Rarity rarity)
	{
		switch(rarity)
		{
			case COMMON: return TextFormatting.WHITE;
			case RARE: return TextFormatting.AQUA;
			case UNCOMMON: return TextFormatting.YELLOW;
			case VERY_RARE: return TextFormatting.LIGHT_PURPLE;
			default: return TextFormatting.OBFUSCATED;
		}
	}
	@OnlyIn(Dist.CLIENT)
	public List<String> getIncompats(FontRenderer font)
	{
		List<String> list = new ObjectArrayList<>();
		if(incompats.isEmpty())
		{
			list.addAll(font.listFormattedStringToWidth("- "+I18n.format("unique.base.jei.no.incompat"), 122));
		}
		else
		{
			for(Enchantment ench : incompats)
			{
				list.addAll(font.listFormattedStringToWidth("- "+I18n.format(ench.getName()), 122));
			}
		}
		return list;
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