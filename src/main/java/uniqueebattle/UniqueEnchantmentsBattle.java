package uniqueebattle;

import java.io.File;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.potion.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.api.BaseUEMod;
import uniquebase.api.IKeyBind;
import uniquebase.handler.BaseHandler;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.AresGrace;
import uniqueebattle.enchantments.ArtemisSoul;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.DeepWounds;
import uniqueebattle.enchantments.Fury;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.GranisSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.IronBird;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.enchantments.StreakersWill;
import uniqueebattle.enchantments.WarsOdium;
import uniqueebattle.handler.BattleHandler;
import uniqueebattle.handler.potion.Bleed;
import uniqueebattle.handler.potion.Toughend;

@Mod(modid = "uniqueebattle", name = "Unique Battle Enchantments", version = "1.0.1", dependencies = "required-after:uniquebase@[1.0.0,);")
public class UniqueEnchantmentsBattle extends BaseUEMod
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
	
	public static final Potion TOUGHEND = new Toughend();
	public static final Potion BLEED = new Bleed();
	public static IKeyBind GRANIS_SOUL_DASH = IKeyBind.empty();

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		GRANIS_SOUL_DASH = UniqueEnchantmentsBase.PROXY.registerKey("Granis Soul Dash", 46);
		ForgeRegistries.POTIONS.registerAll(TOUGHEND, BLEED);
		init("uniqueebattle", new File(event.getModConfigurationDirectory(), "UniqueEnchantments_Battle.cfg"));
		MinecraftForge.EVENT_BUS.register(BattleHandler.INSTANCE);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniqueebattle.stored.ender.name", ArtemisSoul.ENDER_STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniqueebattle.stored.ender.souls.common.name", ArtemisSoul.TEMPORARY_SOUL_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ARTEMIS_SOUL, "tooltip.uniqueebattle.stored.ender.souls.higher.name", ArtemisSoul.PERSISTEN_SOUL_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ARTEMIS_SOUL, ArtemisSoul.VALID_ITEMS, ArtemisSoul.ENDER_STORAGE);
	}
	
	@Override
	protected void addEnchantments()
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
	}
}
