package uniqueeutils.misc;

import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;

public class Proxy
{
	Object2BooleanMap<UUID> keyMap = new Object2BooleanOpenHashMap<>();
	
	public void init()
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void updateData(EntityPlayer player, boolean pressed)
	{
		keyMap.put(player.getUniqueID(), pressed);
	}
	
	public boolean isBoostKeyDown(EntityPlayer player)
	{
		return keyMap.getBoolean(player.getUniqueID());
	}
}
