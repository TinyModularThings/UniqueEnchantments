package uniquebase.gui;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.utils.MiscUtil;

public class EnchantmentContainer extends AbstractContainerMenu
{
	Container inventory;
	List<ItemStack> enchantments = new ObjectArrayList<>();
	List<ItemStack> applicable = new ObjectArrayList<>();
	int offset = 0;
	boolean applied = true;
	
	public EnchantmentContainer(ItemStack stack)
	{
		super(null, -1);
		Map<Enchantment, Integer> presentEnchantments = EnchantmentHelper.getEnchantments(stack);
		for(Entry<Enchantment, Integer> entry : presentEnchantments.entrySet())
		{
			Enchantment ench = entry.getKey();
			ItemStack item = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, entry.getValue()));
			addToolTip(item, ChatFormatting.DARK_AQUA.toString()+I18n.get("unique.base.jei.max_level", ChatFormatting.WHITE.toString()+ench.getMaxLevel()));
			addToolTip(item, ChatFormatting.GOLD.toString()+I18n.get("unique.base.jei.treasure", ench.isTreasureOnly() ? ChatFormatting.GREEN.toString()+"Yes" : ChatFormatting.RED.toString()+"No"));
			addToolTip(item, ChatFormatting.DARK_RED.toString()+I18n.get("unique.base.jei.curse", ench.isCurse() ? ChatFormatting.RED.toString()+"Yes" : ChatFormatting.GREEN.toString()+"No"));
			addToolTip(item, ChatFormatting.DARK_PURPLE.toString()+I18n.get("unique.base.jei.rarity", MiscUtil.getFormatting(ench.getRarity())+I18n.get("unique.base.jei."+ench.getRarity().name().toLowerCase())));
			addToolTip(item, Component.translatable("unique.base.jei.tradeable", Component.translatable(ench.isTradeable() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xC98414)));
			addToolTip(item, Component.translatable("unique.base.jei.discover", Component.translatable(ench.isDiscoverable() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xBA910D)));
			addToolTip(item, Component.translatable("unique.base.jei.books", Component.translatable(ench.isAllowedOnBooks() ? "unique.base.jei.yes" : "unique.base.jei.no").withStyle(ChatFormatting.WHITE)).withStyle(createColor(0xBA910D)));
			enchantments.add(item);
		}
		addToolTip(stack, ChatFormatting.BLUE+I18n.get("unique.base.jei.enchantability", ChatFormatting.DARK_PURPLE.toString()+stack.getEnchantmentValue()));
		for(Enchantment ench : ForgeRegistries.ENCHANTMENTS)
		{
			if(ench.canEnchant(stack) && EnchantmentHelper.isEnchantmentCompatible(presentEnchantments.keySet(), ench))
			{
				applicable.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, ench.getMinLevel())));
			}
		}
		inventory = new SimpleContainer(49);
		inventory.setItem(0, stack);
		for(int i = 0,m=Math.min(48, enchantments.size());i<m;i++)
		{
			inventory.setItem(i+1, enchantments.get(i));
		}
		addSlot(new LockedSlot(inventory, 0, 16, 16));
		for(int i = 0;i<48;i++)
		{
			int x = i % 8;
			int y = i / 8;
			addSlot(new LockedSlot(inventory, i+1, 17+x*18, 52+y*18));
		}
	}
	
	private Style createColor(int color)
	{
		return Style.EMPTY.withColor(color).withItalic(false);
	}
	
	public void toggle(boolean applied)
	{
		this.applied = applied;
		offset = 0;
		updateInv();
	}
	
	public void scroll(int direction)
	{
		int maxIndex = (applied ? enchantments : applicable).size()-1;
		int stepsLeft = Math.min(Math.abs(direction), direction < 0 ? offset / 8 : (maxIndex-offset) / 8);
		int moving = stepsLeft * 8 * Integer.signum(direction);
		if(moving == 0 || offset+moving >= maxIndex || offset+moving < 0) return;
		
		offset += moving;
		updateInv();
	}
	
	public boolean canMoveDown()
	{
		int maxIndex = (applied ? enchantments : applicable).size()-1;
		int stepsLeft = Math.min(1, (maxIndex-offset) / 8);
		int moving = stepsLeft * 8;
		return moving+offset < maxIndex && moving != 0;
	}
	
	public int getOffset()
	{
		return offset;
	}
	
	public boolean isPresent()
	{
		return applied;
	}
	
	public int getInvSlots()
	{
		return Mth.clamp((applied ? enchantments : applicable).size() - offset, 0, 48);
	}
	
	private void updateInv()
	{
		for(int i = 1;i<49;i++) inventory.setItem(i, ItemStack.EMPTY);
		List<ItemStack> list = applied ? enchantments : applicable;
		for(int i = 1;i<=Math.min(48, list.size()-offset);i++)
		{
			inventory.setItem(i, list.get(i+offset-1));
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
	
	public static void addToolTip(ItemStack stack, Component text)
	{
		CompoundTag nbt = stack.getOrCreateTagElement("display");
		ListTag list = nbt.getList("Lore", 8);
		list.add(StringTag.valueOf(Component.Serializer.toJson(text)));
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
