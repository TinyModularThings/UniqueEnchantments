package uniquee;

import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.client.EnchantmentLayer;
import uniquee.compat.FirstAidHandler;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.complex.EnderMending;
import uniquee.enchantments.complex.Momentum;
import uniquee.enchantments.complex.PerpetualStrike;
import uniquee.enchantments.complex.SmartAss;
import uniquee.enchantments.complex.SpartanWeapon;
import uniquee.enchantments.complex.SwiftBlade;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.curse.PestilencesOdium;
import uniquee.enchantments.simple.AmelioratedBaneOfArthropod;
import uniquee.enchantments.simple.AmelioratedSharpness;
import uniquee.enchantments.simple.AmelioratedSmite;
import uniquee.enchantments.simple.Berserk;
import uniquee.enchantments.simple.BoneCrusher;
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.FocusImpact;
import uniquee.enchantments.simple.Range;
import uniquee.enchantments.simple.SagesBlessing;
import uniquee.enchantments.simple.Swift;
import uniquee.enchantments.simple.TreasurersEyes;
import uniquee.enchantments.simple.Vitae;
import uniquee.enchantments.unique.AlchemistsGrace;
import uniquee.enchantments.unique.AresBlessing;
import uniquee.enchantments.unique.ClimateTranquility;
import uniquee.enchantments.unique.Cloudwalker;
import uniquee.enchantments.unique.Ecological;
import uniquee.enchantments.unique.EnderLibrarian;
import uniquee.enchantments.unique.EnderMarksmen;
import uniquee.enchantments.unique.EndestReap;
import uniquee.enchantments.unique.FastFood;
import uniquee.enchantments.unique.IcarusAegis;
import uniquee.enchantments.unique.IfritsGrace;
import uniquee.enchantments.unique.MidasBlessing;
import uniquee.enchantments.unique.NaturesGrace;
import uniquee.enchantments.unique.PhoenixBlessing;
import uniquee.enchantments.unique.WarriorsGrace;
import uniquee.handler.EntityEvents;
import uniquee.handler.potion.PotionPestilencesOdium;

@Mod(modid = "uniquee", name = "Unique Enchantments", version = "1.9.0", guiFactory = "uniquee.handler.ConfigHandler")
public class UniqueEnchantments
{
	static List<IToggleEnchantment> ENCHANTMENTS = new ObjectArrayList<IToggleEnchantment>();
	//Simple
	public static Enchantment BERSERKER = new Berserk();
	public static Enchantment ADV_SHARPNESS = new AmelioratedSharpness();
	public static Enchantment ADV_SMITE = new AmelioratedSmite();
	public static Enchantment ADV_BANE_OF_ARTHROPODS = new AmelioratedBaneOfArthropod();
	public static Enchantment VITAE = new Vitae();
	public static Enchantment SWIFT = new Swift();
	public static Enchantment SAGES_BLESSING = new SagesBlessing();
	public static Enchantment ENDER_EYES = new EnderEyes();
	public static Enchantment FOCUS_IMPACT = new FocusImpact();
	public static Enchantment BONE_CRUSH = new BoneCrusher();
	public static Enchantment RANGE = new Range();
	public static Enchantment TREASURERS_EYES = new TreasurersEyes();
	
	//Complex
	public static Enchantment SWIFT_BLADE = new SwiftBlade();
	public static Enchantment SPARTAN_WEAPON = new SpartanWeapon();
	public static Enchantment PERPETUAL_STRIKE = new PerpetualStrike();
	public static Enchantment CLIMATE_TRANQUILITY = new ClimateTranquility();
	public static Enchantment MOMENTUM = new Momentum();
	public static Enchantment ENDER_MENDING = new EnderMending();
	public static Enchantment SMART_ASS = new SmartAss();
	
