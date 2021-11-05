package uniqueeutils.misc;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import uniquebase.UniqueEnchantmentsBase;

@OnlyIn(Dist.CLIENT)
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
		int newState = (boostKey.isKeyDown() ? 1 : 0);
		if(newState != lastState)
		{
			lastState = newState;
			UniqueEnchantmentsBase.NETWORKING.sendToServer(new KeyPacket(newState));
		}
	}
	
	@Override
	public boolean isBoostKeyDown(PlayerEntity player)
	{
		return boostKey.isKeyDown();
	}
}
