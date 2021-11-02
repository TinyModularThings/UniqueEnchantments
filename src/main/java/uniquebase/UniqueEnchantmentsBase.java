package uniquebase;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import uniquebase.api.BaseUEMod;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.networking.PacketHandler;

@Mod(modid = "uniquebase", name = "Unique Enchantments Base", version = "1.0.1", guiFactory = "uniquebase.handler.ConfigHandler")
public class UniqueEnchantmentsBase
{
	public static final PacketHandler NETWORKING = new PacketHandler();
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		CropHarvestRegistry.INSTANCE.init();
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new ReloadCommand());
	}
	
	public static class ReloadCommand extends CommandBase
	{
		@Override
		public String getName()
		{
			return "uniquee";
		}

		@Override
		public String getUsage(ICommandSender sender)
		{
			return "reloads the Unique Enchantments Config";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
		{
			sender.sendMessage(new TextComponentString("Reloading Config"));
			BaseUEMod.reload();
			sender.sendMessage(new TextComponentString("Reloaded Config"));
		}
	}
}
