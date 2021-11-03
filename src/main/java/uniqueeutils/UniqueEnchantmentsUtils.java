package uniqueeutils;

import java.io.File;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.api.BaseUEMod;
import uniquebase.handler.BaseHandler;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.DemetersBlessing;
import uniqueeutils.enchantments.complex.EssenceOfSlime;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.PhanesRegret;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.Adept;
import uniqueeutils.enchantments.simple.ThickPick;
import uniqueeutils.enchantments.unique.AnemoiFragment;
import uniqueeutils.enchantments.unique.DemetersSoul;
import uniqueeutils.enchantments.unique.MountingAegis;
import uniqueeutils.enchantments.unique.Reinforced;
import uniqueeutils.enchantments.unique.Resonance;
import uniqueeutils.enchantments.unique.SagesSoul;
import uniqueeutils.handler.UtilsHandler;
import uniqueeutils.misc.KeyPacket;
import uniqueeutils.misc.Proxy;
import uniqueeutils.potion.SaturationPotion;

@Mod(modid = "uniqueeutil", name = "Unique Util Enchantments", version = "1.0.1", dependencies = "required-after:uniquebase@[1.0.0,);")
public class UniqueEnchantmentsUtils extends BaseUEMod
{
	public static Enchantment SLEIPNIRS_GRACE;
	public static Enchantment FAMINES_ODIUM;
	public static Enchantment THICK_PICK;
	public static Enchantment ROCKET_MAN;
	public static Enchantment CLIMBER;
	public static Enchantment PHANES_REGRET;
	public static Enchantment MOUNTING_AEGIS;
	public static Enchantment DETEMERS_BLESSING;
	public static Enchantment DEMETERS_SOUL;
	public static Enchantment AMBROSIA;
	public static Enchantment ESSENCE_OF_SLIME;
	public static Enchantment ADEPT;
	public static Enchantment ALCHEMISTS_BLESSING;
	public static Enchantment ANEMOIS_FRAGMENT;
	public static Enchantment REINFORCED;
	public static Enchantment RESONANCE;
	public static Enchantment SAGES_SOUL;
	
	public static Potion SATURATION = new SaturationPotion();
	
	public static final SoundEvent RESONANCE_SOUND = new SoundEvent(new ResourceLocation("uniqueeutil", "resonance_found"));
	
	@SidedProxy(clientSide = "uniqueeutils.misc.ClientProxy", serverSide = "uniqueeutils.misc.Proxy")
	public static Proxy PROXY;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		PROXY.init();
		UniqueEnchantmentsBase.NETWORKING.registerInternalPacket(this, KeyPacket.class, 20);
		ForgeRegistries.POTIONS.register(SATURATION.setRegistryName("saturation"));
		init("uniqueeutil", new File(event.getModConfigurationDirectory(), "UniqueEnchantments_Utils.cfg"));
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		BaseHandler.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		BaseHandler.INSTANCE.registerAnvilHelper(ANEMOIS_FRAGMENT, AnemoiFragment.FUEL_SOURCE, AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerAnvilHelper(ALCHEMISTS_BLESSING, AlchemistsBlessing.REDSTONE, AlchemistsBlessing.STORED_REDSTONE);
		BaseHandler.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueeutil.stored.repair.name", ThickPick.TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ANEMOIS_FRAGMENT, "tooltip.uniqueeutil.stored.fuel.name", AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(ALCHEMISTS_BLESSING, "tooltip.uniqueeutil.stored.redstone.name", AlchemistsBlessing.STORED_REDSTONE);
		ForgeRegistries.SOUND_EVENTS.register(RESONANCE_SOUND.setRegistryName("resonance_found"));
	}
	
	@Override
	protected void addEnchantments()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGrace());
		FAMINES_ODIUM = register(new FaminesOdium());
		THICK_PICK = register(new ThickPick());
		ROCKET_MAN = register(new RocketMan());
		CLIMBER = register(new Climber());
		PHANES_REGRET = register(new PhanesRegret());
		MOUNTING_AEGIS = register(new MountingAegis());
		DETEMERS_BLESSING = register(new DemetersBlessing());
		DEMETERS_SOUL = register(new DemetersSoul());
		AMBROSIA = register(new Ambrosia());
		ESSENCE_OF_SLIME = register(new EssenceOfSlime());
		ADEPT = register(new Adept());
		ALCHEMISTS_BLESSING = register(new AlchemistsBlessing());
		ANEMOIS_FRAGMENT = register(new AnemoiFragment());
		REINFORCED = register(new Reinforced());
		RESONANCE = register(new Resonance());
		SAGES_SOUL = register(new SagesSoul());
	}
}