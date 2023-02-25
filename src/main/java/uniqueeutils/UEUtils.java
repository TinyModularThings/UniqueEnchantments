package uniqueeutils;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.UEBase;
import uniquebase.api.BaseUEMod;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.api.IKeyBind;
import uniquebase.api.jei.BlockTarget;
import uniquebase.api.jei.ItemTarget;
import uniquebase.handler.BaseHandler;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.AlchemistsMending;
import uniqueeutils.enchantments.complex.Ambrosia;
import uniqueeutils.enchantments.complex.Climber;
import uniqueeutils.enchantments.complex.DemetersBlessing;
import uniqueeutils.enchantments.complex.EssenceOfSlime;
import uniqueeutils.enchantments.complex.SleipnirsGrace;
import uniqueeutils.enchantments.curse.FaminesOdium;
import uniqueeutils.enchantments.curse.PhanesRegret;
import uniqueeutils.enchantments.curse.RocketMan;
import uniqueeutils.enchantments.simple.Adept;
import uniqueeutils.enchantments.simple.Dreams;
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
	public static Enchantment DREAMS;
	public static Enchantment ALCHEMISTS_MENDING;
	
	public static MobEffect SATURATION;
	
	public static final SoundEvent RESONANCE_SOUND = new SoundEvent(new ResourceLocation("uniqueutil", "resonance_found"));
	public static final SoundEvent ALCHEMIST_BLESSING_SOUND = new SoundEvent(new ResourceLocation("uniqueutil", "alchemist_blessing_transmutate"));
	
	public static final EnchantedUpgrade ROCKET_UPGRADE = new RocketUpgrade();
	public static final EnchantedUpgrade THICK_UPGRADE = new ThickUpgrade();
	public static final EnchantedUpgrade FAMINES_UPGRADE = new FaminesUpgrade();
	public static final EnchantedUpgrade PHANES_UPGRADE = new PhanesUpgrade();
	
	public static final List<MobEffect> NEGATIVE_EFFECTS = new ObjectArrayList<>();
	
	public static IKeyBind BOOST_KEY = IKeyBind.empty();
	public static BooleanValue RENDER_SHIELD_HUD;
	
	public UEUtils()
	{
		UEBase.NETWORKING.registerInternalPacket(this, HighlightPacket.class, HighlightPacket::new, 21);
		BOOST_KEY = UEBase.PROXY.registerKey("Pegasus Soul Key", 341);
		SATURATION = new SaturationEffect();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerContent);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::postInit);
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
		if(FMLEnvironment.dist.isClient()) {
			FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerOverlay);
		}
		addTarget(new BlockTarget(Component.translatable(""), POSEIDONS_SOUL, PoseidonsSoul::isValid));
		addTarget(new ItemTarget(Component.translatable(""), ALCHEMISTS_BLESSING, T -> AlchemistsBlessing.REDSTONE.applyAsInt(T) > 0));
		addTarget(new ItemTarget(Component.translatable(""), ALCHEMISTS_BLESSING, T -> AlchemistsBlessing.RECIPES.containsKey(T.getItem())));
		addTarget(new BlockTarget(Component.translatable(""), THICK_PICK, T -> T.getBlock().defaultDestroyTime() >= 20));
		addTarget(new ItemTarget(Component.translatable(""), THICK_PICK, T -> ThickPick.VALIDATOR.applyAsInt(T) > 0));
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
	
	public void postInit(FMLCommonSetupEvent setup) 
	{
		for(MobEffect effect : ForgeRegistries.MOB_EFFECTS)
		{
			if(effect.getCategory() == MobEffectCategory.HARMFUL)
			{
				NEGATIVE_EFFECTS.add(effect);
			}
		}
	}
	
	@Override
	protected void loadBanners() 
	{
		registerPattern("uniqueutil", "adept", "ueudpt", Rarity.RARE);
		registerPattern("uniqueutil", "adept_color", "ueudptclr", Rarity.EPIC);

		registerPattern("uniqueutil", "alchemists_blessing_large", "ueulchmstsblssnglrg", Rarity.RARE);
		registerPattern("uniqueutil", "alchemists_blessing_large_color", "ueulchmstsblssnglrgclr", Rarity.EPIC);

		registerPattern("uniqueutil", "alchemists_blessing_small", "ueulchmstsblssngsmll", Rarity.RARE);
		registerPattern("uniqueutil", "alchemists_blessing_small_color", "ueulchmstsblssngsmllclr", Rarity.EPIC);

		registerPattern("uniqueutil", "alchemists_mending_border", "ueulchmstsmndngbrdr", Rarity.RARE);
		registerPattern("uniqueutil", "alchemists_mending_border_color", "ueulchmstsmndngbrdrclr", Rarity.EPIC);

		registerPattern("uniqueutil", "alchemists_mending_small", "ueulchmstsmndngsmll", Rarity.RARE);
		registerPattern("uniqueutil", "alchemists_mending_small_color", "ueulchmstsmndngsmllclr", Rarity.EPIC);
		
		registerPattern("uniqueutil", "alchemists_mending_part", "ueulchmstsmndngprt", Rarity.RARE);
		registerPattern("uniqueutil", "alchemists_mending_part_color", "ueulchmstsmndngsprtclr", Rarity.EPIC);

		registerPattern("uniqueutil", "ambrosia", "ueumbrs", Rarity.RARE);
		registerPattern("uniqueutil", "ambrosia_color", "ueumbrsclr", Rarity.EPIC);

		registerPattern("uniqueutil", "anemoi_fragment_border", "ueunmfrgmntbrdr", Rarity.RARE);
		registerPattern("uniqueutil", "anemoi_fragment_border_color", "ueunmfrgmntbrdrclr", Rarity.EPIC);

		registerPattern("uniqueutil", "anemoi_fragment_small", "ueunmfrgmntsmll", Rarity.RARE);
		registerPattern("uniqueutil", "anemoi_fragment_small_color", "ueunmfrgmntsmllclr", Rarity.EPIC);

		registerPattern("uniqueutil", "climber", "ueuclmbr", Rarity.RARE);
		registerPattern("uniqueutil", "climber_color", "ueuclmbrclr", Rarity.EPIC);

		registerPattern("uniqueutil", "demeters_blessing", "ueudmtrsblssng", Rarity.RARE);
		registerPattern("uniqueutil", "demeters_blessing_color", "ueudmtrsblssngclr", Rarity.EPIC);

		registerPattern("uniqueutil", "demeters_soul", "ueudmtrssl", Rarity.RARE);
		registerPattern("uniqueutil", "demeters_soul_color", "ueudmtrsslclr", Rarity.EPIC);

		registerPattern("uniqueutil", "dreams", "ueudrms", Rarity.RARE);
		registerPattern("uniqueutil", "dreams_color", "ueudrmsclr", Rarity.EPIC);
		
		registerPattern("uniqueutil", "dreams_dreams_skull", "ueudrmsdrmsskll", Rarity.EPIC);

		registerPattern("uniqueutil", "dreams_tall_clear", "ueudrmstllclr", Rarity.RARE);
		registerPattern("uniqueutil", "dreams_tall_clear_color", "ueudrmstllclrclr", Rarity.EPIC);

		registerPattern("uniqueutil", "dreams_tall_cloudy", "ueudrmstllcldy", Rarity.RARE);
		registerPattern("uniqueutil", "dreams_tall_cloudy_color", "ueudrmstllcldyclr", Rarity.EPIC);

		registerPattern("uniqueutil", "essence_of_slime", "ueussncfslm", Rarity.RARE);
		registerPattern("uniqueutil", "essence_of_slime_color", "ueussncfslmclr", Rarity.EPIC);

		registerPattern("uniqueutil", "famines_odium", "ueufmnsdm", Rarity.RARE);
		registerPattern("uniqueutil", "famines_odium_color", "ueufmnsdmclr", Rarity.EPIC);

		registerPattern("uniqueutil", "mounting_aegis", "ueumntnggs", Rarity.RARE);
		registerPattern("uniqueutil", "mounting_aegis_color", "ueumntnggsclr", Rarity.EPIC);

		registerPattern("uniqueutil", "pegasus_soul", "ueupgsssl", Rarity.RARE);
		registerPattern("uniqueutil", "pegasus_soul_color", "ueupgssslclr", Rarity.EPIC);

		registerPattern("uniqueutil", "phanes_regret", "ueuphnsrgrt", Rarity.RARE);
		registerPattern("uniqueutil", "phanes_regret_color", "ueuphnsrgrtclr", Rarity.EPIC);

		registerPattern("uniqueutil", "poseidons_soul_large", "ueupsdnssllrg", Rarity.RARE);
		registerPattern("uniqueutil", "poseidons_soul_large_color", "ueupsdnssllrgclr", Rarity.EPIC);

		registerPattern("uniqueutil", "poseidons_soul_small", "ueupsdnsslsmll", Rarity.RARE);
		registerPattern("uniqueutil", "poseidons_soul_small_color", "ueupsdnsslsmllclr", Rarity.EPIC);

		registerPattern("uniqueutil", "reinforced", "ueurnfrcd", Rarity.RARE);
		registerPattern("uniqueutil", "reinforced_color", "ueurnfrcdclr", Rarity.EPIC);

		registerPattern("uniqueutil", "resonance_large_dark", "ueursnnclrgdrk", Rarity.RARE);
		registerPattern("uniqueutil", "resonance_large_dark_color", "ueursnnclrgdrkclr", Rarity.EPIC);

		registerPattern("uniqueutil", "resonance_large_light", "ueursnnclrglght", Rarity.RARE);
		registerPattern("uniqueutil", "resonance_large_light_color", "ueursnnclrglghtclr", Rarity.EPIC);

		registerPattern("uniqueutil", "resonance_small", "ueursnncsmll", Rarity.RARE);
		registerPattern("uniqueutil", "resonance_small_color", "ueursnncsmllclr", Rarity.EPIC);

		registerPattern("uniqueutil", "rocket_man", "ueurcktmn", Rarity.RARE);
		registerPattern("uniqueutil", "rocket_man_color", "ueurcktmnclr", Rarity.EPIC);

		registerPattern("uniqueutil", "sages_soul", "ueusgssl", Rarity.RARE);
		registerPattern("uniqueutil", "sages_soul_color", "ueusgsslclr", Rarity.EPIC);

		registerPattern("uniqueutil", "sleipnirs_grace", "ueuslpnrsgrc", Rarity.RARE);
		registerPattern("uniqueutil", "sleipnirs_grace_color", "ueuslpnrsgrcclr", Rarity.EPIC);

		registerPattern("uniqueutil", "thick_pick", "ueuthckpck", Rarity.RARE);
		registerPattern("uniqueutil", "thick_pick_color", "ueuthckpckclr", Rarity.EPIC);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void registerOverlay(RegisterGuiOverlaysEvent event)
	{
		event.registerAbove(new ResourceLocation("player_health"), "overlay", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
			UtilsHandler.INSTANCE.onOverlay(poseStack);
		});
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
		DREAMS = register(new Dreams());
		ALCHEMISTS_MENDING = register(new AlchemistsMending());
	}
}
