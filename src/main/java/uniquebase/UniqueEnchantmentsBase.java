package uniquebase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.Proxy;
import uniquebase.networking.PacketHandler;

@Mod("uniquebase")
public class UniqueEnchantmentsBase
{
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	
	public UniqueEnchantmentsBase()
	{
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
	}
}
