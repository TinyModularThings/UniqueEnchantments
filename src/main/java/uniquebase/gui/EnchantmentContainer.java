package uniquebase.gui;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import uniquebase.utils.MiscUtil;

public class EnchantmentContainer extends AbstractContainerMenu
{
	Container inventory;
	List<ItemStack> enchantments = new ObjectArrayList<>();
	
	public EnchantmentContainer(ItemStack stack)
	{
		super(null, -1);
		for(Entry<Enchantment, Integer> entry : EnchantmentHelper.getEnchantments(stack).entrySet())
		{
			ItemStack item = new ItemStack(Items.ENCHANTED_BOOK);
			EnchantedBookItem.addEnchantment(item, new EnchantmentInstance(entry.getKey(), entry.getValue()));
			addToolTip(item, ChatFormatting.DARK_AQUA.toString()+I18n.get("unique.base.jei.max_level", ChatFormatting.WHITE.toString()+entry.getKey().getMaxLevel()));
			addToolTip(item, ChatFormatting.GOLD.toString()+I18n.get("unique.base.jei.treasure")+(entry.getKey().isTreasureOnly() ? ChatFormatting.GREEN.toString()+"Yes" : ChatFormatting.RED.toString()+"No"));
			addToolTip(item, ChatFormatting.DARK_RED.toString()+I18n.get("unique.base.jei.curse")+(entry.getKey().isCurse() ? ChatFormatting.RED.toString()+"Yes" : ChatFormatting.GREEN.toString()+"No"));
			addToolTip(item, ChatFormatting.DARK_PURPLE.toString()+I18n.get("unique.base.jei.rarity", MiscUtil.getFormatting(entry.getKey().getRarity())+I18n.get("unique.base.jei."+entry.getKey().getRarity().name().toLowerCase())));
			enchantments.add(item);
		}
		inventory = new SimpleContainer(enchantments.size()+1);
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
	
	@Override
	public ItemStack quickMoveStack(Player player, int slot)
	{
		return ItemStack.EMPTY;
	}
	
	public static void addToolTip(ItemStack stack, String text)
	{
		CompoundTag nbt = stack.getOrCreateTagElement("display");
		ListTag list = nbt.getList("Lore", 8);
		list.add(StringTag.valueOf(Component.Serializer.toJson(Component.literal(text))));
		nbt.put("Lore", list);
	}
	
	@Override
	public boolean stillValid(Player playerIn)
	{
		return true;
	}
	
	public static class LockedSlot extends Slot
	{
		public LockedSlot(Container inventoryIn, int index, int xPosition, int yPosition)
		{
			super(inventoryIn, index, xPosition, yPosition);
		}
		
		@Override
		public boolean mayPlace(ItemStack stack)
		{
			return false;
		}
		
		@Override
		public boolean mayPickup(Player playerIn)
		{
			return false;
		}
	}
}
