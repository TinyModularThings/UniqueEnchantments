package uniquebase.utils;

import java.util.EnumMap;
import java.util.UUID;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;

public class SlotUUID
{
	EnumMap<EquipmentSlot, UUID> id = new EnumMap<>(EquipmentSlot.class);

	public SlotUUID(String...array)
	{
		if(array.length != 6) throw new IllegalStateException("6 UUIDs are required");
		for(EquipmentSlot slot : EquipmentSlot.values())
		{
			if(slot.getFilterFlag() < 0 || slot.getFilterFlag() >= 6) continue; //Mods might add Custom slots we dont want them.
			id.put(slot, UUID.fromString(array[slot.getFilterFlag()]));
		}
	}

	//Defaults to Vanilla's UUID generation when the slot isn't supported
	public UUID getId(EquipmentSlot slot)
	{
		return id.getOrDefault(slot, Mth.createInsecureUUID(RandomSource.create()));
	}
}