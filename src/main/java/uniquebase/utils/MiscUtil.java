package uniquebase.utils;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.math.DoubleMath;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.locale.Language;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantment.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.StructureBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.BaseConfig;
import uniquebase.UEBase;
import uniquebase.api.ColorConfig;
import uniquebase.api.IToggleEnchantment;
import uniquebase.utils.ICurioHelper.CurioSlot;
import uniquebase.utils.mixin.common.enchantments.EnchantmentMixin;

public class MiscUtil
{
	static final Object2IntMap.Entry<EquipmentSlot> NO_ENCHANTMENT = new AbstractObject2IntMap.BasicEntry<>(null, 0);
	static final Object2IntMap.Entry<ItemStack> NO_ENCH_FOUND = new AbstractObject2IntMap.BasicEntry<>(ItemStack.EMPTY, 0);
	static final Consumer<LivingEntity>[] SLOT_BASE = createSlots();
	static final String UPGRADE_TAG = "ue_upgrades";
	static final String TRANCENDENCE_BLOCK = "block_trancedence";
	
	@SuppressWarnings("unchecked")
	static Consumer<LivingEntity>[] createSlots()
	{
		Consumer<LivingEntity>[] slots = new Consumer[EquipmentSlot.values().length];
		for(EquipmentSlot slot : EquipmentSlot.values())
		{
			slots[slot.getIndex()] = (entity) -> entity.broadcastBreakEvent(slot);
		}
		return slots;
	}
		
