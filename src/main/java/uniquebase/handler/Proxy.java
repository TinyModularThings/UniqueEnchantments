package uniquebase.handler;

import java.util.Map;
import java.util.UUID;

import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import uniquebase.api.IKeyBind;

public class Proxy
{
	Map<UUID, PlayerKeys> keyMap = new Object2ObjectOpenHashMap<>();
	
	public void preInit(IEventBus bus)
	{
		
	}
	
	public void init()
	{
		
	}
	
	public void update()
	{
		
	}
	
	public void updateData(Player player, Object2BooleanMap<String> pressed)
	{
		keyMap.computeIfAbsent(player.getUUID(), T -> new PlayerKeys()).update(pressed);
	}
	
	public IKeyBind registerKey(String name, int keyBinding)
	{
		return new PlayerKey(name, keyMap);
	}
	
	public static class PlayerKey implements IKeyBind
	{
		String key;
		Map<UUID, PlayerKeys> keyMap;
		
		public PlayerKey(String key, Map<UUID, PlayerKeys> keyMap)
		{
			this.key = key;
			this.keyMap = keyMap;
		}
		
		@Override
		public boolean test(Player t)
		{
			PlayerKeys keys = keyMap.get(t.getUUID());
			return keys != null && keys.isPressed(key);
		}
		
		@Override
		public Component getKeyName()
		{
			return Component.literal("Names only Exist in the Client Side");
		}
		
		@Override
		public Component getName()
		{
			return Component.literal(key);
		}
	}
	
	public static class PlayerKeys
	{
		Object2BooleanMap<String> keyNames = new Object2BooleanLinkedOpenHashMap<>();
		
		public void update(Object2BooleanMap<String> newKeys)
		{
			keyNames.clear();
			keyNames.putAll(newKeys);
		}
		
		public boolean isPressed(String name)
		{
			return keyNames.getBoolean(name);
		}
	}

	public Enchantment getEnchantment(ItemStack stack)
	{
		return null;
	}
}
