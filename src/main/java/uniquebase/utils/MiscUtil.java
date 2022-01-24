package uniquebase.utils;

import java.math.RoundingMode;
import java.util.Collections;
import java.util.Set;

import com.google.common.math.DoubleMath;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import uniquebase.api.IToggleEnchantment;

@SuppressWarnings("deprecation")
public class MiscUtil
{
	public static final Object2IntMap.Entry<EntityEquipmentSlot> NO_ENCHANTMENT = new AbstractObject2IntMap.BasicEntry<>(null, 0);
	
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
		return entity instanceof EntityPlayer ? ((EntityPlayer)entity).experienceLevel : defaultValue;
	}
	
	public static double getAttackSpeed(EntityLivingBase entity)
	{
		return getAttribute(entity, SharedMonsterAttributes.ATTACK_SPEED, 0D);
	}
	
	public static double getAttribute(EntityLivingBase entity, IAttribute attribute)
	{
		return getAttribute(entity, attribute, 0D);
	}
	
	public static double getAttribute(EntityLivingBase entity, IAttribute attribute, double defaultValue)
	{
		IAttributeInstance instance = entity.getEntityAttribute(attribute);
		return instance == null ? defaultValue : instance.getAttributeValue();
	}
	
	public static double getBaseAttribute(EntityLivingBase entity, IAttribute attribute)
	{
		return getBaseAttribute(entity, attribute, 0D);
	}
	
	public static double getBaseAttribute(EntityLivingBase entity, IAttribute attribute, double defaultValue)
	{
		IAttributeInstance instance = entity.getEntityAttribute(attribute);
		return instance == null ? defaultValue : instance.getBaseValue();
	}
	
	public static NBTTagCompound getPersistentData(Entity entity)
	{
		NBTTagCompound data = entity.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		if(data.isEmpty()) entity.getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, data);
		return data;
	}
	
	public static int getEnchantmentLevel(Enchantment ench, ItemStack stack)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		if(stack.isEmpty()) return 0;
		NBTTagList list = stack.getEnchantmentTagList();
		if(list.isEmpty()) return 0;
		int id = Enchantment.getEnchantmentID(ench);
		for(int i = 0, m = list.tagCount();i < m;i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			if(tag.getInteger("id") == id)
			{
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				return Math.min(tag.getInteger("lvl"), getHardCap(ench));
			}
		}
		return 0;
	}
	
	public static int getCombinedEnchantmentLevel(Enchantment ench, EntityLivingBase base)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return 0;
		EntityEquipmentSlot[] slots = getEquipmentSlotsFor(ench);
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
		NBTTagList list = stack.getEnchantmentTagList();
		// Micro Optimization. If the EnchantmentMap is empty then returning a
		// EmptyMap is faster then creating a new map. More Performance in
		// checks.
		if(list.isEmpty()) return Object2IntMaps.emptyMap();
		Object2IntMap<Enchantment> map = new Object2IntOpenHashMap<Enchantment>();
		for(int i = 0, m = list.tagCount();i < m;i++)
		{
			NBTTagCompound tag = list.getCompoundTagAt(i);
			Enchantment enchantment = Enchantment.getEnchantmentByID(tag.getShort("id"));
			if(enchantment != null)
			{
				if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) continue;
				// Only grabbing Level if it is needed. Not wasting CPU Time on
				// grabbing useless data
				map.put(enchantment, Math.min(tag.getInteger("lvl"), getHardCap(enchantment)));
			}
		}
		return map;
	}
	
	public static EntityEquipmentSlot[] getEquipmentSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return new EntityEquipmentSlot[0];
		try
		{
			return (EntityEquipmentSlot[])ReflectionHelper.getPrivateValue(Enchantment.class, ench, "applicableEquipmentTypes", "field_185263_a");
		}
		catch(Exception e)
		{
		}
		return new EntityEquipmentSlot[0];
	}
	
	public static Set<EntityEquipmentSlot> getSlotsFor(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment && !((IToggleEnchantment)ench).isEnabled()) return Collections.emptySet();
		EntityEquipmentSlot[] slots = getEquipmentSlotsFor(ench);
		return slots.length <= 0 ? Collections.emptySet() : new ObjectLinkedOpenHashSet<>(slots);
	}
	
	public static Object2IntMap.Entry<EntityEquipmentSlot> getEnchantedItem(Enchantment enchantment, EntityLivingBase base)
	{
		if(enchantment instanceof IToggleEnchantment && !((IToggleEnchantment)enchantment).isEnabled()) return NO_ENCHANTMENT;
		EntityEquipmentSlot[] slots = getEquipmentSlotsFor(enchantment);
		if(slots.length <= 0) return NO_ENCHANTMENT;
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
	
	public static boolean harvestBlock(BreakEvent event, IBlockState state, BlockPos pos)
	{
		if(!(event.getPlayer() instanceof EntityPlayerMP))
		{
			return false;
		}
		EntityPlayerMP player = (EntityPlayerMP)event.getPlayer();
		ItemStack stack = event.getPlayer().getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem().onBlockStartBreak(stack, pos, player))
			return false;
		
		World world = event.getWorld();
		world.playEvent(player, 2001, pos, Block.getStateId(state));
		boolean flag1 = false;
		
		if(player.isCreative())
		{
			flag1 = removeBlock(world, pos, player, false);
			player.connection.sendPacket(new SPacketBlockChange(world, pos));
		}
		else
		{
			ItemStack itemstack1 = player.getHeldItemMainhand();
			ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy();
			boolean flag = state.getBlock().canHarvestBlock(world, pos, player);
			
			if(!itemstack1.isEmpty())
			{
				itemstack1.onBlockDestroyed(world, state, pos, player);
				if(itemstack1.isEmpty())
					net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack2, EnumHand.MAIN_HAND);
			}
			
			flag1 = removeBlock(world, pos, player, flag);
			if(flag1 && flag)
			{
				state.getBlock().harvestBlock(world, player, pos, state, world.getTileEntity(pos), itemstack2);
			}
		}
		
		// Drop experience
		if(!player.isCreative() && flag1 && event.getExpToDrop() > 0)
		{
			state.getBlock().dropXpOnBlockBreak(world, pos, event.getExpToDrop());
		}
		return true;
	}
	
	private static boolean removeBlock(World world, BlockPos pos, EntityPlayer player, boolean canHarvest)
	{
		IBlockState iblockstate = world.getBlockState(pos);
		boolean flag = iblockstate.getBlock().removedByPlayer(iblockstate, world, pos, player, canHarvest);
		if(flag)
		{
			iblockstate.getBlock().onPlayerDestroy(world, pos, iblockstate);
		}
		return flag;
	}
	
	public static int drainExperience(EntityPlayer player, int points)
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
	
	public static int getXP(EntityPlayer player)
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
