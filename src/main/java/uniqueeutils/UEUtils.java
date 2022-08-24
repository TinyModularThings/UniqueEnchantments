package uniqueeutils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.UEBase;
import uniquebase.api.BaseUEMod;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.api.IKeyBind;
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
import uniqueeutils.enchantments.upgrades.FaminesUpgrade;
import uniqueeutils.enchantments.upgrades.PhanesUpgrade;
import uniqueeutils.enchantments.upgrades.RocketUpgrade;
import uniqueeutils.enchantments.upgrades.ThickUpgrade;
import uniqueeutils.handler.UtilsHandler;
import uniqueeutils.misc.HighlightPacket;
import uniqueeutils.potion.SaturationEffect;

@Mod("uniqueutil")
public class UEUtils extends BaseUEMod
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
	
	public static MobEffect SATURATION;
	
	public static final SoundEvent RESONANCE_SOUND = new SoundEvent(new ResourceLocation("uniqueutil", "resonance_found"));
	public static final SoundEvent ALCHEMIST_BLESSING_SOUND = new SoundEvent(new ResourceLocation("uniqueutil", "alchemist_blessing_transmutate"));
	
	public static final EnchantedUpgrade ROCKET_UPGRADE = new RocketUpgrade();
	public static final EnchantedUpgrade THICK_UPGRADE = new ThickUpgrade();
	public static final EnchantedUpgrade FAMINES_UPGRADE = new FaminesUpgrade();
	public static final EnchantedUpgrade PHANES_UPGRADE = new PhanesUpgrade();
	
	public static IKeyBind BOOST_KEY = IKeyBind.empty();
	public static BooleanValue RENDER_SHIELD_HUD;
	
	public UEUtils()
	{
		UEBase.NETWORKING.registerInternalPacket(this, HighlightPacket.class, HighlightPacket::new, 21);
		BOOST_KEY = UEBase.PROXY.registerKey("Pegasus Soul Key", 341);
		SATURATION = new SaturationEffect();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerContent);
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UEUtils.toml");
		MinecraftForge.EVENT_BUS.register(UtilsHandler.INSTANCE);
		BaseHandler.INSTANCE.registerAnvilHelper(THICK_PICK, ThickPick.VALIDATOR, ThickPick.TAG);
		BaseHandler.INSTANCE.registerAnvilHelper(ANEMOIS_FRAGMENT, AnemoiFragment.FUEL_SOURCE, AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerAnvilHelper(ALCHEMISTS_BLESSING, AlchemistsBlessing.REDSTONE, AlchemistsBlessing.STORED_REDSTONE);
		BaseHandler.INSTANCE.registerStorageTooltip(THICK_PICK, "tooltip.uniqueutil.stored.repair.name", ThickPick.TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ANEMOIS_FRAGMENT, "tooltip.uniqueutil.stored.fuel.name", AnemoiFragment.STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(ALCHEMISTS_BLESSING, "tooltip.uniqueutil.stored.redstone.name", AlchemistsBlessing.STORED_REDSTONE);
		BaseHandler.INSTANCE.registerStorageTooltip(SAGES_SOUL, "tooltip.uniqueutil.stored.soul.name", SagesSoul.STORED_XP);
		BaseHandler.INSTANCE.registerStorageTooltip(REINFORCED, "tooltip.uniqueutil.stored.shield.name", Reinforced.SHIELD);	
	}
	
	@Override
	protected void addConfig(Builder builder)
	{
		builder.comment("If the shield hearts should be rendered or not if you have a shield");
		RENDER_SHIELD_HUD = builder.define("render_shield_hud", true);
	}
	
	public void registerContent(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.MOB_EFFECTS))
		{
	    	event.getForgeRegistry().register("saturation", SATURATION);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS))
		{
			event.getForgeRegistry().register("resonance_found", RESONANCE_SOUND);
			event.getForgeRegistry().register("alchemist_blessing_transmutate", ALCHEMIST_BLESSING_SOUND);
		}
	}
	
	@Override
	protected void loadUpgrades()
	{
		registerUpgrade(ROCKET_UPGRADE);
		registerUpgrade(THICK_UPGRADE);
		registerUpgrade(FAMINES_UPGRADE);
		registerUpgrade(PHANES_UPGRADE);
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
