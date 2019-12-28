package uniquee.utils;

import java.util.Collections;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class MiscUtil
{
	public static int getEnchantmentLevel(Enchantment ench, ItemStack stack)
	{
		if(stack.isEmpty())
		{
			return 0;
		}
		NBTTagList list = stack.getEnchantmentTagList();
		if(list.hasNoTags())
		{
			return 0;
		}
		int id = Enchantment.getEnchantmentID(ench);
		for(int i = 0,m=list.tagCount();i<m;i++)
		{
            NBTTagCompound tag = list.getCompoundTagAt(i);
            if(tag.getInteger("id") == id)
            {
            	//Only grabbing Level if it is needed. Not wasting CPU Time on grabbing useless data
            	return tag.getInteger("lvl");
            }
		}
		return 0;
	}
	
	public static Object2IntMap<Enchantment> getEnchantments(ItemStack stack)
	{
		if(stack.isEmpty())
		{
			return Object2IntMaps.emptyMap();
		}
		NBTTagList list = stack.getEnchantmentTagList();
		//Micro Optimization. If the EnchantmentMap is empty then returning a EmptyMap is faster then creating a new map. More Performance in checks.
		if(list.hasNoTags())
		{
			return Object2IntMaps.emptyMap();
		}
		Object2IntMap<Enchantment> map = new Object2IntOpenHashMap<Enchantment>();
		for(int i = 0,m=list.tagCount();i<m;i++)
		{
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Enchantment enchantment = Enchantment.getEnchantmentByID(tag.getShort("id"));
            if(enchantment != null)
            {
            	//Only grabbing Level if it is needed. Not wasting CPU Time on grabbing useless data
            	map.put(enchantment, tag.getInteger("lvl"));
            }
		}
		return map;
	}
	
	public static EntityEquipmentSlot[] getEquipmentSlotsFor(Enchantment ench)
	{
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
		EntityEquipmentSlot[] slots = getEquipmentSlotsFor(ench);
		return slots.length <= 0 ? Collections.emptySet() : new ObjectOpenHashSet(slots);
	}
	
	public static Object2IntMap.Entry<EntityEquipmentSlot> getEnchantedItem(Enchantment enchantment, EntityLivingBase base)
	{
		EntityEquipmentSlot[] slots = getEquipmentSlotsFor(enchantment);
		if(slots.length <= 0)
		{
			return new AbstractObject2IntMap.BasicEntry(null, 0);
		}
		for(int i = 0;i<slots.length;i++)
		{
			int level = getEnchantmentLevel(enchantment, base.getItemStackFromSlot(slots[i]));
			if(level > 0)
			{
				return new AbstractObject2IntMap.BasicEntry(slots[i], level);
			}
		}
		return new AbstractObject2IntMap.BasicEntry(null, 0);
	}
}
