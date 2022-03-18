package uniquebase.handler;

import java.util.Map;

import org.lwjgl.glfw.GLFW;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import uniquebase.UEBase;
import uniquebase.api.IKeyBind;
import uniquebase.networking.KeyPacket;

@OnlyIn(Dist.CLIENT)
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
			UEBase.NETWORKING.sendToServer(new KeyPacket(keyState));			
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
			return isKeyPressed(binding);
		}
		
		@Override
		public boolean test(PlayerEntity t)
		{
			return getState();
		}
		
		public boolean isKeyPressed(KeyBinding binding)
		{
			IKeyConflictContext context = binding.getKeyConflictContext();
			binding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
			boolean result = isKeyDown(binding);
			binding.setKeyConflictContext(context);
			return result;
		}
		
		private boolean isKeyDown(KeyBinding binding)
		{
			Input input = binding.getKey();
			long monitor = Minecraft.getInstance().getWindow().getWindow();
			return input.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(monitor, input.getValue()) == 1 : InputMappings.isKeyDown(monitor, input.getValue());
		}
		
		@Override
		public ITextComponent getKeyName()
		{
			return binding.getTranslatedKeyMessage();
		}
		
		@Override
		public ITextComponent getName()
		{
			return new StringTextComponent(name);
		}
	}
}
