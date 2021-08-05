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
import uniquee.enchantments.complex.EnchantmentEnderMending;
import uniquee.enchantments.complex.EnchantmentMomentum;
import uniquee.enchantments.complex.EnchantmentPerpetualStrike;
import uniquee.enchantments.complex.EnchantmentSmartAss;
import uniquee.enchantments.complex.EnchantmentSpartanWeapon;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;
import uniquee.enchantments.curse.EnchantmentDeathsOdium;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;
import uniquee.enchantments.simple.EnchantmentAmelioratedBaneOfArthropod;
import uniquee.enchantments.simple.EnchantmentAmelioratedSharpness;
import uniquee.enchantments.simple.EnchantmentAmelioratedSmite;
import uniquee.enchantments.simple.EnchantmentBerserk;
import uniquee.enchantments.simple.EnchantmentBoneCrusher;
import uniquee.enchantments.simple.EnchantmentEnderEyes;
import uniquee.enchantments.simple.EnchantmentFocusImpact;
import uniquee.enchantments.simple.EnchantmentRange;
import uniquee.enchantments.simple.EnchantmentSagesBlessing;
import uniquee.enchantments.simple.EnchantmentSwift;
import uniquee.enchantments.simple.EnchantmentTreasurersEyes;
import uniquee.enchantments.simple.EnchantmentVitae;
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentClimateTranquility;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderLibrarian;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentEndestReap;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentIcarusAegis;
import uniquee.enchantments.unique.EnchantmentIfritsGrace;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentPhoenixBlessing;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.handler.EntityEvents;
import uniquee.handler.potion.PotionPestilencesOdium;

@Mod(modid = "uniquee", name = "Unique Enchantments", version = "1.9.0", guiFactory = "uniquee.handler.ConfigHandler")
public class UniqueEnchantments
{
	static List<IToggleEnchantment> ENCHANTMENTS = new ObjectArrayList<IToggleEnchantment>();
	//Simple
	public static Enchantment BERSERKER = new EnchantmentBerserk();
	public static Enchantment ADV_SHARPNESS = new EnchantmentAmelioratedSharpness();
	public static Enchantment ADV_SMITE = new EnchantmentAmelioratedSmite();
	public static Enchantment ADV_BANE_OF_ARTHROPODS = new EnchantmentAmelioratedBaneOfArthropod();
	public static Enchantment VITAE = new EnchantmentVitae();
	public static Enchantment SWIFT = new EnchantmentSwift();
	public static Enchantment SAGES_BLESSING = new EnchantmentSagesBlessing();
	public static Enchantment ENDER_EYES = new EnchantmentEnderEyes();
	public static Enchantment FOCUS_IMPACT = new EnchantmentFocusImpact();
	public static Enchantment BONE_CRUSH = new EnchantmentBoneCrusher();
	public static Enchantment RANGE = new EnchantmentRange();
	public static Enchantment TREASURERS_EYES = new EnchantmentTreasurersEyes();
	
	//Complex
	public static Enchantment SWIFT_BLADE = new EnchantmentSwiftBlade();
	public static Enchantment SPARTAN_WEAPON = new EnchantmentSpartanWeapon();
	public static Enchantment PERPETUAL_STRIKE = new EnchantmentPerpetualStrike();
	public static Enchantment CLIMATE_TRANQUILITY = new EnchantmentClimateTranquility();
	public static Enchantment MOMENTUM = new EnchantmentMomentum();
	public static Enchantment ENDER_MENDING = new EnchantmentEnderMending();
	public static Enchantment SMART_ASS = new EnchantmentSmartAss();
	
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
	public static Enchantment ICARUS_AEGIS = new EnchantmentIcarusAegis();
	public static Enchantment ENDER_LIBRARIAN = new EnchantmentEnderLibrarian();
	public static Enchantment ENDEST_REAP = new EnchantmentEndestReap();
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM = new EnchantmentPestilencesOdium();
	public static Enchantment DEATHS_ODIUM = new EnchantmentDeathsOdium();
	
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
		
		EntityEvents.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniqee.stored.gold.name", EnchantmentMidasBlessing.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniqee.stored.lava.name", EnchantmentIfritsGrace.LAVA_COUNT);
		EntityEvents.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniqee.stored.feather.name", EnchantmentIcarusAegis.FEATHER_TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniqee.stored.repair.name", EnchantmentEnderMending.ENDER_TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.unqiuee.stored.reap.name", EnchantmentEndestReap.REAP_STORAGE);
		
		EntityEvents.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, EnchantmentMidasBlessing.VALIDATOR, EnchantmentMidasBlessing.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, EnchantmentIfritsGrace.VALIDATOR, EnchantmentIfritsGrace.LAVA_COUNT);
		EntityEvents.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, EnchantmentIcarusAegis.VALIDATOR, EnchantmentIcarusAegis.FEATHER_TAG);
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
