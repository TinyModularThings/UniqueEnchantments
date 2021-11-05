package uniqueeutils.misc;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;

public class Proxy
{
	Object2IntMap<UUID> keyMap = new Object2IntOpenHashMap<>();
	
	public void init()
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void updateData(PlayerEntity player, int pressed)
	{
		keyMap.put(player.getUniqueID(), pressed);
	}
	
	public boolean isBoostKeyDown(PlayerEntity player)
	{
		return (keyMap.getInt(player.getUniqueID()) & 1) != 0;
	}
}