	//Unique
	public static Enchantment WARRIORS_GRACE = new WarriorsGrace();
	public static Enchantment ENDERMARKSMEN = new EnderMarksmen();
	public static Enchantment ARES_BLESSING = new AresBlessing();
	public static Enchantment ALCHEMISTS_GRACE = new AlchemistsGrace();
	public static Enchantment CLOUD_WALKER = new Cloudwalker();
	public static Enchantment FAST_FOOD = new FastFood();
	public static Enchantment NATURES_GRACE = new NaturesGrace();
	public static Enchantment ECOLOGICAL = new Ecological();
	public static Enchantment PHOENIX_BLESSING = new PhoenixBlessing();
	public static Enchantment MIDAS_BLESSING = new MidasBlessing();
	public static Enchantment IFRIDS_GRACE = new IfritsGrace();
	public static Enchantment ICARUS_AEGIS = new IcarusAegis();
	public static Enchantment ENDER_LIBRARIAN = new EnderLibrarian();
	public static Enchantment ENDEST_REAP = new EndestReap();
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM = new PestilencesOdium();
	public static Enchantment DEATHS_ODIUM = new DeathsOdium();
	
	//Potions
	public static Potion PESTILENCES_ODIUM_POTION = new PotionPestilencesOdium();
	
	public static Configuration CONFIG;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		registerEnchantments(BERSERKER, ADV_SHARPNESS, ADV_SMITE, ADV_BANE_OF_ARTHROPODS, VITAE, SWIFT, SAGES_BLESSING, ENDER_EYES, FOCUS_IMPACT, BONE_CRUSH, RANGE, TREASURERS_EYES);
		registerEnchantments(SWIFT_BLADE, SPARTAN_WEAPON, PERPETUAL_STRIKE, CLIMATE_TRANQUILITY, MOMENTUM, ENDER_MENDING, SMART_ASS);
		registerEnchantments(WARRIORS_GRACE, ENDERMARKSMEN, ARES_BLESSING, ALCHEMISTS_GRACE, CLOUD_WALKER, FAST_FOOD, NATURES_GRACE, ECOLOGICAL, PHOENIX_BLESSING, MIDAS_BLESSING, IFRIDS_GRACE, ICARUS_AEGIS, ENDER_LIBRARIAN, ENDEST_REAP);
		registerEnchantments(PESTILENCES_ODIUM, DEATHS_ODIUM);
		ForgeRegistries.POTIONS.register(PESTILENCES_ODIUM_POTION);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FirstAidHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		CONFIG = new Configuration(event.getSuggestedConfigurationFile());
		
		EntityEvents.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniqee.stored.gold.name", MidasBlessing.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniqee.stored.lava.name", IfritsGrace.LAVA_COUNT);
		EntityEvents.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniqee.stored.feather.name", IcarusAegis.FEATHER_TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniqee.stored.repair.name", EnderMending.ENDER_TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.unqiuee.stored.reap.name", EndestReap.REAP_STORAGE);
		
		EntityEvents.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessing.VALIDATOR, MidasBlessing.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGrace.VALIDATOR, IfritsGrace.LAVA_COUNT);
		EntityEvents.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegis.VALIDATOR, IcarusAegis.FEATHER_TAG);
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		for(IToggleEnchantment ench : ENCHANTMENTS) ench.loadIncompats();
		loadConfig();
		if(FMLCommonHandler.instance().getSide().isClient())
		{
			onClientLoad();
		}
		CropHarvestRegistry.INSTANCE.init();
	}
	
	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new ReloadCommand());
	}
	
	@SideOnly(Side.CLIENT)
	public void onClientLoad()
	{
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		for(RenderPlayer player : manager.getSkinMap().values())
		{
			player.addLayer(new EnchantmentLayer());
		}
		for(Entry<Class<? extends Entity>, Render<? extends Entity>> entry : manager.entityRenderMap.entrySet())
		{
			if(entry.getValue() instanceof RenderLivingBase && (AbstractSkeleton.class.isAssignableFrom(entry.getKey()) || EntityZombie.class.isAssignableFrom(entry.getKey())))
			{
				((RenderLivingBase<?>)entry.getValue()).addLayer(new EnchantmentLayer());
			}
		}
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
			CONFIG.save();
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
	
	public static void registerEnchantments(Enchantment...enchantments)
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
			loadConfig();
			sender.sendMessage(new TextComponentString("Reloaded Config"));
		}
		
	}
}
