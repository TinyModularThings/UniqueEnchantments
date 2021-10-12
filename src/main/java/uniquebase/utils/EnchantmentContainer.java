package uniquebase.utils;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;

public class EnchantmentContainer
{
	@SuppressWarnings("unchecked")
	Object2IntMap<Enchantment>[] enchantments = new Object2IntMap[EquipmentSlotType.values().length];
	Object2IntLinkedOpenHashMap<Enchantment> combinedEnchantments = null;
	int checkedSlots = 0;
	LivingEntity base;
	AllIterable iter = new AllIterable();
	
	public EnchantmentContainer(LivingEntity base)
	{
		this.base = base;
	}
	
	public Iterable<Map.Entry<EquipmentSlotType, Object2IntMap<Enchantment>>> getAll()
	{
		triggerAll();
		return iter;
	}
	
	public int getEnchantment(Enchantment ench, EquipmentSlotType slot)
	{
		if(enchantments[slot.ordinal()] == null) 
		{
			enchantments[slot.ordinal()] = MiscUtil.getEnchantments(base.getItemStackFromSlot(slot));
			checkedSlots++;
		}
		return enchantments[slot.ordinal()].getInt(ench);
	}
	
	public Object2IntMap.Entry<EquipmentSlotType> getEnchantedItem(Enchantment ench)
	{
		EquipmentSlotType[] slots = MiscUtil.getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return MiscUtil.NO_ENCHANTMENT;
		for(int i = 0,m=slots.length;i<m;i++)
		{
			int level = getEnchantment(ench, slots[i]);
			if(level > 0) return new AbstractObject2IntMap.BasicEntry<>(slots[i], level);
		}
		return MiscUtil.NO_ENCHANTMENT;
	}
	
	protected void triggerAll()
	{
		if(combinedEnchantments == null)
		{
			if(checkedSlots != enchantments.length)
			{
				for(EquipmentSlotType slot : EquipmentSlotType.values())
				{
					if(enchantments[slot.ordinal()] == null)
					{
						enchantments[slot.ordinal()] = MiscUtil.getEnchantments(base.getItemStackFromSlot(slot));
						checkedSlots++;
					}
				}
			}
			combinedEnchantments = new Object2IntLinkedOpenHashMap<>();
			for(int i = 0;i<checkedSlots;i++)
			{
				for(Object2IntMap.Entry<Enchantment> entry : enchantments[i].object2IntEntrySet())
				{
					combinedEnchantments.addTo(entry.getKey(), entry.getIntValue());
				}
			}
		}
	}
	
	public int getCombinedEnchantment(Enchantment ench)
	{
		triggerAll();
		return combinedEnchantments.getInt(ench);
	}
	
	private class AllIterable implements Iterable<Map.Entry<EquipmentSlotType, Object2IntMap<Enchantment>>>
	{
		@Override
		public Iterator<Entry<EquipmentSlotType, Object2IntMap<Enchantment>>> iterator()
		{
			return new AllIterator();
		}
	}
	
	private class AllIterator implements Iterator<Map.Entry<EquipmentSlotType, Object2IntMap<Enchantment>>>
	{
		EquipmentSlotType[] slots = EquipmentSlotType.values();
		int i = 0;
		@Override
		public boolean hasNext()
		{
			return i < slots.length;
		}

		@Override
		public Entry<EquipmentSlotType, Object2IntMap<Enchantment>> next()
		{
			return new AbstractMap.SimpleEntry<>(slots[i], enchantments[i++]);
		}
	}
}
