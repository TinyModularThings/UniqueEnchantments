package uniquebase.gui;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.TextFormatting;

public class EnchantmentContainer extends Container
{
	IInventory inventory;
	List<ItemStack> enchantments = new ObjectArrayList<>();
	
	public EnchantmentContainer(ItemStack stack)
	{
		for(Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet())
		{
			ItemStack item = new ItemStack(Items.ENCHANTED_BOOK);
			ItemEnchantedBook.addEnchantment(item, new EnchantmentData(entry.getKey(), entry.getValue()));
			addToolTip(item, TextFormatting.DARK_AQUA.toString()+I18n.format("unique.base.jei.max_level", TextFormatting.WHITE.toString()+entry.getKey().getMaxLevel()));
			addToolTip(item, TextFormatting.GOLD.toString()+I18n.format("unique.base.jei.treasure")+(entry.getKey().isTreasureEnchantment() ? TextFormatting.GREEN.toString()+"Yes" : TextFormatting.RED.toString()+"No"));
			addToolTip(item, TextFormatting.DARK_RED.toString()+I18n.format("unique.base.jei.curse")+(entry.getKey().isCurse() ? TextFormatting.RED.toString()+"Yes" : TextFormatting.GREEN.toString()+"No"));
			addToolTip(item, TextFormatting.DARK_PURPLE.toString()+I18n.format("unique.base.jei.rarity", getFormatting(entry.getKey().getRarity())+I18n.format("unique.base.jei."+entry.getKey().getRarity().name().toLowerCase())));
			enchantments.add(item);
		}
		inventory = new InventoryBasic("inv", false, enchantments.size()+1);
		inventory.setInventorySlotContents(0, stack);
		for(int i = 0;i<enchantments.size();i++)
		{
			inventory.setInventorySlotContents(i+1, enchantments.get(i));
		}
		addSlotToContainer(new LockedSlot(inventory, 0, 16, 16));
		for(int i = 0;i<enchantments.size();i++)
		{
			int x = i % 8;
			int y = i / 8;
			addSlotToContainer(new LockedSlot(inventory, i+1, 17+x*18, 37+y*18));
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
		NBTTagCompound nbt = stack.getOrCreateSubCompound("display");
		NBTTagList list = nbt.getTagList("Lore", 8);
		list.appendTag(new NBTTagString(TextFormatting.RESET+text+TextFormatting.RESET));
		nbt.setTag("Lore", list);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn)
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
		public boolean isItemValid(ItemStack stack)
		{
			return false;
		}

		@Override
		public boolean canTakeStack(EntityPlayer playerIn)
		{
			return false;
		}
	}
}
