package uniquebase.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.math.DoubleMath;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.IToggleEnchantment;

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
			slots[slot.getIndex()] = (entity) -> entity.sendBreakAnimation(slot);
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
	
	public static int getEnchantmentLevel(Enchantment ench, ItemStack stack)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		if(stack.isEmpty()) return 0;
		ListNBT list = stack.getEnchantmentTagList();
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
	
	public static int getCombinedEnchantmentLevel(Enchantment ench, LivingEntity base)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		EquipmentSlotType[] slots = getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return 0;
		int totalLevel = 0;
		for(int i = 0;i < slots.length;i++)
		{
			totalLevel += getEnchantmentLevel(ench, base.getItemStackFromSlot(slots[i]));
		}
		return totalLevel;
	}
	
	public static Object2IntMap<Enchantment> getEnchantments(ItemStack stack)
	{
		if(stack.isEmpty()) return Object2IntMaps.emptyMap();
		ListNBT list = stack.getEnchantmentTagList();
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
	
	public static EquipmentSlotType[] getEquipmentSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return new EquipmentSlotType[0];
		try
		{
			return findField(Enchantment.class, ench, EquipmentSlotType[].class, "applicableEquipmentTypes", "field_185263_a");
		}
		catch(Exception e)
		{
		}
		return new EquipmentSlotType[0];
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
			int level = getEnchantmentLevel(enchantment, base.getItemStackFromSlot(slots[i]));
			if(level > 0)
			{
				return new AbstractObject2IntMap.BasicEntry<>(slots[i], level);
			}
		}
		return NO_ENCHANTMENT;
	}
	
	public static Method findMethod(Class<?> clz, String[] names, Class<?>...variables)
	{
		for(String s : names)
		{
			try
			{
				Method method = clz.getDeclaredMethod(s, variables);
				method.setAccessible(true);
				return method;
			}
			catch(Exception e)
			{
			}
		}
		throw new IllegalStateException("Couldnt find methods: "+ObjectArrayList.wrap(names));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T findField(Class<?> clz, Object instance, Class<T> result, String...names)
	{
		for(String s : names)
		{
			try
			{
				Field field = clz.getDeclaredField(s);
				field.setAccessible(true);
				return (T)field.get(instance);
			}
			catch(Exception e)
			{
			}
		}
		throw new IllegalStateException("Couldnt find fields: "+ObjectArrayList.wrap(names));
	}
	
	public static boolean harvestBlock(BreakEvent event, BlockState state, BlockPos pos)
	{
		if(!(event.getPlayer() instanceof ServerPlayerEntity))
		{
			return false;
		}
		ServerPlayerEntity player = (ServerPlayerEntity)event.getPlayer();
		World world = (World)event.getWorld();
		TileEntity tileentity = world.getTileEntity(pos);
		Block block = state.getBlock();
		if((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) && !player.canUseCommandBlock())
		{
			world.notifyBlockUpdate(pos, state, state, 3);
			return false;
		}
		else if(player.getHeldItemMainhand().onBlockStartBreak(pos, player))
		{
			return false;
		}
		else if(player.func_223729_a(world, pos, player.interactionManager.getGameType()))
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
				ItemStack itemstack = player.getHeldItemMainhand();
				ItemStack copy = itemstack.copy();
				boolean flag1 = state.canHarvestBlock(world, pos, player);
				itemstack.onBlockDestroyed(world, state, pos, player);
				if(itemstack.isEmpty() && !copy.isEmpty())
				{
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, Hand.MAIN_HAND);
				}
				boolean flag = removeBlock(world, pos, player, flag1);
				if(flag && flag1)
				{
					ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
					block.harvestBlock(world, player, pos, state, tileentity, itemstack1);
				}
				if(flag && exp > 0)
				{
					state.getBlock().dropXpOnBlockBreak(world, pos, exp);
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
			state.getBlock().onPlayerDestroy(world, pos, state);
		}
		return removed;
	}
	
	public static int drainExperience(PlayerEntity player, int points)
	{
		if(player.isCreative())
		{
			return points;
		}
		int change = Math.min(getXP(player), points);
		player.experienceTotal -= change;
		player.experienceLevel = getLvlForXP(player.experienceTotal);
		player.experience = (float)(player.experienceTotal - getXPForLvl(player.experienceLevel)) / (float)player.xpBarCap();
		player.onEnchant(ItemStack.EMPTY, 0);
		return change;
	}
	
	public static int getXP(PlayerEntity player)
	{
		return getXPForLvl(player.experienceLevel) + (DoubleMath.roundToInt(player.experience * player.xpBarCap(), RoundingMode.HALF_UP));
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
}