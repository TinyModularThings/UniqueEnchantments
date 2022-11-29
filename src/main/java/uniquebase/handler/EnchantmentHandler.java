package uniquebase.handler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.mojang.datafixers.util.Either;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.network.chat.HoverEvent.ItemStackInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.gui.TooltipIcon;
import uniquebase.utils.IdStat;
import uniquebase.utils.VisibilityMode;

public class EnchantmentHandler
{
	public static final EnchantmentHandler INSTANCE = new EnchantmentHandler();
	Map<Enchantment, List<ItemStack>> enchantedItems = new Object2ObjectLinkedOpenHashMap<>();
	boolean toggle = false;
	int ticker = 0;
	
	public void limitEnchantments(ListTag list, ItemStack stack) {
		int limit = UEBase.ENCHANTMENT_LIMITS.getInt(ForgeRegistries.ITEMS.getKey(stack.getItem()));
		if(UEBase.ENCHANTMENT_LIMIT_BLACKLIST.isEmpty()) {
			while(list.size() > limit) {
				list.remove(list.size()-1);
			}
		}
		else if(list.size() > limit) {
			IntArrayList toDeleteList = new IntArrayList();
			for(int i = 0,m=list.size();i<m;i++) {
				if(UEBase.ENCHANTMENT_LIMIT_BLACKLIST.contains(ResourceLocation.tryParse(list.getCompound(i).getString("id")))) continue;
				toDeleteList.add(i);
			}
			while(toDeleteList.size() > limit) {
				list.remove(toDeleteList.popInt());
			}
		}
	}
	
	public void limitEnchantments(List<EnchantmentInstance> list, ItemStack item) {
		int limit = UEBase.ENCHANTMENT_LIMITS.getInt(ForgeRegistries.ITEMS.getKey(item.getItem()));
		if(UEBase.ENCHANTMENT_LIMIT_BLACKLIST.isEmpty()) {
			while(list.size() > limit) {
				list.remove(list.size()-1);
			}
		}
		else if(list.size() > limit) {
			IntArrayList toDeleteList = new IntArrayList();
			for(int i = 0,m=list.size();i<m;i++) {
				if(UEBase.ENCHANTMENT_LIMIT_BLACKLIST.contains(list.get(i).enchantment)) continue;
				toDeleteList.add(i);
			}
			while(toDeleteList.size() > limit) {
				list.remove(toDeleteList.popInt());
			}
		}
	}
	
