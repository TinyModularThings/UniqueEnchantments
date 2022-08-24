package uniquebase.utils.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class PiglinWearableCheckEvent extends Event
{
	LivingEntity entity;
	
	public PiglinWearableCheckEvent(LivingEntity entity)
	{
		this.entity = entity;
	}
	
	public LivingEntity getEntity()
	{
		return entity;
	}
}
