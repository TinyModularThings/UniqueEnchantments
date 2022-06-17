package uniquebase.api.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;

public class ItemDurabilityChangeEvent extends Event
{
	public final ItemStack item;
	public final LivingEntity entity;
	public final int damageDone;
	
	public ItemDurabilityChangeEvent(ItemStack item, int damageDone, LivingEntity entity)
	{
		this.item = item;
		this.entity = entity;
		this.damageDone = damageDone;
	}
	
}
