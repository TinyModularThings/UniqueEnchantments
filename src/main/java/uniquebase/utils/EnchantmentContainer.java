package uniquebase.utils;

import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentContainer
{
	@SuppressWarnings("unchecked")
	Object2IntMap<Enchantment>[] enchantments = new Object2IntMap[EntityEquipmentSlot.values().length];
	Object2IntLinkedOpenHashMap<Enchantment> combinedEnchantments = null;
	int checkedSlots = 0;
	EntityLivingBase base;
	
	public EnchantmentContainer(EntityLivingBase base)
	{
		this.base = base;
	}
	
	public int getEnchantment(Enchantment ench, EntityEquipmentSlot slot)
	{
		if(enchantments[slot.ordinal()] == null) 
		{
			enchantments[slot.ordinal()] = MiscUtil.getEnchantments(base.getItemStackFromSlot(slot));
			checkedSlots++;
		}
		return enchantments[slot.ordinal()].getInt(ench);
	}
	
	public Object2IntMap.Entry<EntityEquipmentSlot> getEnchantedItem(Enchantment ench)
	{
		EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(ench);
		if(slots.length <= 0) return MiscUtil.NO_ENCHANTMENT;
		for(int i = 0,m=slots.length;i<m;i++)
		{
			int level = getEnchantment(ench, slots[i]);
			if(level > 0) return new AbstractObject2IntMap.BasicEntry<>(slots[i], level);
		}
		return MiscUtil.NO_ENCHANTMENT;
	}
	
	public int getCombinedEnchantment(Enchantment ench)
	{
		if(combinedEnchantments == null)
		{
			if(checkedSlots != enchantments.length)
			{
				for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
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
		return combinedEnchantments.getInt(ench);
	}
}