	public void cleanCache()
	{
		enchantedItems.clear();
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)	
	public void onClientTick(ClientTickEvent event)
	{
		if(event.phase == Phase.START) return;
		if(UEBase.ICONS_VISIBLE.get()) {
			if(Screen.hasControlDown()) ticker = 0;
			ticker += Screen.hasShiftDown() ? 0 : 1;
		}
		if(UEBase.ICONS.get()) {
			Player player = Minecraft.getInstance().player;
			if(player != null) {
				boolean isPressed = UEBase.ENCHANTMENT_ICONS.test(player);
				if(isPressed && !toggle) {
					toggle = true;
					UEBase.ICONS_VISIBLE.set(!UEBase.ICONS_VISIBLE.get());
					UEBase.CONFIG.save();
				}
				else if(!isPressed && toggle) {
					toggle = false;
				}
			}
		}
	}
	
	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void renderTooltip(RenderTooltipEvent.GatherComponents event)
	{
		if(!UEBase.ICONS.get()) return;
		List<Either<FormattedText, TooltipComponent>> lines = event.getTooltipElements();
		for(int i = 0,m=lines.size();i<m;i++)
		{
			Either<FormattedText, TooltipComponent> line = lines.get(i);
			Optional<FormattedText> left = line.left();
			if(left.isEmpty()) continue;
			List<ItemStack> items = new ObjectArrayList<>();
			left.get().visit((S, T) -> {
				HoverEvent hover = S.getHoverEvent();
				if(hover != null)
				{
					ItemStackInfo itemHover = hover.getValue(Action.SHOW_ITEM);
					if(itemHover != null)
					{
						ItemStack stack = itemHover.getItemStack();
						if(stack.getOrCreateTag().getBoolean("ueicon"))
						{
							items.add(stack);
						}
					}
				}
				return Optional.empty();
			}, Style.EMPTY);
			if(items.size() > 0)
			{
				lines.set(i, Either.right(new TooltipIcon(items)));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean addEnchantmentInfo(ListTag list, List<Component> tooltip, Item item)
	{
		boolean hideCurses = UEBase.HIDE_CURSES.get();
		boolean icons = UEBase.ICONS.get() && UEBase.ICONS_VISIBLE.get();
		boolean desciptions = !ModList.get().isLoaded("enchdesc") && UEBase.SHOW_DESCRIPTION.get();
		if(!hideCurses && !icons && !desciptions) return false;
		boolean tools = UEBase.SHOW_NON_BOOKS.get();
		boolean shiftPressed = Screen.hasShiftDown();
		int elements = UEBase.ICON_ROW_ELEMENTS.get();
		int total = UEBase.ICON_ROWS.get() * elements;
		int cycleTime = UEBase.ICON_CYCLE_TIME.get();
		for(int i = 0;i < list.size();++i)
		{
			CompoundTag compoundnbt = list.getCompound(i);
			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(ResourceLocation.tryParse(compoundnbt.getString("id")));
			if(ench == null || (ench.isCurse() && hideCurses && !shiftPressed)) continue;
			tooltip.add(ench.getFullname(compoundnbt.getInt("lvl")));
			if(icons) addEnchantment(tooltip, ench, elements, total, cycleTime);
			if(desciptions && (item == Items.ENCHANTED_BOOK || tools)) addDescriptions(tooltip, ench);
			
		}
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	private void addDescriptions(List<Component> list, Enchantment ench)
	{
		if(!Screen.hasShiftDown()) return;
		String s = ench.getDescriptionId() + ".desc";
		if(I18n.exists(s)) list.add(Component.translatable(s).withStyle(ChatFormatting.DARK_AQUA));
		else list.add(Component.translatable("unique.base.jei.no.description"));
	}
	
	@OnlyIn(Dist.CLIENT)
	private void addEnchantment(List<Component> list, Enchantment ench, int elements, int total, int cycleTime)
	{
		List<ItemStack> items = getItemsForEnchantment(ench);
		if(items.isEmpty()) return;
		MutableComponent text = Component.literal("").withStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")));
		int start = items.size() >= total ? ((ticker / cycleTime) % Mth.ceil(items.size() / (double)total)) * total : 0;
		for(int j = 1+start,x=0,m=items.size();j<=m&&x<total;j++,x++)
		{
			ItemStack stack = items.get(j-1).copy();
			stack.addTagElement("ueicon", ByteTag.ONE);
			text.append(Component.literal(" ").setStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")).withHoverEvent(new HoverEvent(Action.SHOW_ITEM, new ItemStackInfo(stack)))));
			if(j % elements == 0) {
				list.add(text);
				text = Component.literal("").withStyle(Style.EMPTY.withFont(new ResourceLocation("ue", "hacking")));
			}
		}
		if(text.getSiblings().size() > 0) list.add(text);
	}
	
	@OnlyIn(Dist.CLIENT)
	private List<ItemStack> getItemsForEnchantment(Enchantment ench)
	{
		if(Minecraft.getInstance().player == null) return Collections.emptyList();
		return enchantedItems.computeIfAbsent(ench, this::createItemsForEnchantments);
	}
	
	@OnlyIn(Dist.CLIENT)
	private List<ItemStack> createItemsForEnchantments(Enchantment ench)
	{
		List<ItemStack> validItems = new ObjectArrayList<>();
		List<ItemStack> validBlocks = new ObjectArrayList<>();
		List<ItemStack> foods = new ObjectArrayList<>();
		List<ItemStack> extra = new ObjectArrayList<>();
		Collector<Class<?>> classBased = new Collector<>();
		Collector<ArmorEntry> armor = new Collector<>();
		Set<Class<?>> clz = new ObjectOpenHashSet<>();
		IdStat<Item> stat = UEBase.APPLICABLE_ICON_OVERRIDE;
		boolean check = !stat.isEmpty();
		VisibilityMode mode = UEBase.ICON_MODE.get();
		int limit = mode == VisibilityMode.LIMITED ? UEBase.LIMIT_AMOUNT.get() : Integer.MAX_VALUE;
		for(Item item : ForgeRegistries.ITEMS)
		{
			if(check && !stat.contains(ForgeRegistries.ITEMS.getKey(item)))
			{
				continue;
			}
			ItemStack stack = new ItemStack(item);
			if(ench.canApplyAtEnchantingTable(stack))
			{
				if(mode == VisibilityMode.EVERYTHING)
				{
					validItems.add(stack);
					continue;
				}
				else if(item instanceof TieredItem tier)
				{
					classBased.add(stack, item.getClass(), TierSortingRegistry.getSortedTiers().indexOf(tier.getTier()));
				}
				else if(item instanceof ArmorItem)
				{
					boolean vanilla = item.getClass() == ArmorItem.class || item.getClass() == DyeableArmorItem.class;
					if(mode == VisibilityMode.LIMITED && !vanilla) continue;
					EquipmentSlot slot = Mob.getEquipmentSlotForItem(stack);
					armor.add(stack, new ArmorEntry(slot, vanilla ? ArmorItem.class : item.getClass()), ((ArmorItem)item).getMaterial().getDurabilityForSlot(slot));
				}
				else if(item instanceof BlockItem)
				{
					validBlocks.add(stack);
				}
				else if(item.isEdible())
				{
					ClientLevel world = Minecraft.getInstance().level;
					SimpleContainer inv = new SimpleContainer(1);
					inv.setItem(0, stack.copy());
					Optional<SmeltingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, inv, world);
					if(!recipe.isPresent() || !recipe.get().getResultItem().isEdible())
					{
						foods.add(stack);						
					}
				}
				else
				{
					if(ForgeRegistries.ITEMS.getKey(item).getNamespace().equalsIgnoreCase("minecraft") || clz.add(item.getClass())) {
						extra.add(stack);
					}
				}
			}
		}
		classBased.collect(validItems);
		armor.collect(validItems);
		if(!foods.isEmpty())
		{
			validItems.addAll(foods.subList(0, Math.min(foods.size(), limit)));
		}
		if(!extra.isEmpty())
		{
			validItems.addAll(extra.subList(0, Math.min(extra.size(), limit)));
		}
		if(validBlocks.size() < 10)
		{
			validItems.addAll(validBlocks);
		}
		return validItems.isEmpty() ? validBlocks : validItems;
	}
	
	private static class ArmorEntry
	{
		EquipmentSlot type;
		Class<?> clz;
		int hash;
		
		public ArmorEntry(EquipmentSlot type, Class<?> clz)
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
