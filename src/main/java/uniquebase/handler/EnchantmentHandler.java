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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TieredItem;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.util.text.event.HoverEvent.ItemHover;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;

public class EnchantmentHandler
{
	public static final EnchantmentHandler INSTANCE = new EnchantmentHandler();
	Map<Enchantment, List<ItemStack>> enchantedItems = new Object2ObjectLinkedOpenHashMap<>();
	int ticker = 0;
	
	public void limitEnchantments(ListNBT list, ItemStack stack)
	{
		int limit = UEBase.ENCHANTMENT_LIMITS.getInt(stack.getItem());
		while(list.size() > limit) {
			list.remove(list.size()-1);
		}
		stack.getOrCreateTag().put(stack.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments", list);
	}
	
	public void limitEnchantments(List<EnchantmentData> list, ItemStack item)
	{
		int limit = UEBase.ENCHANTMENT_LIMITS.getInt(item.getItem());
		while(list.size() > limit) {
			list.remove(list.size()-1);
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)	
	public void onClientTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		if(Screen.hasControlDown()) ticker = 0;
		ticker += Screen.hasShiftDown() ? 0 : 1;
	}
	
	@SuppressWarnings("deprecation")
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTooltip(RenderTooltipEvent.PostText event)
	{
		if(!UEBase.ICONS.get()) return;
		//TODO copy IC2Classics UI Renderer code into here to remove OpenGL requirements.
		RenderSystem.pushMatrix();
		RenderSystem.translatef(event.getX(), event.getY() + 12, 500);
		RenderSystem.scalef(0.5F, 0.5F, 1.0F);
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
	
	
	
	@OnlyIn(Dist.CLIENT)
	public boolean addEnchantmentInfo(ListNBT list, List<ITextComponent> tooltip, Item item)
	{
		boolean hideCurses = UEBase.HIDE_CURSES.get();
		boolean icons = UEBase.ICONS.get();
		boolean desciptions = !ModList.get().isLoaded("enchdesc");
		if(!hideCurses && !icons && !desciptions) return false;
		boolean tools = UEBase.SHOW_NON_BOOKS.get();
		boolean shiftPressed = Screen.hasShiftDown();
		int elements = UEBase.ICON_ROW_ELEMENTS.get();
		int total = UEBase.ICON_ROWS.get() * elements;
		int cycleTime = UEBase.ICON_CYCLE_TIME.get();
		for(int i = 0;i < list.size();++i)
		{
			CompoundNBT compoundnbt = list.getCompound(i);
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(ench == null || (ench.isCurse() && hideCurses && !shiftPressed)) continue;
			tooltip.add(ench.getFullname(compoundnbt.getInt("lvl")));
			if(icons) addEnchantment(tooltip, ench, elements, total, cycleTime);
			if(desciptions && (item == Items.ENCHANTED_BOOK || tools)) addDescriptions(tooltip, ench);
			
		}
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	private void addDescriptions(List<ITextComponent> list, Enchantment ench)
	{
		if(!Screen.hasShiftDown()) return;
		String s = ench.getDescriptionId() + ".desc";
		if(I18n.exists(s)) list.add(new TranslationTextComponent(s).withStyle(TextFormatting.DARK_GRAY));
		else list.add(new TranslationTextComponent("unique.base.jei.no.description"));
	}
	
	@OnlyIn(Dist.CLIENT)
	private void addEnchantment(List<ITextComponent> list, Enchantment ench, int elements, int total, int cycleTime)
	{
		List<ItemStack> items = getItemsForEnchantment(ench);
		if(items.isEmpty()) return;
		IFormattableTextComponent text = new StringTextComponent("").withStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")));
		int start = items.size() >= total ? ((ticker / cycleTime) % MathHelper.ceil(items.size() / (double)total)) * total : 0;
		for(int j = 1+start,x=0,m=items.size();j<=m&&x<total;j++,x++)
		{
			ItemStack stack = items.get(j-1).copy();
			stack.addTagElement("ueicon", ByteNBT.ONE);
			text.append(new StringTextComponent(" ").setStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")).withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemHover(stack)))));
			if(j % elements == 0) {
				list.add(text);
				text = new StringTextComponent("").withStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")));
			}
		}
		if(text.getSiblings().size() > 0) list.add(text);
	}
	
	private List<ItemStack> getItemsForEnchantment(Enchantment ench)
	{
		return enchantedItems.computeIfAbsent(ench, this::createItemsForEnchantments);
	}
	
	private List<ItemStack> createItemsForEnchantments(Enchantment ench)
	{
		List<ItemStack> validItems = new ObjectArrayList<>();
		List<ItemStack> validBlocks = new ObjectArrayList<>();
		Collector<Set<ToolType>> tools = new Collector<>();
		Collector<Class<?>> classBased = new Collector<>();
		Collector<ArmorEntry> armor = new Collector<>();
		for(Item item : ForgeRegistries.ITEMS)
		{
			ItemStack stack = new ItemStack(item);
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
