package uniquebase;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import uniquebase.handler.BaseHandler;

@Mod("uniquebase")
public class UniqueEnchantmentsBase
{
	public UniqueEnchantmentsBase()
	{
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
	}
}
