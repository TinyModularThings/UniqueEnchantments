package uniquee;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import uniquee.compat.FirstAidHandler;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.complex.EnchantmentJokersBlessing;
import uniquee.enchantments.complex.EnchantmentMomentum;
import uniquee.enchantments.complex.EnchantmentPerpetualStrike;
import uniquee.enchantments.complex.EnchantmentSpartanWeapon;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;
import uniquee.enchantments.simple.EnchantmentAdvancedDamage;
import uniquee.enchantments.simple.EnchantmentBerserk;
import uniquee.enchantments.simple.EnchantmentEnderEyes;
import uniquee.enchantments.simple.EnchantmentSagesBlessing;
import uniquee.enchantments.simple.EnchantmentSwift;
import uniquee.enchantments.simple.EnchantmentVitae;
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentClimateTranquility;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentIfritsGrace;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentPhoenixBlessing;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.handler.EntityEvents;

@Mod(modid = "uniquee", name = "Unique Enchantments", version = "1.3.0", guiFactory = "uniquee.handler.ConfigHandler")
public class UniqueEnchantments
{
	static List<IToggleEnchantment> ENCHANTMENTS = new ObjectArrayList<IToggleEnchantment>();
	//Simple
	public static Enchantment BERSERKER = new EnchantmentBerserk();
	public static Enchantment ADV_SHARPNESS = new EnchantmentAdvancedDamage(0);
	public static Enchantment ADV_SMITE = new EnchantmentAdvancedDamage(1);
	public static Enchantment ADV_BANE_OF_ARTHROPODS = new EnchantmentAdvancedDamage(2);
	public static Enchantment VITAE = new EnchantmentVitae();
	public static Enchantment SWIFT = new EnchantmentSwift();
	public static Enchantment SAGES_BLESSING = new EnchantmentSagesBlessing();
	public static Enchantment ENDER_EYES = new EnchantmentEnderEyes();
	
	//Complex
	public static Enchantment SWIFT_BLADE = new EnchantmentSwiftBlade();
	public static Enchantment SPARTAN_WEAPON = new EnchantmentSpartanWeapon();
	public static Enchantment PERPETUAL_STRIKE = new EnchantmentPerpetualStrike();
	public static Enchantment CLIMATE_TRANQUILITY = new EnchantmentClimateTranquility();
	public static Enchantment MOMENTUM = new EnchantmentMomentum();
	//Disabled because to high exploits
	public static Enchantment JOKERS_BLESSING = new EnchantmentJokersBlessing();
	
	//Unique
	public static Enchantment WARRIORS_GRACE = new EnchantmentWarriorsGrace();
	public static Enchantment ENDERMARKSMEN = new EnchantmentEnderMarksmen();
	public static Enchantment ARES_BLESSING = new EnchantmentAresBlessing();
	public static Enchantment ALCHEMISTS_GRACE = new EnchantmentAlchemistsGrace();
	public static Enchantment CLOUD_WALKER = new EnchantmentCloudwalker();
	public static Enchantment FAST_FOOD = new EnchantmentFastFood();
	public static Enchantment NATURES_GRACE = new EnchantmentNaturesGrace();
	public static Enchantment ECOLOGICAL = new EnchantmentEcological();
	public static Enchantment PHOENIX_BLESSING = new EnchantmentPhoenixBlessing();
	public static Enchantment MIDAS_BLESSING = new EnchantmentMidasBlessing();
	public static Enchantment IFRIDS_GRACE = new EnchantmentIfritsGrace();
	
	public static Configuration CONFIG;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		IForgeRegistry<Enchantment> registry = ForgeRegistries.ENCHANTMENTS;
		registerEnchantments(BERSERKER, ADV_SHARPNESS, ADV_SMITE, ADV_BANE_OF_ARTHROPODS, VITAE, SWIFT, SAGES_BLESSING, ENDER_EYES);
		registerEnchantments(SWIFT_BLADE, SPARTAN_WEAPON, PERPETUAL_STRIKE, CLIMATE_TRANQUILITY, MOMENTUM);
		registerEnchantments(WARRIORS_GRACE, ENDERMARKSMEN, ARES_BLESSING, ALCHEMISTS_GRACE, CLOUD_WALKER, FAST_FOOD, NATURES_GRACE, ECOLOGICAL, PHOENIX_BLESSING, MIDAS_BLESSING, IFRIDS_GRACE);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FirstAidHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		loadConfig();
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new ReloadCommand());
	}
	
	@SubscribeEvent
	public void onConfigChange(OnConfigChangedEvent evt)
	{
		if(evt.getModID().equalsIgnoreCase("uniquee"))
		{
			return;
		}
		try
		{
			for(IToggleEnchantment ench : ENCHANTMENTS)
			{
				ench.loadFromConfig(CONFIG);
			}
		}
		catch(Exception e)
		{
		}
	}
	
	public static void loadConfig()
	{
		try
		{
			CONFIG.load();
			for(IToggleEnchantment ench : ENCHANTMENTS)
			{
				ench.loadFromConfig(CONFIG);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			CONFIG.save();
		}
	}
	
	private void registerEnchantments(Enchantment...enchantments)
	{
		ForgeRegistries.ENCHANTMENTS.registerAll(enchantments);
		for(Enchantment enchantment : enchantments)
		{
			if(enchantment instanceof IToggleEnchantment)
			{
				ENCHANTMENTS.add((IToggleEnchantment)enchantment);
			}
		}
	}
	
	public static class ReloadCommand extends CommandBase
	{

		@Override
		public String getName()
		{
			return "unique_enchantments";
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
			loadConfig();
			sender.sendMessage(new TextComponentString("Reloaded Config"));
		}
		
	}
}
