package uniquebase.gui;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class EnchantmentContainer extends Container
{
	IInventory inventory;
	List<ItemStack> enchantments = new ObjectArrayList<>();
	
	public EnchantmentContainer(ItemStack stack)
	{
		super(null, -1);
		for(Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet())
		{
			ItemStack item = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(item, new EnchantmentData(entry.getKey(), entry.getValue()));
			addToolTip(item, TextFormatting.DARK_AQUA.toString()+I18n.get("unique.base.jei.max_level", TextFormatting.WHITE.toString()+entry.getKey().getMaxLevel()));
			addToolTip(item, TextFormatting.GOLD.toString()+I18n.get("unique.base.jei.treasure")+(entry.getKey().isTreasureOnly() ? TextFormatting.GREEN.toString()+"Yes" : TextFormatting.RED.toString()+"No"));
			addToolTip(item, TextFormatting.DARK_RED.toString()+I18n.get("unique.base.jei.curse")+(entry.getKey().isCurse() ? TextFormatting.RED.toString()+"Yes" : TextFormatting.GREEN.toString()+"No"));
			addToolTip(item, TextFormatting.DARK_PURPLE.toString()+I18n.get("unique.base.jei.rarity", getFormatting(entry.getKey().getRarity())+I18n.get("unique.base.jei."+entry.getKey().getRarity().name().toLowerCase())));
			enchantments.add(item);
		}
		inventory = new Inventory(enchantments.size()+1);
		inventory.setItem(0, stack);
		for(int i = 0;i<enchantments.size();i++)
		{
			inventory.setItem(i+1, enchantments.get(i));
		}
		addSlot(new LockedSlot(inventory, 0, 16, 16));
		for(int i = 0;i<enchantments.size();i++)
		{
			int x = i % 8;
			int y = i / 8;
			addSlot(new LockedSlot(inventory, i+1, 17+x*18, 37+y*18));
		}
	}
	
	private TextFormatting getFormatting(Rarity rarity)
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
	
	public static void addToolTip(ItemStack stack, String text)
	{
		CompoundNBT nbt = stack.getOrCreateTagElement("display");
		ListNBT list = nbt.getList("Lore", 8);
		list.add(StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(text))));
		nbt.put("Lore", list);
	}
	
	@Override
	public boolean stillValid(PlayerEntity playerIn)
	{
		return true;
	}
	
	public static class LockedSlot extends Slot
	{
		public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}
		
		@Override
		public boolean mayPickup(PlayerEntity playerIn)
		{
			return false;
		}
	}
}
