package uniquebase.utils;

import java.util.EnumMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.MathHelper;

public class SlotUUID
{
	EnumMap<EntityEquipmentSlot, UUID> id = new EnumMap<>(EntityEquipmentSlot.class);
	
	public SlotUUID(String...array)
	{
		if(array.length != 6) throw new IllegalStateException("6 UUIDs are required");
		for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
		{
			if(slot.getSlotIndex() < 0 || slot.getSlotIndex() >= 6) continue; //Mods might add Custom slots we dont want them.
			id.put(slot, UUID.fromString(array[slot.getSlotIndex()]));
		}
	}
	
	//Defaults to Vanilla's UUID generation when the slot isn't supported
	public UUID getId(EntityEquipmentSlot slot)
	{
		return id.getOrDefault(slot, MathHelper.getRandomUUID(ThreadLocalRandom.current()));
	}
}
