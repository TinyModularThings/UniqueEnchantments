package uniquebase.utils;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.math.DoubleMath;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.api.IToggleEnchantment;
import uniquebase.handler.flavor.Flavor;
import uniquebase.handler.flavor.FlavorRarity;
import uniquebase.handler.flavor.FlavorTarget;
import uniquebase.handler.flavor.ItemType;
import uniquebase.utils.mixin.EnchantmentMixin;

public class MiscUtil
{
	static final Object2IntMap.Entry<EquipmentSlotType> NO_ENCHANTMENT = new AbstractObject2IntMap.BasicEntry<>(null, 0);
	static final Consumer<LivingEntity>[] SLOT_BASE = createSlots();
	
	@SuppressWarnings("unchecked")
	static Consumer<LivingEntity>[] createSlots()
	{
		Consumer<LivingEntity>[] slots = new Consumer[EquipmentSlotType.values().length];
		for(EquipmentSlotType slot : EquipmentSlotType.values())
		{
			slots[slot.getIndex()] = (entity) -> entity.broadcastBreakEvent(slot);
		}
		return slots;
	}
		
	public static Consumer<LivingEntity> get(EquipmentSlotType slot)
	{
		return slot == null ? SLOT_BASE[0] : SLOT_BASE[slot.getIndex()];
	}
	
	public static int getHardCap(Enchantment ench)
	{
		return ench instanceof IToggleEnchantment ? ((IToggleEnchantment)ench).getHardCap() : Integer.MAX_VALUE;
	}
	
	public static boolean isDisabled(Enchantment ench)
	{
		return ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled();
	}
	