	public static Consumer<LivingEntity> get(EquipmentSlot slot)
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
		return entity instanceof Player ? ((Player)entity).experienceLevel : defaultValue;
	}
	
	public static int getPlayerExperience(Entity entity, int defaultValue)
	{
		return entity instanceof Player ? ((Player)entity).totalExperience : defaultValue;
	}
	
	public static int getTrancendenceLevel(Enchantment enchantment)
	{
		return enchantment instanceof IToggleEnchantment ? ((IToggleEnchantment)enchantment).getTranscendedLevel() : 1000;
	}
	
	public static boolean isTrancendenceDisbaled(ItemStack stack)
	{
		return stack.hasTag() && stack.getTag().getBoolean(TRANCENDENCE_BLOCK); 
	}
	
	public static void toggleTrancendence(ItemStack stack)
	{
		if(isTrancendenceDisbaled(stack)) stack.removeTagKey(TRANCENDENCE_BLOCK);
		else stack.getOrCreateTag().putBoolean(TRANCENDENCE_BLOCK, true);
	}
	
	public static boolean isTranscendent(Entity entity, ItemStack stack, Enchantment enchantment)
	{
		return !isTrancendenceDisbaled(stack) && getPlayerLevel(entity, 200) >= getTrancendenceLevel(enchantment);
	}
	
	public static double getArmorProtection(LivingEntity entity)
	{
		return entity.getArmorValue() + (getAttribute(entity, Attributes.ARMOR_TOUGHNESS) * 2.5D);
	}

	public static double getAttackSpeed(LivingEntity entity)
	{
		return Math.max(getAttribute(entity, Attributes.ATTACK_SPEED, 1.6D), 0.1D);
	}
	
	public static double getAttackSpeed(LivingEntity entity, double defaultValue)
	{
		return Math.max(getAttribute(entity, Attributes.ATTACK_SPEED, defaultValue), 0.1D);
	}
	
	public static double getAttribute(LivingEntity entity, Attribute attribute)
	{
		return getAttribute(entity, attribute, 0D);
	}
	
	public static double getAttribute(LivingEntity entity, Attribute attribute, double defaultValue)
	{
		AttributeInstance instance = entity.getAttribute(attribute);
		return instance == null ? defaultValue : instance.getValue();
	}

	public static double getBaseAttribute(LivingEntity entity, Attribute attribute)
	{
		return getBaseAttribute(entity, attribute, 0D);
	}

	public static double getBaseAttribute(LivingEntity entity, Attribute attribute, double defaultValue)
	{
		AttributeInstance instance = entity.getAttribute(attribute);
		return instance == null ? defaultValue : instance.getBaseValue();
	}
	
	public static CompoundTag getPersistentData(Entity entity)
	{
		CompoundTag data = entity.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
		if(data.isEmpty()) entity.getPersistentData().put(Player.PERSISTED_NBT_TAG, data);
		return data;
	}
	
	public static int getStoredPoints(ItemStack stack, String id)
	{
		return stack.isEmpty() || stack.getTag() == null ? 0 : stack.getTag().getCompound(UPGRADE_TAG).getInt(id);
	}
	
	public static void storePoints(ItemStack stack, String id, int points)
	{
		if(stack.isEmpty()) return;
		CompoundTag data = stack.getOrCreateTagElement(UPGRADE_TAG);
		data.putInt(id, data.getInt(id)+points);
	}
	
	public static int getCombinedPoints(LivingEntity living, String id)
	{
		int total = 0;
		for(EquipmentSlot slot : EquipmentSlot.values()) {
			total += getStoredPoints(living.getItemBySlot(slot), id);
		}
		return total;
	}

	public static void doNewDamageInstance(LivingEntity ent, DamageSource source, float amount) {
		int time = ent.invulnerableTime;
		ent.hurt(source, amount);
		ent.invulnerableTime = Math.max(time, ent.invulnerableTime);
	}
	
	public static int getEnchantmentLevel(Enchantment ench, ItemStack stack)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		if(stack.isEmpty()) return 0;
		ListTag list = stack.getEnchantmentTags();
		if(list.isEmpty()) return 0;
		String id = ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundTag tag = list.getCompound(i);
			if(tag.getString("id").equalsIgnoreCase(id))
			{
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				return Math.min(tag.getInt("lvl"), getHardCap(ench));
			}
		}
		return 0;
	}
	
	public static void decreaseEnchantmentLevel(Enchantment ench, ItemStack stack) {
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return;
		if(stack.isEmpty()) return;
		ListTag list = stack.getEnchantmentTags();
		if(list.isEmpty()) return;
		String id = ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundTag tag = list.getCompound(i);
			if(tag.getString("id").equalsIgnoreCase(id))
			{
				int level = tag.getInt("lvl");
				if(level <= 1) list.remove(i);
				else tag.putInt("lvl", level-1);
				break;
			}
		}
		if(list.isEmpty()) stack.removeTagKey("Enchantments");
	}
	
	public static void replaceEnchantmentLevel(Enchantment ench, ItemStack stack, int newLevel)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return;
		if(stack.isEmpty()) return;
		ListTag list = stack.getEnchantmentTags();
		if(list.isEmpty()) return;
		String id = ForgeRegistries.ENCHANTMENTS.getKey(ench).toString();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundTag tag = list.getCompound(i);
			if(tag.getString("id").equalsIgnoreCase(id))
			{
				if(newLevel <= 0) list.remove(i);
				else tag.putInt("lvl", Math.min(newLevel, getHardCap(ench)));
				break;
			}
		}
		if(list.isEmpty()) stack.removeTagKey("Enchantments");
	}
	
	public static int getCombinedEnchantmentLevel(Enchantment ench, LivingEntity base)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		EquipmentSlot[] slots = getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return 0;
		int totalLevel = 0;
		for(int i = 0;i < slots.length;i++)
		{
			totalLevel += getEnchantmentLevel(ench, base.getItemBySlot(slots[i]));
		}
		return totalLevel;
	}
	
	
	public static int getItemLevel(ItemStack stack)
	{
		int totalLevel = 0;
		for(Entry<Enchantment, Integer> ench : MiscUtil.getEnchantments(stack).object2IntEntrySet()) 
		{
			if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) continue;
			totalLevel += ench.getValue();
		}
		return totalLevel;
	}
	
	public static int getItemCurseLevel(ItemStack stack)
	{
		int totalLevel = 0;
		for(Entry<Enchantment, Integer> ench : MiscUtil.getEnchantments(stack).object2IntEntrySet()) 
		{
			if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) continue;
			totalLevel += ench.getKey().isCurse() ? ench.getValue() : 0;
		}
		return totalLevel;
	}
	
	public static int getCombinedCurseLevel(Enchantment ench, LivingEntity base)
	{
		//Only takes Enchantments from Items that have the Enchantment
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		EquipmentSlot[] slots = getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return 0;
		int totalLevel = 0;
		for(int i = 0;i < slots.length;i++)
		{
			totalLevel += getItemCurseLevel(base.getItemBySlot(slots[i]));
		}
		return totalLevel;
	}
	
	public static Object2IntMap<Enchantment> getEnchantments(ItemStack stack)
	{
		if(stack.isEmpty()) return Object2IntMaps.emptyMap();
		ListTag list = stack.getEnchantmentTags();
		// Micro Optimization. If the EnchantmentMap is empty then returning a
		// EmptyMap is faster then creating a new map. More Performance in
		// checks.
		if(list.isEmpty()) return Object2IntMaps.emptyMap();
		Object2IntMap<Enchantment> map = new Object2IntOpenHashMap<Enchantment>();
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundTag tag = list.getCompound(i);
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
		ListTag list = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags();
		// Micro Optimization. If the EnchantmentMap is empty then returning a
		// EmptyMap is faster then creating a new map. More Performance in
		// checks.
		if(list.isEmpty()) return null;
		for(int i = 0, m = list.size();i < m;i++)
		{
			CompoundTag tag = list.getCompound(i);
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
	
	public static Set<EquipmentSlot> getEquipWithEnchantment(Enchantment ench, LivingEntity base) 
	{
		Set<EquipmentSlot> slots = new HashSet<>();
		for(ItemStack a: base.getAllSlots()) {
			if(a.getEnchantmentLevel(ench) > 0) slots.add(a.getEquipmentSlot());
		}
		return slots;
	}
	
	public static EquipmentSlot[] getEquipmentSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return new EquipmentSlot[0];
		return ((EnchantmentMixin)ench).getSlots();
	}
	
	public static Set<EquipmentSlot> getSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return Collections.emptySet();
		EquipmentSlot[] slots = getEquipmentSlotsFor(ench);
		return slots.length <= 0 ? Collections.emptySet() : new ObjectOpenHashSet<EquipmentSlot>(slots);
	}
	
	public static Object2IntMap.Entry<ItemStack> getEquipment(LivingEntity living, Enchantment ench, CurioSlot scan)
	{
		for(EquipmentSlot slot : scan.getSlot())
		{
			ItemStack stack = living.getItemBySlot(slot);
			int lvl = MiscUtil.getEnchantmentLevel(ench, stack);
			if(lvl > 0) return new AbstractObject2IntMap.BasicEntry<>(stack, lvl);
		}
		return UEBase.CURIO.findEnchantment(living, ench, scan.getCurio());
	}
	
	public static Object2IntMap.Entry<EquipmentSlot> getEnchantedItem(Enchantment enchantment, LivingEntity base)
	{
		if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) return NO_ENCHANTMENT;
		EquipmentSlot[] slots = getEquipmentSlotsFor(enchantment);
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
	
	public static <T extends Entity> T targetEntities(Class<T> target, Entity source, double maxDistance, Predicate<T> filter)
	{
		Level level = source.level;
		Vec3 sourcePos = source.getEyePosition();
        Vec3 look = source.getViewVector(1.0F);
        Vec3 max = sourcePos.add(look.x * maxDistance, look.y * maxDistance, look.z * maxDistance);
        AABB scanBox = source.getBoundingBox().expandTowards(look.scale(maxDistance)).inflate(1.0D, 1.0D, 1.0D);
		double closest = Double.MAX_VALUE;
		T result = null;
		
		for(T potentialTarget : level.getEntitiesOfClass(target, scanBox, filter)) {
			AABB aabb = potentialTarget.getBoundingBox().inflate(0.3D);
			Optional<Vec3> optional = aabb.clip(sourcePos, max);
			if (optional.isPresent()) {
				double distance = sourcePos.distanceToSqr(optional.get());
				if (distance < closest) {
					result = potentialTarget;
					closest = distance;
				}
			}
		}
		return result;
	}
	
	public static boolean harvestBlock(BreakEvent event, BlockState state, BlockPos pos)
	{
		if(!(event.getPlayer() instanceof ServerPlayer))
		{
			return false;
		}
		ServerPlayer player = (ServerPlayer)event.getPlayer();
		Level world = (Level)event.getLevel();
		BlockEntity tileentity = world.getBlockEntity(pos);
		Block block = state.getBlock();
		if((block instanceof CommandBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseGameMasterBlocks())
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
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, InteractionHand.MAIN_HAND);
				}
				boolean flag = removeBlock(world, pos, player, flag1);
				if(flag && flag1)
				{
					ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
					block.playerDestroy(world, player, pos, state, tileentity, itemstack1);
				}
				if(flag && exp > 0)
				{
					state.getBlock().popExperience((ServerLevel)world, pos, exp);
				}
				
				return true;
			}
		}
	}
	
	private static boolean removeBlock(Level world, BlockPos pos, ServerPlayer player, boolean canHarvest)
	{
		BlockState state = world.getBlockState(pos);
		boolean removed = state.onDestroyedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
		if(removed)
		{
			state.getBlock().destroy(world, pos, state);
		}
		return removed;
	}
	
	public static int drainExperience(Player player, int points)
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
	
	public static int getXP(Player player)
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
	
	public static int getColorFromText(String s)
    {
        s = s == null ? "" : s;
        return (s.hashCode() & 11184810) + 4473924;
    }
	
	public static Style toColor(int color)
    {
        return Style.EMPTY.withColor(TextColor.fromRgb(color & 0xFFFFFF));
    }
	
	public static String toHex(int color)
    {
        return "#"+Integer.toHexString((1 << 24) | (color & 0xFFFFFF)).substring(1);
    }
	
	public static int parseColor(String color, int defaultColor) {
		if(color == null || color.isEmpty()) return defaultColor;
		int size = color.length();
		if(color.startsWith("#")) size--;
		else if(color.startsWith("0x")) size-=2;
		else throw new UnsupportedOperationException("Format Unsupported, please use HTML/Hex format");
		if(size <= 6) return Integer.decode(color);
		int offset = 6 + (color.length() - size);
		return Integer.decode(color.substring(0, offset)) | Integer.decode("#"+color.substring(offset, offset+2)) << 24;
	}
	
	public static ChatFormatting getFormatting(Rarity rarity)
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
	
	public static MutableComponent createEnchantmentName(Enchantment ench, int level, boolean allowCurse) {
		MutableComponent textComponent = Component.translatable(ench.getDescriptionId()).setStyle(getEnchantmentColor(ench, allowCurse));
		if(ench.getMaxLevel() != 1) {
			textComponent.append(" ").append(Component.translatable("enchantment.level." + level));
		}
		MutableComponent result = Component.literal("");
		Language map = Language.getInstance();
		String s = ench.getDescriptionId()+".icon";
		if(map.has(s)) {
			result.append(Component.translatable(s).withStyle(Style.EMPTY.withFont(new ResourceLocation("uniquebase:icons"))).withStyle(MiscUtil.toColor(0xFFFFFF)));
			result.append(" ");
		}
		result.append(textComponent);
		if(ench instanceof IToggleEnchantment toggle && !toggle.isEnabled()) result.append(Component.translatable("unique.base.enchantment.disabled"));
		return result;
	}
	
	public static Style getEnchantmentColor(Enchantment ench, boolean allowCurseColor) {
		ColorConfig config = BaseConfig.BOOKS.getEnchantmentColor(ench);
		return MiscUtil.toColor(config.getTextColor() == -1 ? (ench.isCurse() && allowCurseColor ? 0xFF5555 : 0xAAAAAAAA) : config.getTextColor());
	}
	
	public static void spawnDrops(LivingEntity deadEntity, Enchantment enchantment, int level) {
		ItemEntity itemEntity = deadEntity.spawnAtLocation(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level)), 0F);
		if(itemEntity != null) itemEntity.setPickUpDelay(30);
	}
}