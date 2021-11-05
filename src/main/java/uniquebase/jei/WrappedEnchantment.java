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
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.ICustomItemEnchantment;

@OnlyIn(Dist.CLIENT)
public class WrappedEnchantment implements Comparable<WrappedEnchantment>
{
	Enchantment ench;
	List<Enchantment> incompats = new ObjectArrayList<>();
	List<ItemStack> validItems = new ObjectArrayList<>();
	int pageIndex = 0;
	ExtendedButton left = new ExtendedButton(1, 68, 10, 10, new StringTextComponent("<"), T -> {});
	ExtendedButton right = new ExtendedButton(54, 68, 10, 10, new StringTextComponent(">"), T -> {});
	
	
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
			item.fillItemCategory(ItemGroup.TAB_SEARCH, temp);
			for(int i = 0,m=temp.size();i<m;i++)
			{
				if(ench.canEnchant(temp.get(i))) validItems.add(temp.get(i));
			}
		}
		if(ench instanceof ICustomItemEnchantment)
		{
			((ICustomItemEnchantment)ench).addCustomItems(validItems);
		}
	}
	
	public String getDescription()
	{
		String s = I18n.get("enchantment."+ench.getRegistryName().getNamespace()+"."+ench.getRegistryName().getPath()+".desc");
		if(s.startsWith("enchantment.")) return I18n.get("unique.base.jei.no.description");
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
	public List<IReorderingProcessor> getIncompats(FontRenderer font)
	{
		List<IReorderingProcessor> list = new ObjectArrayList<>();
		if(incompats.isEmpty())
		{
			list.addAll(font.split(new StringTextComponent("- ").append(new TranslationTextComponent("unique.base.jei.no.incompat")), 122));
		}
		else
		{
			for(Enchantment ench : incompats)
			{
				list.addAll(font.split(new StringTextComponent("- ").append(new TranslationTextComponent(ench.getDescriptionId())), 122));
			}
		}
		return list;
	}
	
	public String getName()
	{
		return I18n.get(ench.getDescriptionId());
	}
	
	@Override
	public int compareTo(WrappedEnchantment o)
	{
		return getName().compareToIgnoreCase(o.getName());
	}
}