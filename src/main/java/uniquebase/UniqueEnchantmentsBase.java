package uniquebase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uniquebase.handler.BaseHandler;

@Mod(modid = "uniquebase", name = "Unique Enchantments Base", version = "1.0.0")
public class UniqueEnchantmentsBase
{
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
	}
}
