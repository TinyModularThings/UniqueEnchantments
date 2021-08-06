package uniquee;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uniquebase.api.BaseUEMod;
import uniquebase.api.IToggleEnchantment;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquee.client.EnchantmentLayer;
import uniquee.compat.FirstAidHandler;
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
import uniquee.enchantments.unique.Grimoire;
import uniquee.enchantments.unique.IcarusAegis;
import uniquee.enchantments.unique.IfritsGrace;
import uniquee.enchantments.unique.MidasBlessing;
import uniquee.enchantments.unique.NaturesGrace;
import uniquee.enchantments.unique.PhoenixBlessing;
import uniquee.enchantments.unique.WarriorsGrace;
import uniquee.handler.EntityEvents;
import uniquee.handler.potion.PotionPestilencesOdium;

@Mod(modid = "uniquee", name = "Unique Enchantments", version = "2.0.0")
public class UniqueEnchantments extends BaseUEMod
{
	static List<IToggleEnchantment> ENCHANTMENTS = new ObjectArrayList<IToggleEnchantment>();
	//Simple
	public static Enchantment BERSERKER;
	public static Enchantment ADV_SHARPNESS;
	public static Enchantment ADV_SMITE;
	public static Enchantment ADV_BANE_OF_ARTHROPODS;
	public static Enchantment VITAE;
	public static Enchantment SWIFT;
	public static Enchantment SAGES_BLESSING;
	public static Enchantment ENDER_EYES;
	public static Enchantment FOCUS_IMPACT;
	public static Enchantment BONE_CRUSH;
	public static Enchantment RANGE;
	public static Enchantment TREASURERS_EYES;
	
	//Complex
	public static Enchantment SWIFT_BLADE;
	public static Enchantment SPARTAN_WEAPON;
	public static Enchantment PERPETUAL_STRIKE;
	public static Enchantment CLIMATE_TRANQUILITY;
	public static Enchantment MOMENTUM;
	public static Enchantment ENDER_MENDING;
	public static Enchantment SMART_ASS;
	
	//Unique
	public static Enchantment WARRIORS_GRACE;
	public static Enchantment ENDERMARKSMEN;
	public static Enchantment ARES_BLESSING;
	public static Enchantment ALCHEMISTS_GRACE;
	public static Enchantment CLOUD_WALKER;
	public static Enchantment FAST_FOOD;
	public static Enchantment NATURES_GRACE;
	public static Enchantment ECOLOGICAL;
	public static Enchantment PHOENIX_BLESSING;
	public static Enchantment MIDAS_BLESSING;
	public static Enchantment IFRIDS_GRACE;
	public static Enchantment ICARUS_AEGIS;
	public static Enchantment ENDER_LIBRARIAN;
	public static Enchantment ENDEST_REAP;
	public static Enchantment GRIMOIRE;
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM;
	public static Enchantment DEATHS_ODIUM;
	
	//Potions
	public static Potion PESTILENCES_ODIUM_POTION = new PotionPestilencesOdium();
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		ForgeRegistries.POTIONS.register(PESTILENCES_ODIUM_POTION);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(FirstAidHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		init("uniquee", new File(event.getModConfigurationDirectory(), "UniqueEnchantments.cfg"));
		BaseHandler.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniqee.stored.gold.name", MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniqee.stored.lava.name", IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniqee.stored.feather.name", IcarusAegis.FEATHER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniqee.stored.repair.name", EnderMending.ENDER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.unqiuee.stored.reap.name", EndestReap.REAP_STORAGE);
		
		BaseHandler.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessing.VALIDATOR, MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGrace.VALIDATOR, IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegis.VALIDATOR, IcarusAegis.FEATHER_TAG);
	}
	
	@Override
	protected void addEnchantments()
	{
		BERSERKER = register(new Berserk());
		ADV_SHARPNESS = register(new AmelioratedSharpness());
		ADV_SMITE = register(new AmelioratedSmite());
		ADV_BANE_OF_ARTHROPODS = register(new AmelioratedBaneOfArthropod());
		VITAE = register(new Vitae());
		SWIFT = register(new Swift());
		SAGES_BLESSING = register(new SagesBlessing());
		ENDER_EYES = register(new EnderEyes());
		FOCUS_IMPACT = register(new FocusImpact());
		BONE_CRUSH = register(new BoneCrusher());
		RANGE = register(new Range());
		TREASURERS_EYES = register(new TreasurersEyes());
		
		
		SWIFT_BLADE = register(new SwiftBlade());
		SPARTAN_WEAPON = register(new SpartanWeapon());
		PERPETUAL_STRIKE = register(new PerpetualStrike());
		CLIMATE_TRANQUILITY = register(new ClimateTranquility());
		MOMENTUM = register(new Momentum());
		ENDER_MENDING = register(new EnderMending());
		SMART_ASS = register(new SmartAss());
		
		
		WARRIORS_GRACE = register(new WarriorsGrace());
		ENDERMARKSMEN = register(new EnderMarksmen());
		ARES_BLESSING = register(new AresBlessing());
		ALCHEMISTS_GRACE = register(new AlchemistsGrace());
		CLOUD_WALKER = register(new Cloudwalker());
		FAST_FOOD = register(new FastFood());
		NATURES_GRACE = register(new NaturesGrace());
		ECOLOGICAL = register(new Ecological());
		PHOENIX_BLESSING = register(new PhoenixBlessing());
		MIDAS_BLESSING = register(new MidasBlessing());
		IFRIDS_GRACE = register(new IfritsGrace());
		ICARUS_AEGIS = register(new IcarusAegis());
		ENDER_LIBRARIAN = register(new EnderLibrarian());
		ENDEST_REAP = register(new EndestReap());
		GRIMOIRE = register(new Grimoire());
		
		PESTILENCES_ODIUM = register(new PestilencesOdium());
		DEATHS_ODIUM = register(new DeathsOdium());
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent event)
	{
		if(FMLCommonHandler.instance().getSide().isClient())
		{
			onClientLoad();
		}
		CropHarvestRegistry.INSTANCE.init();
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
}