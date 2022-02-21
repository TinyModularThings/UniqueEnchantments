package uniquebase.handler;

import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.api.IKeyBind;
import uniquebase.networking.KeyPacket;

@SideOnly(Side.CLIENT)
public class ClientProxy extends Proxy
{
	Map<String, ClientPlayerKey> keys = new Object2ObjectLinkedOpenHashMap<>();
	Object2BooleanMap<String> lastKeyState = Object2BooleanMaps.emptyMap();
	
	@Override
	public void update()
	{
		Object2BooleanMap<String> keyState = new Object2BooleanOpenHashMap<>();
		for(ClientPlayerKey key : keys.values())
		{
			key.appendState(keyState);
		}
		if(!lastKeyState.equals(keyState))
		{
			lastKeyState = keyState.isEmpty() ? Object2BooleanMaps.emptyMap() : keyState;
			UniqueEnchantmentsBase.NETWORKING.sendToServer(new KeyPacket(keyState));			
		}
	}
	
	@Override
	public IKeyBind registerKey(String name, int keyBinding)
	{
		return keys.computeIfAbsent(name, T -> new ClientPlayerKey(T, keyBinding));
	}
	
	public class ClientPlayerKey implements IKeyBind
	{
		String name;
		KeyBinding binding;
		
		public ClientPlayerKey(String name, int key)
		{
			this.name = name;
			binding = new KeyBinding(name, key, "UE Keys");
			ClientRegistry.registerKeyBinding(binding);
		}
		
		public void appendState(Object2BooleanMap<String> map)
		{
			map.put(name, getState());
		}
		
		public boolean getState()
		{
			return GameSettings.isKeyDown(binding);
		}
		
		@Override
		public boolean test(EntityPlayer t)
		{
			return getState();
		}
		
		@Override
		public String getKeyName()
		{
			return binding.getDisplayName();
		}
		
		@Override
		public String getName()
		{
			return name;
		}
	}
}
