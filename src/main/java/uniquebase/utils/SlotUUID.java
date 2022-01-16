package uniquebase.utils;

import java.util.EnumMap;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.MathHelper;

public class SlotUUID
{
	EnumMap<EquipmentSlotType, UUID> id = new EnumMap<>(EquipmentSlotType.class);

	public SlotUUID(String...array)
	{
		if(array.length != 6) throw new IllegalStateException("6 UUIDs are required");
		for(EquipmentSlotType slot : EquipmentSlotType.values())
		{
			if(slot.getFilterFlag() < 0 || slot.getFilterFlag() >= 6) continue; //Mods might add Custom slots we dont want them.
			id.put(slot, UUID.fromString(array[slot.getFilterFlag()]));
		}
	}

	//Defaults to Vanilla's UUID generation when the slot isn't supported
	public UUID getId(EquipmentSlotType slot)
	{
		return id.getOrDefault(slot, MathHelper.createInsecureUUID(ThreadLocalRandom.current()));
	}
}