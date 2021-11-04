package uniqueeutils.misc;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquebase.UniqueEnchantmentsBase;

@SideOnly(Side.CLIENT)
public class ClientProxy extends Proxy
{
	KeyBinding boostKey;
	int lastState = 0;
	
	@Override
	public void init()
	{
		boostKey = new KeyBinding("AnemoiFragment Boost Key", 29, "UE Keys");
		ClientRegistry.registerKeyBinding(boostKey);
	}
	
	@Override
	public void update()
	{
		int newState = (GameSettings.isKeyDown(boostKey) ? 1 : 0);
		if(newState != lastState)
		{
			lastState = newState;
			UniqueEnchantmentsBase.NETWORKING.sendToServer(new KeyPacket(newState));
		}
	}
	
	@Override
	public boolean isBoostKeyDown(EntityPlayer player)
	{
		return GameSettings.isKeyDown(boostKey);
	}
}
