package uniquebase.api.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ItemDurabilityChangeEvent extends Event
{
	public final ItemStack item;
	public final LivingEntity entity;
	
	public ItemDurabilityChangeEvent(ItemStack item, LivingEntity entity)
	{
		this.item = item;
		this.entity = entity;
	}
	
}