	public static int getPlayerLevel(Entity entity, int defaultValue)
	{
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).experienceLevel : defaultValue;
	}

	public static double getArmorProtection(LivingEntity entity)
	{
		return entity.getArmorValue() + (getAttribute(entity, Attributes.ARMOR_TOUGHNESS) * 2.5D);
	}

	public static double getAttackSpeed(LivingEntity entity)
	{
		return getAttribute(entity, Attributes.ATTACK_SPEED, 0D);
	}

	public static double getAttribute(LivingEntity entity, Attribute attribute)
	{
		return getAttribute(entity, attribute, 0D);
	}

	public static double getAttribute(LivingEntity entity, Attribute attribute, double defaultValue)
	{
		ModifiableAttributeInstance instance = entity.getAttribute(attribute);
		return instance == null ? defaultValue : instance.getValue();
	}

	public static double getBaseAttribute(LivingEntity entity, Attribute attribute)
	{
		return getBaseAttribute(entity, attribute, 0D);
	}

	public static double getBaseAttribute(LivingEntity entity, Attribute attribute, double defaultValue)
	{
		ModifiableAttributeInstance instance = entity.getAttribute(attribute);
		return instance == null ? defaultValue : instance.getBaseValue();
	}
	
	public static CompoundNBT getPersistentData(Entity entity)
	{
		CompoundNBT data = entity.getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
		if(data.isEmpty()) entity.getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, data);
		return data;
	}
	
	public static int getEnchantmentLevel(Enchantment ench, ItemStack stack)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		if(stack.isEmpty()) return 0;
		ListNBT list = stack.getEnchantmentTags();
		if(list.isEmpty()) return 0;
		String id = ench.getRegistryName().toString();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundNBT tag = list.getCompound(i);
			if(tag.getString("id").equalsIgnoreCase(id))
			{
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				return Math.min(tag.getInt("lvl"), getHardCap(ench));
			}
		}
		return 0;
	}
	
	public static void replaceEnchantmentLevel(Enchantment ench, ItemStack stack, int newLevel)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return;
		if(stack.isEmpty()) return;
		ListNBT list = stack.getEnchantmentTags();
		if(list.isEmpty()) return;
		String id = ench.getRegistryName().toString();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundNBT tag = list.getCompound(i);
			if(tag.getString("id").equalsIgnoreCase(id))
			{
				if(newLevel <= 0) list.remove(i);
				else tag.putInt("lvl", Math.min(newLevel, getHardCap(ench)));
				return;
			}
		}
	}
	
	public static int getCombinedEnchantmentLevel(Enchantment ench, LivingEntity base)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		EquipmentSlotType[] slots = getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return 0;
		int totalLevel = 0;
		for(int i = 0;i < slots.length;i++)
		{
			totalLevel += getEnchantmentLevel(ench, base.getItemBySlot(slots[i]));
		}
		return totalLevel;
	}
	
	public static Object2IntMap<Enchantment> getEnchantments(ItemStack stack)
	{
		if(stack.isEmpty()) return Object2IntMaps.emptyMap();
		ListNBT list = stack.getEnchantmentTags();
		// Micro Optimization. If the EnchantmentMap is empty then returning a
		// EmptyMap is faster then creating a new map. More Performance in
		// checks.
		if(list.isEmpty()) return Object2IntMaps.emptyMap();
		Object2IntMap<Enchantment> map = new Object2IntOpenHashMap<Enchantment>();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundNBT tag = list.getCompound(i);
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(tag.getString("id")));
			if(enchantment != null)
			{
				if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) continue;
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				map.put(enchantment, Math.min(tag.getInt("lvl"), getHardCap(enchantment)));
			}
		}
		return map;
	}
	
	public static Object2IntMap.Entry<Enchantment> getFirstEnchantment(ItemStack stack)
	{
		ListNBT list = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags();
		// Micro Optimization. If the EnchantmentMap is empty then returning a
		// EmptyMap is faster then creating a new map. More Performance in
		// checks.
		if(list.isEmpty()) return null;
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundNBT tag = list.getCompound(i);
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(tag.getString("id")));
			if(enchantment != null)
			{
				if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) continue;
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				return new AbstractObject2IntMap.BasicEntry<Enchantment>(enchantment, Math.min(tag.getInt("lvl"), getHardCap(enchantment)));
			}
		}
		return null;
	}
	
	public static EquipmentSlotType[] getEquipmentSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return new EquipmentSlotType[0];
		return ((EnchantmentMixin)ench).getSlots();
	}
	
	public static Set<EquipmentSlotType> getSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return Collections.emptySet();
		EquipmentSlotType[] slots = getEquipmentSlotsFor(ench);
		return slots.length <= 0 ? Collections.emptySet() : new ObjectOpenHashSet<EquipmentSlotType>(slots);
	}
	
	public static Object2IntMap.Entry<EquipmentSlotType> getEnchantedItem(Enchantment enchantment, LivingEntity base)
	{
		if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) return NO_ENCHANTMENT;
		EquipmentSlotType[] slots = getEquipmentSlotsFor(enchantment);
		if(slots.length <= 0)
		{
			return NO_ENCHANTMENT;
		}
		for(int i = 0;i < slots.length;i++)
		{
			int level = getEnchantmentLevel(enchantment, base.getItemBySlot(slots[i]));
			if(level > 0)
			{
				return new AbstractObject2IntMap.BasicEntry<>(slots[i], level);
			}
		}
		return NO_ENCHANTMENT;
	}
	
	public static boolean harvestBlock(BreakEvent event, BlockState state, BlockPos pos)
	{
		if(!(event.getPlayer() instanceof ServerPlayerEntity))
		{
			return false;
		}
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		World world = (World)event.getWorld();
		TileEntity tileentity = world.getBlockEntity(pos);
		Block block = state.getBlock();
		if((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks())
		{
			world.sendBlockUpdated(pos, state, state, 3);
			return false;
		}
		else if(player.getMainHandItem().onBlockStartBreak(pos, player))
		{
			return false;
		}
		else if(player.blockActionRestricted(world, pos, player.gameMode.getGameModeForPlayer()))
		{
			return false;
		}
		else
		{
			if(player.isCreative())
			{
				removeBlock(world, pos, player, false);
				return true;
			}
			else
			{
				int exp = event.getExpToDrop();
				ItemStack itemstack = player.getMainHandItem();
				ItemStack copy = itemstack.copy();
				boolean flag1 = state.canHarvestBlock(world, pos, player);
				itemstack.mineBlock(world, state, pos, player);
				if(itemstack.isEmpty() && !copy.isEmpty())
				{
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.MAIN_HAND);
				}
				boolean flag = removeBlock(world, pos, player, flag1);
				if(flag && flag1)
				{
					ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
					block.playerDestroy(world, player, pos, state, tileentity, itemstack1);
				}
				if(flag && exp > 0)
				{
					state.getBlock().popExperience((ServerWorld)world, pos, exp);
				}
				
				return true;
			}
		}
	}
	
	private static boolean removeBlock(World world, BlockPos pos, ServerPlayerEntity player, boolean canHarvest)
	{
		BlockState state = world.getBlockState(pos);
		boolean removed = state.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
		if(removed)
		{
			state.getBlock().destroy(world, pos, state);
		}
		return removed;
	}
	
	public static int drainExperience(PlayerEntity player, int points)
	{
		if(player.isCreative())
		{
			return points;
		}
		int totalXP = getXP(player); 
		if(totalXP != player.totalExperience) player.totalExperience = totalXP;
		int change = Math.min(getXP(player), points);
		player.totalExperience -= change;
		player.experienceLevel = getLvlForXP(player.totalExperience);
		player.experienceProgress = (float)(player.totalExperience - getXPForLvl(player.experienceLevel)) / (float)player.getXpNeededForNextLevel();
		player.onEnchantmentPerformed(ItemStack.EMPTY, 0);
		return change;
	}
	
	public static int getXP(PlayerEntity player)
	{
		return getXPForLvl(player.experienceLevel) + (DoubleMath.roundToInt(player.experienceProgress * player.getXpNeededForNextLevel(), RoundingMode.HALF_UP));
	}
	
	public static int getXPForLvl(int level)
	{
		if(level < 0)
			return Integer.MAX_VALUE;
		if(level <= 15)
			return level * level + 6 * level;
		if(level <= 30)
			return (int)(((level * level) * 2.5D) - (40.5D * level) + 360.0D);
		return (int)(((level * level) * 4.5D) - (162.5D * level) + 2220.0D);
	}
	
	public static int getLvlForXP(int totalXP)
	{
		int result = 0;
		while(getXPForLvl(result) <= totalXP)
		{
			result++;
		}
		return --result;
	}
	
	public static Style toColor(int color)
    {
        return Style.EMPTY.withColor(Color.fromRgb(color & 0xFFFFFF));
    }
	
	public static TextFormatting getFormatting(Rarity rarity)
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
	
	public static int getAdjectiveCount(float chance, Random rand) {
		if(chance <= 0F) return 0;
		int total = 0;
		for(int i=0;i<3;i++) {
			if(rand.nextDouble() < chance) {
				total++;
				chance = chance * chance;
			}
		}

		return Math.min(total, 3);
	}
	
	public static ITextComponent itemNameGen(ItemStack item, Entity entity, double entityChance, double rarityChance, double locationChance) {
		Random rand = entity.level.getRandom();
		IFormattableTextComponent result = new StringTextComponent("").setStyle(Style.EMPTY.withItalic(false));
		
		//To Be Done: Name Combo (if Person X was chosen, 10% to get Adjective/Name/Suffix Y)
		
		ITextComponent hoverName = item.getItem().getName(item);
		FlavorRarity rarity = FlavorRarity.UNCOMMON;
		float chance = rarity.getChance();
		
		//Person from List, or Name of Entity slain
		if(rand.nextDouble() < entityChance && entity != null) {
			result.append(entity.getDisplayName()).append(" ");
		}
		else if(rand.nextDouble() < chance) {
			result.append(Flavor.getFlavor(FlavorTarget.PERSON, rarity, ItemType.byId("sword"), rand));
		}
		
		//UniqueRarity of the Item
		if(rarity.getIndex() >= 4 && rand.nextDouble() < (1-rarityChance) || rand.nextDouble() < rarityChance) {
			result.append(rarity.getName()).append(" ");
		}
		
		//Adjectives
		int adjCount = getAdjectiveCount(chance, rand);
		UEBase.LOGGER.info(adjCount);
		if(adjCount > 0) {
			for(int i=1; i<=adjCount; i++) {
				result.append(Flavor.getFlavor(FlavorTarget.ADJECTIVE, rarity, ItemType.byId("sword"), rand));
			}
		}
		
		//Name
		if(rand.nextDouble() < chance * chance) {
			result.append(Flavor.getFlavor(FlavorTarget.NAME, rarity, ItemType.byId("sword"), rand));
		} else {
			result.append(hoverName.getString()).append(" ");
		}
		
		//Location OR Suffix
		if(rand.nextDouble() < locationChance && entity instanceof LivingEntity) {
			result.append((entity.getY() < 63.0 ? "from the " : "of the ") + toPascalCase(entity.level.getBiome(entity.blockPosition()).getRegistryName().getPath()));
		} else if(rand.nextDouble() < chance) {
			result.append(Flavor.getFlavor(FlavorTarget.SUFFIX, rarity, ItemType.byId("sword"), rand));
		}
		UEBase.LOGGER.info(result.getString());
		return !result.getSiblings().isEmpty() ? result : item.getHoverName();
	}
	
	public static String firstLetterUppercase(String string) {
		if(string == null || string.isEmpty()) {
			return string;
		}
		String first = Character.toString(string.charAt(0));
		return string.replaceFirst(first, first.toUpperCase());
	}
	
	public static String toPascalCase(String input)
	{
		StringBuilder builder = new StringBuilder();
		for(String s : input.replaceAll("_", " ").split(" "))
		{
			builder.append(firstLetterUppercase(s)).append(" ");
		}
		return builder.substring(0, builder.length() - 1);
	}
	
}
