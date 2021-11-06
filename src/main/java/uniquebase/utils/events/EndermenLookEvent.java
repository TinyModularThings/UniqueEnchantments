package uniquebase.utils.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EndermenLookEvent extends PlayerEvent
{
	public EndermenLookEvent(PlayerEntity player)
	{
		super(player);
	}
}
