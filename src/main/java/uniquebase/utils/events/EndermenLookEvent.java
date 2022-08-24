package uniquebase.utils.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EndermenLookEvent extends PlayerEvent
{
	public EndermenLookEvent(Player player)
	{
		super(player);
	}
}
