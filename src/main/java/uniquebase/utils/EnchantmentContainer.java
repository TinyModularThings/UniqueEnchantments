package uniquebase.utils;

import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.ints.AbstractInt2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class EnchantmentContainer
{
	private static final EquipmentSlotType[] SLOTS = EquipmentSlotType.values();
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
	
	public Iterable<Int2ObjectMap.Entry<ItemStack>> getEnchantedItems(Enchantment ench)
	{
		triggerAll();
		return new SpecialIterable(ench);
	}
	
	public int getEnchantment(Enchantment ench, EquipmentSlotType slot)
	{
		if(enchantments[slot.ordinal()] == null) 
		{
			enchantments[slot.ordinal()] = MiscUtil.getEnchantments(base.getItemBySlot(slot));
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
						enchantments[slot.ordinal()] = MiscUtil.getEnchantments(base.getItemBySlot(slot));
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
	
	private class SpecialIterable implements Iterable<Int2ObjectMap.Entry<ItemStack>>
	{
		Enchantment ench;
		
		public SpecialIterable(Enchantment ench)
		{
			this.ench = ench;
		}
		
		@Override
		public Iterator<Int2ObjectMap.Entry<ItemStack>> iterator()
		{
			return new SpecialIterator(ench);
		}
	}
	
	private class SpecialIterator implements Iterator<Int2ObjectMap.Entry<ItemStack>>
	{
		Enchantment ench;
		int i = 0;
		int lastResult = -1;
		
		public SpecialIterator(Enchantment ench)
		{
			this.ench = ench;
		}

		private void compute()
		{
			if(lastResult != -1) return;
			while(i < enchantments.length)
			{
				int level = enchantments[i].getInt(ench);
				if(level > 0) 
				{
					lastResult = level;
					return;
				}
				i++;
			}
			lastResult = 0;
		}
		
		@Override
		public boolean hasNext()
		{
			compute();
			return lastResult != 0;
		}

		@Override
		public Int2ObjectMap.Entry<ItemStack> next()
		{
			if(!hasNext()) throw new IllegalStateException();
			Int2ObjectMap.Entry<ItemStack> result = new AbstractInt2ObjectMap.BasicEntry<>(lastResult, base.getItemBySlot(SLOTS[i]));
			lastResult = -1;
			i++;
			return result;
		}
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
	
	public static class ThornsClass {
		public static boolean ONCE = false;
		public static Entity ENT = null;
	}
}
