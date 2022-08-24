package uniquebase.handler;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.UEBase;
import uniquebase.api.IKeyBind;
import uniquebase.gui.TooltipIcon;
import uniquebase.networking.KeyPacket;
import uniquebase.utils.MiscUtil;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends Proxy
{
	Map<String, ClientPlayerKey> keys = Object2ObjectMaps.synchronize(new Object2ObjectLinkedOpenHashMap<>());
	Object2BooleanMap<String> lastKeyState = Object2BooleanMaps.emptyMap();
	public int counter = 0;
	
	@Override
	public void preInit(IEventBus bus)
	{
		bus.addListener(this::registerColor);
		bus.addListener(this::registerComponent);
	}
	
	@Override
	public void init()
	{
		ItemProperties.register(Items.ENCHANTED_BOOK, new ResourceLocation("ue", "enchantment_attributes"), (I, W, L, E) -> {
			Enchantment ench = getEnchantment(I);
			return ench == null ? 0F : (ench.isTradeable() ? 1F : 0F) + (ench.isCurse() ? 2F : 0F) + (ench.isTreasureOnly() ? 4F : 0F);
		});
	}
	
	private void registerComponent(RegisterClientTooltipComponentFactoriesEvent event)
	{
		event.register(TooltipIcon.class, T -> T);
	}
	
	private void registerColor(RegisterColorHandlersEvent.Item event)
	{
		event.register((I, T) -> {
			if(!UEBase.ITEM_COLORING_ENABLED.get()) return -1;
			if(T == 0) return UEBase.getEnchantmentColor(getEnchantment(I)).getTextColor();
			if(T == 1)
			{
				Enchantment ench = getEnchantment(I);
				if(ench != null) return MiscUtil.getFormatting(ench.getRarity()).getColor() | 0xFF000000;
			}
			return -1;
		}, Items.ENCHANTED_BOOK);
	}
	
	private Enchantment getEnchantment(ItemStack stack)
	{
		ListTag list = stack.getItem() == Items.ENCHANTED_BOOK ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags();
		if(list.isEmpty()) return null;
		int index = (counter / 40) % list.size();
		int tries = 0;
		while(tries < list.size())
		{
			Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(new ResourceLocation(list.getCompound(index).getString("id")));
			if(enchantment != null) return enchantment;
			tries++;
			index = ++index % list.size();
		}
		return null;
	}
	
	@Override
	public void update()
	{
		counter++;
		if(Minecraft.getInstance().level == null) return;
		Object2BooleanMap<String> keyState = new Object2BooleanOpenHashMap<>();
		keys.forEach((K, V) -> V.appendState(keyState));
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
		KeyMapping binding;
		
		public ClientPlayerKey(String name, int key)
		{
			this.name = name;
			binding = new KeyMapping(name, key, "UE Keys");
			Options options = Minecraft.getInstance().options;
	        options.keyMappings = ArrayUtils.add(options.keyMappings, binding);
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
		public boolean test(Player t)
		{
			return getState();
		}
		
		public boolean isKeyPressed(KeyMapping binding)
		{
			IKeyConflictContext context = binding.getKeyConflictContext();
			binding.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
			boolean result = isKeyDown(binding);
			binding.setKeyConflictContext(context);
			return result;
		}
		
		private boolean isKeyDown(KeyMapping binding)
		{
			if(binding.isUnbound()) return false;
			Key input = binding.getKey();
			long monitor = Minecraft.getInstance().getWindow().getWindow();
			return input.getType() == Type.MOUSE ? GLFW.glfwGetMouseButton(monitor, input.getValue()) == 1 : InputConstants.isKeyDown(monitor, input.getValue());
		}
		
		@Override
		public Component getKeyName()
		{
			return binding.getTranslatedKeyMessage();
		}
		
		@Override
		public Component getName()
		{
			return Component.literal(name);
		}
	}
}
