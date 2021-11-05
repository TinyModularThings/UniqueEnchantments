package uniqueeutils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
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
import uniqueeutils.enchantments.unique.PegasusSoul;
import uniqueeutils.enchantments.unique.PoseidonsSoul;
import uniqueeutils.enchantments.unique.Reinforced;
import uniqueeutils.enchantments.unique.Resonance;
import uniqueeutils.enchantments.unique.SagesSoul;
import uniqueeutils.handler.UtilsHandler;
import uniqueeutils.misc.ClientProxy;
import uniqueeutils.misc.HighlightPacket;
import uniqueeutils.misc.KeyPacket;
import uniqueeutils.misc.Proxy;
import uniqueeutils.potion.SaturationEffect;

@Mod("uniqueutil")
public class UniqueEnchantmentsUtils extends BaseUEMod
{
	public static Enchantment SLEIPNIRS_GRACE;
	public static Enchantment FAMINES_ODIUM;
	public static Enchantment THICK_PICK;
	public static Enchantment ROCKET_MAN;
	public static Enchantment CLIMBER;
	public static Enchantment PHANES_REGRET;
	public static Enchantment POSEIDONS_SOUL;
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
	public static Enchantment PEGASUS_SOUL;
	
	public static Effect SATURATION;
	
	public static final SoundEvent RESONANCE_SOUND = new SoundEvent(new ResourceLocation("uniqueeutil", "resonance_found"));
	
	public static final Proxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	
	public UniqueEnchantmentsUtils()
	{
		UniqueEnchantmentsBase.NETWORKING.registerInternalPacket(this, KeyPacket.class, KeyPacket::new, 20);
		UniqueEnchantmentsBase.NETWORKING.registerInternalPacket(this, HighlightPacket.class, HighlightPacket::new, 21);
		SATURATION = new SaturationEffect();
		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Effect.class, this::registerPotion);
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UniqueEnchantment-Utils.toml");
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		BaseHandler.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		BaseHandler.INSTANCE.registerAnvilHelper(ANEMOIS_FRAGMENT, AnemoiFragment.FUEL_SOURCE, AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerAnvilHelper(ALCHEMISTS_BLESSING, AlchemistsBlessing.REDSTONE, AlchemistsBlessing.STORED_REDSTONE);
		BaseHandler.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueutil.stored.repair.name", ThickPick.TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ANEMOIS_FRAGMENT, "tooltip.uniqueeutil.stored.fuel.name", AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(ALCHEMISTS_BLESSING, "tooltip.uniqueeutil.stored.redstone.name", AlchemistsBlessing.STORED_REDSTONE);
		BaseHandler.INSTANCE.registerStorageTooltip(SAGES_SOUL, "tooltip.uniqueeutil.stored.soul.name", SagesSoul.STORED_XP);
		BaseHandler.INSTANCE.registerStorageTooltip(REINFORCED, "tooltip.uniqueeutil.stored.shield.name", Reinforced.SHIELD);
		ForgeRegistries.SOUND_EVENTS.register(RESONANCE_SOUND.setRegistryName("resonance_found"));
	}
	
	public void registerPotion(RegistryEvent.Register<Effect> event)
	{
		event.getRegistry().register(SATURATION);
	}
	
	@Override
	protected void loadEnchantments()
	{
		SLEIPNIRS_GRACE = register(new SleipnirsGrace());
		FAMINES_ODIUM = register(new FaminesOdium());
		THICK_PICK = register(new ThickPick());
		ROCKET_MAN = register(new RocketMan());
		CLIMBER = register(new Climber());
		PHANES_REGRET = register(new PhanesRegret());
		POSEIDONS_SOUL = register(new PoseidonsSoul());
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
		PEGASUS_SOUL = register(new PegasusSoul());
	}
}
