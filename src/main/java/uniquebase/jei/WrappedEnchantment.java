package uniquebase.jei;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.ICustomItemEnchantment;

@OnlyIn(Dist.CLIENT)
public class WrappedEnchantment implements Comparable<WrappedEnchantment>
{
	Enchantment ench;
	List<Enchantment> incompats = new ObjectArrayList<>();
	List<ItemStack> validItems = new ObjectArrayList<>();
	int pageIndex = 0;
	ExtendedButton left = new ExtendedButton(1, 68, 10, 10, Component.literal("<"), T -> {});
	ExtendedButton right = new ExtendedButton(54, 68, 10, 10, Component.literal(">"), T -> {});
	
	
	public WrappedEnchantment(Enchantment ench, List<ItemStack> items)
	{
		this.ench = ench;
		for(Enchantment entry : ForgeRegistries.ENCHANTMENTS)
		{
			if(ench.isCompatibleWith(entry) && entry.isCompatibleWith(ench)) continue;
			if(ench == entry) continue;
			incompats.add(entry);
		}
		for(int i = 0,m=items.size();i<m;i++) {
			ItemStack stack = items.get(i);
			if(ench.canApplyAtEnchantingTable(stack)) validItems.add(stack);
		}
		if(ench instanceof ICustomItemEnchantment)
		{
			((ICustomItemEnchantment)ench).addCustomItems(validItems);
		}
	}
	
	@Override
	public String toString()
	{
		return "Wrapped Enchantment: "+ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
	}
	
	public String getDescription()
	{
		ResourceLocation id = ForgeRegistries.ENCHANTMENTS.getKey(ench);
		String s = I18n.get("enchantment."+id.getNamespace()+"."+id.getPath()+".desc");
		if(s.startsWith("enchantment.")) return I18n.get("unique.base.jei.no.description");
		return s;
	}
	
	public ChatFormatting getFormatting(Rarity rarity)
	{
		switch(rarity)
		{
			case COMMON: return ChatFormatting.WHITE;
			case RARE: return ChatFormatting.AQUA;
			case UNCOMMON: return ChatFormatting.YELLOW;
			case VERY_RARE: return ChatFormatting.LIGHT_PURPLE;
			default: return ChatFormatting.OBFUSCATED;
		}
	}
	@OnlyIn(Dist.CLIENT)
	public List<FormattedCharSequence> getIncompats(Font font)
	{
		List<FormattedCharSequence> list = new ObjectArrayList<>();
		if(incompats.isEmpty())
		{
			list.addAll(font.split(Component.literal("- ").append(Component.translatable("unique.base.jei.no.incompat")), 122));
		}
		else
		{
			for(Enchantment ench : incompats)
			{
				list.addAll(font.split(Component.literal("- ").append(Component.translatable(ench.getDescriptionId())), 122));
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