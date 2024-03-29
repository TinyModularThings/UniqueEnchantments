package uniqueebattle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.enchantment.Enchantment;
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
import uniqueebattle.enchantments.complex.AresFragment;
import uniqueebattle.enchantments.complex.ArtemisSoul;
import uniqueebattle.enchantments.complex.DeepWounds;
import uniqueebattle.enchantments.complex.GranisSoul;
import uniqueebattle.enchantments.complex.IfritsBlessing;
import uniqueebattle.enchantments.curse.IfritsJudgement;
import uniqueebattle.enchantments.curse.LunaticDespair;
import uniqueebattle.enchantments.curse.WarsOdium;
import uniqueebattle.enchantments.simple.AresGrace;
import uniqueebattle.enchantments.simple.CelestialBlessing;
import uniqueebattle.enchantments.simple.Fury;
import uniqueebattle.enchantments.simple.GolemSoul;
import uniqueebattle.enchantments.simple.IronBird;
import uniqueebattle.enchantments.simple.Snare;
import uniqueebattle.enchantments.simple.StreakersWill;
import uniqueebattle.enchantments.upgrades.AresUpgrade;
import uniqueebattle.enchantments.upgrades.IfritsUpgrade;
import uniqueebattle.enchantments.upgrades.LunaticUpgrade;
import uniqueebattle.enchantments.upgrades.WarsUpgrade;
import uniqueebattle.handler.BattleHandler;
import uniqueebattle.handler.potion.Bleed;
import uniqueebattle.handler.potion.Lockdown;
import uniqueebattle.handler.potion.Toughend;


@Mod("uniquebattle")
public class UEBattle extends BaseUEMod
{
	public static Enchantment LUNATIC_DESPAIR;
	public static Enchantment CELESTIAL_BLESSING;
	public static Enchantment ARES_FRAGMENT;
	public static Enchantment IFRITS_BLESSING;
	public static Enchantment IFRITS_JUDGEMENT;
	public static Enchantment GOLEM_SOUL;
	public static Enchantment FURY;
	public static Enchantment STREAKERS_WILL;
	public static Enchantment IRON_BIRD;
	public static Enchantment DEEP_WOUNDS;
	public static Enchantment WARS_ODIUM;
	public static Enchantment ARES_GRACE;
	public static Enchantment GRANIS_SOUL;
	public static Enchantment ARTEMIS_SOUL;
	public static Enchantment SNARE;

	
	public static MobEffect TOUGHEND;
	public static MobEffect BLEED;
	public static MobEffect LOCK_DOWN;
	public static IKeyBind GRANIS_SOUL_DASH = IKeyBind.empty();
	
	public static final SoundEvent CELESTIAL_BLESSING_START_SOUND = new SoundEvent(new ResourceLocation("uniquebattle", "celestial_blessing_start"));
	public static final SoundEvent CELESTIAL_BLESSING_END_SOUND = new SoundEvent(new ResourceLocation("uniquebattle", "celestial_blessing_end"));
	public static final SoundEvent FURY_DROP_SOUND = new SoundEvent(new ResourceLocation("uniquebattle", "fury_drop"));
	public static final SoundEvent WARS_ODIUM_REVIVE_SOUND = new SoundEvent(new ResourceLocation("uniquebattle", "wars_odium_revive"));
	
	public static final EnchantedUpgrade ARES_UPGRADE = new AresUpgrade();
	public static final EnchantedUpgrade IFRITS_UPGRADE = new IfritsUpgrade();
	public static final EnchantedUpgrade LUNATIC_UPGRADE = new LunaticUpgrade();
	public static final EnchantedUpgrade WARS_UPGRADE = new WarsUpgrade();
	
	public UEBattle()
	{
		GRANIS_SOUL_DASH = UEBase.PROXY.registerKey("Granis Soul Dash", 341);
		TOUGHEND = new Toughend();
		BLEED = new Bleed();
		LOCK_DOWN = new Lockdown();
		init(FMLJavaModLoadingContext.get().getModEventBus(), "UEBattle.toml");
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerContent);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniquebattle.stored.ender.name", ArtemisSoul.ENDER_STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniquebattle.stored.ender.souls.common.name", ArtemisSoul.TEMPORARY_SOUL_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniquebattle.stored.ender.souls.higher.name", ArtemisSoul.PERSISTEN_SOUL_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ARTEMIS_SOUL, ArtemisSoul.VALID_ITEMS, ArtemisSoul.ENDER_STORAGE);

	}
	
	public void registerContent(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.MOB_EFFECTS))
		{
	    	event.getForgeRegistry().register("toughend", TOUGHEND);
	    	event.getForgeRegistry().register("bleed", BLEED);
	    	event.getForgeRegistry().register("snare", LOCK_DOWN);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS))
		{
			event.getForgeRegistry().register("celestial_blessing_start", CELESTIAL_BLESSING_START_SOUND);
			event.getForgeRegistry().register("celestial_blessing_end", CELESTIAL_BLESSING_END_SOUND);
			event.getForgeRegistry().register("fury_drop", FURY_DROP_SOUND);
			event.getForgeRegistry().register("wars_odium_revive", WARS_ODIUM_REVIVE_SOUND);
		}
	}
	
	@Override
	protected void loadUpgrades()
	{
		registerUpgrade(ARES_UPGRADE);
		registerUpgrade(IFRITS_UPGRADE);
		registerUpgrade(LUNATIC_UPGRADE);
		registerUpgrade(WARS_UPGRADE);
	}
	
	@Override
	protected void loadEnchantments()
	{
		LUNATIC_DESPAIR = register(new LunaticDespair());
		CELESTIAL_BLESSING = register(new CelestialBlessing());
		ARES_FRAGMENT = register(new AresFragment());
		IFRITS_BLESSING = register(new IfritsBlessing());
		IFRITS_JUDGEMENT = register(new IfritsJudgement());
		GOLEM_SOUL = register(new GolemSoul());
		FURY = register(new Fury());
		STREAKERS_WILL = register(new StreakersWill());
		IRON_BIRD = register(new IronBird());
		DEEP_WOUNDS = register(new DeepWounds());
		WARS_ODIUM = register(new WarsOdium());
		ARES_GRACE = register(new AresGrace());
		GRANIS_SOUL = register(new GranisSoul());
		ARTEMIS_SOUL = register(new ArtemisSoul());
		SNARE = register(new Snare());
	}
}
