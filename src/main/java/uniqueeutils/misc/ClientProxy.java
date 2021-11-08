package uniqueeutils.misc;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Input;
import net.minecraft.client.util.InputMappings.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
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
		boostKey = new KeyBinding("Effect Key", GLFW.GLFW_KEY_LEFT_CONTROL, "UE Keys");
		ClientRegistry.registerKeyBinding(boostKey);
	}
	
	@Override
	public void update()
	{
		int newState = (isKeyPressed(boostKey) ? 1 : 0);
		if(newState != lastState)
		{
			lastState = newState;
			UniqueEnchantmentsBase.NETWORKING.sendToServer(new KeyPacket(newState));
			updateData(Minecraft.getInstance().player, newState);
		}
	}
	
	public boolean isKeyPressed(KeyBinding binding)
	{
		IKeyConflictContext context = binding.getKeyConflictContext();
		binding.setKeyConflictContext(KeyConflictContext.IN_GAME);
		boolean result = isKeyDown(binding) && binding.getKeyConflictContext().isActive() && binding.getKeyModifier().isActive(binding.getKeyConflictContext());
		binding.setKeyConflictContext(context);
		return result;
	}
	
	private boolean isKeyDown(KeyBinding binding)
	{
		Input input = binding.getKey();
		return input.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(Minecraft.getInstance().mainWindow.getHandle(), input.getKeyCode()) == 1 : InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.getHandle(), input.getKeyCode());
	}
}
