package uniquebase.handler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableInt;

import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.util.text.event.HoverEvent.ItemHover;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentHandler
{
	public static final EnchantmentHandler INSTANCE = new EnchantmentHandler();
	Map<ResourceLocation, List<ItemStack>> enchantedItems = new Object2ObjectLinkedOpenHashMap<>();
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event)
	{
		RenderSystem.pushMatrix();
		RenderSystem.translatef(event.getX(), event.getY() + 12, 500);
		RenderSystem.scalef(0.5f, 0.5f, 1.0f);
		Minecraft mc = Minecraft.getInstance();
		List<? extends ITextProperties> tooltip = event.getLines();
		MutableInt xOff = new MutableInt();
		for(int i = 0,m=tooltip.size();i<m;i++)
		{
			ITextProperties props = tooltip.get(i);
			xOff.setValue(0);
			int y = i;
			props.visit((S, T) -> {
				HoverEvent hover = S.getHoverEvent();
				if(hover != null)
				{
					ItemHover itemHover = hover.getValue(Action.SHOW_ITEM);
					if(itemHover != null)
					{
						ItemStack stack = itemHover.getItemStack();
						if(stack.getOrCreateTag().getBoolean("ueicon"))
						{
							mc.getItemRenderer().renderGuiItem(stack, xOff.getValue() * 18, (y-1) * 20);
						}
					}
				}
				xOff.add(T.length());
				return Optional.empty();
			}, Style.EMPTY);
		}
		RenderSystem.popMatrix();
	}
	
	public void addEnchantmentInfo(ListNBT list, List<ITextComponent> tooltip)
	{
		for(int i = 0;i < list.size();++i)
		{
			CompoundNBT compoundnbt = list.getCompound(i);
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(ench == null) continue;
			tooltip.add(ench.getFullname(compoundnbt.getInt("lvl")));
			IFormattableTextComponent text = new StringTextComponent("");
			List<ItemStack> items = enchantedItems.getOrDefault(ench.getRegistryName(), ObjectLists.emptyList());
			for(int j = 1,m=items.size();j<=m;j++)
			{
				ItemStack stack = items.get(j-1).copy();
				stack.addTagElement("ueicon", ByteNBT.ONE);
				text.append(new StringTextComponent(" ").setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemHover(stack)))));
				if(j % 17 == 0) {
					tooltip.add(text);
					text = new StringTextComponent("");
				}
			}
			if(text.getSiblings().size() > 0) tooltip.add(text);
		}
	}
	
	public void init()
	{
		for(Enchantment ench : ForgeRegistries.ENCHANTMENTS)
		{
			enchantedItems.put(ench.getRegistryName(), getItemsForEnchantment(ench));
		}
	}
	
	private List<ItemStack> getItemsForEnchantment(Enchantment ench)
	{
		List<ItemStack> validItems = new ObjectArrayList<>();
		List<ItemStack> validBlocks = new ObjectArrayList<>();
		Collector<Set<ToolType>> tools = new Collector<>();
		Collector<Class<?>> classBased = new Collector<>();
		Collector<ArmorEntry> armor = new Collector<>();
		for(Item item : ForgeRegistries.ITEMS)
		{
			NonNullList<ItemStack> list = NonNullList.create();
			item.fillItemCategory(ItemGroup.TAB_SEARCH, list);
			if(list.isEmpty())
				continue;
			ItemStack stack = list.get(0);
			if(ench.canApplyAtEnchantingTable(stack))
			{
				Set<ToolType> type = stack.getToolTypes();
				if(!type.isEmpty())
				{
					tools.add(stack, type, getHighestLevel(stack, type));
				}
				else if(item instanceof TieredItem)
				{
					classBased.add(stack, item.getClass(), ((TieredItem)item).getTier().getLevel());
				}
				else if(item instanceof ArmorItem)
				{
					boolean vanilla = item.getClass() == ArmorItem.class || item.getClass() == DyeableArmorItem.class;
					EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(stack);
					armor.add(stack, new ArmorEntry(slot, vanilla ? ArmorItem.class : item.getClass()), ((ArmorItem)item).getMaterial().getDurabilityForSlot(slot));
				}
				else if(item instanceof BlockItem)
				{
					validBlocks.add(stack);
				}
				else
				{
					validItems.add(stack);
				}
			}
		}
		classBased.collect(validItems);
		armor.collect(validItems);
		tools.collect(validItems);
		if(validBlocks.size() < 10)
		{
			validItems.addAll(validBlocks);
		}
		return validItems.isEmpty() ? validBlocks : validItems;
	}
	
	private int getHighestLevel(ItemStack stack, Set<ToolType> types)
	{
		int level = 0;
		for(ToolType type : types)
		{
			level = Math.max(level, stack.getHarvestLevel(type, null, null));
		}
		return level;
	}
	
	private static class ArmorEntry
	{
		EquipmentSlotType type;
		Class<?> clz;
		int hash;
		
		public ArmorEntry(EquipmentSlotType type, Class<?> clz)
		{
			this.type = type;
			this.clz = clz;
			hash = Objects.hash(clz, type);
		}
		
		@Override
		public int hashCode()
		{
			return hash;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof ArmorEntry)
			{
				ArmorEntry entry = (ArmorEntry)obj;
				return type == entry.type && Objects.equals(entry.clz, clz);
			}
			return false;
		}
	}
	
	public static class Collector<T>
	{
		Map<T, ItemStack> items = new Object2ObjectLinkedOpenHashMap<>();
		Object2IntMap<T> values = new Object2IntLinkedOpenHashMap<>();
		
		public void add(ItemStack stack, T value, int newLevel)
		{
			if(newLevel > values.getInt(value))
			{
				values.put(value, newLevel);
				items.put(value, stack);
			}
		}
		
		public void collect(List<ItemStack> result)
		{
			if(items.isEmpty()) return;
			result.addAll(0, items.values());
		}
	}
}
