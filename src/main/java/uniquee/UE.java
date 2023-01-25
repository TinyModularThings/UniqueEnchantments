package uniquee;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.api.BaseUEMod;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.utils.BannerUtils;
import uniquebase.utils.MiscUtil;
import uniquee.client.EnchantmentLayer;
import uniquee.enchantments.complex.EnderMending;
import uniquee.enchantments.complex.Momentum;
import uniquee.enchantments.complex.PerpetualStrike;
import uniquee.enchantments.complex.SmartAss;
import uniquee.enchantments.complex.SpartanWeapon;
import uniquee.enchantments.complex.SwiftBlade;
import uniquee.enchantments.curse.ComboStar;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.curse.PestilencesOdium;
import uniquee.enchantments.simple.AmelioratedBaneOfArthropod;
import uniquee.enchantments.simple.AmelioratedSharpness;
import uniquee.enchantments.simple.AmelioratedSmite;
import uniquee.enchantments.simple.Berserk;
import uniquee.enchantments.simple.BoneCrusher;
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.FocusedImpact;
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
import uniquee.enchantments.upgrades.AmelioratedUpgrade;
import uniquee.enchantments.upgrades.DeathsUpgrade;
import uniquee.enchantments.upgrades.GrimoiresUpgrade;
import uniquee.enchantments.upgrades.PestilenceUpgrade;
import uniquee.enchantments.upgrades.PhoenixUpgrade;
import uniquee.enchantments.upgrades.ProtectionUpgrade;
import uniquee.handler.EntityEvents;
import uniquee.handler.LootModifier;
import uniquee.handler.potion.AmelioratedStrength;
import uniquee.handler.potion.EternalFlamePotion;
import uniquee.handler.potion.PestilencesOdiumPotion;
import uniquee.handler.potion.Thrombosis;

@Mod("uniquee")
public class UE extends BaseUEMod
{
	public static Enchantment ADV_SHARPNESS;
	public static Enchantment ADV_SMITE;
	public static Enchantment ADV_BANE_OF_ARTHROPODS;
	public static Enchantment BERSERKER;
	public static Enchantment BONE_CRUSH;
	public static Enchantment BRITTLING_BLADE;
	public static Enchantment ENDER_EYES;
	public static Enchantment FOCUS_IMPACT;
	public static Enchantment RANGE;
	public static Enchantment SAGES_BLESSING;
	public static Enchantment SWIFT;
	public static Enchantment TREASURERS_EYES;
	public static Enchantment VITAE;
	
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
	public static Enchantment DEMETERS_SOUL;
	public static Enchantment ENDEST_REAP;
	public static Enchantment GRIMOIRE;
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM;
	public static Enchantment DEATHS_ODIUM;
	public static Enchantment COMBO_STAR;
	
	//Potions
	public static MobEffect AMELIORATED_STRENGTH;
	public static MobEffect ETERNAL_FLAME_POTION;
	public static MobEffect PESTILENCES_ODIUM_POTION;
	public static MobEffect THROMBOSIS;

	public static ForgeConfigSpec CONFIG;
	

	public static final SoundEvent ENDER_LIBRARIAN_SOUND = new SoundEvent(new ResourceLocation("uniquee", "ender_librarian"));
	public static final SoundEvent GRIMOIRE_SOUND = new SoundEvent(new ResourceLocation("uniquee", "grimoire"));
	public static final SoundEvent MOMENTUM_SOUND = new SoundEvent(new ResourceLocation("uniquee", "momentum"));
	
	public static final EnchantedUpgrade AMELIORATED_UPGRADE = new AmelioratedUpgrade();
	public static final EnchantedUpgrade DEATHS_UPGRADE = new DeathsUpgrade();
	public static final EnchantedUpgrade GRIMOIRES_UPGRADE = new GrimoiresUpgrade();
	public static final EnchantedUpgrade PESTILENCE_UPGRADE = new PestilenceUpgrade();
	public static final EnchantedUpgrade PHOENIX_UPGRADE = new PhoenixUpgrade();
	public static final EnchantedUpgrade PROTECTION_UPGRADE = new ProtectionUpgrade();
	
	public UE()
	{
		AMELIORATED_STRENGTH = new AmelioratedStrength();
		THROMBOSIS = new Thrombosis();
		ETERNAL_FLAME_POTION = new EternalFlamePotion();
		PESTILENCES_ODIUM_POTION = new PestilencesOdiumPotion();
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		init(bus, "UE.toml");
		bus.register(this);
		if(FMLEnvironment.dist.isClient()) bus.addListener(this::onClientInit);
		bus.addListener(this::registerContent);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(this::onCommandLoad);
		
		BaseHandler.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniquee.stored.gold.name", MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniquee.stored.lava.name", IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniquee.stored.feather.name", IcarusAegis.FEATHER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniquee.stored.repair.name", EnderMending.ENDER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.uniquee.stored.reap.name", EndestReap.REAP_STORAGE);
		BaseHandler.INSTANCE.registerStorageTooltip(SAGES_BLESSING, "tooltip.uniquee.stored.xp.name", SagesBlessing.SAGES_XP);

		BaseHandler.INSTANCE.registerAnvilHelper(SAGES_BLESSING, SagesBlessing.VALIDATOR, SagesBlessing.SAGES_XP);
		BaseHandler.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessing.VALIDATOR, MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGrace.VALIDATOR, IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegis.VALIDATOR, IcarusAegis.FEATHER_TAG);
		
	}
	
	public void registerContent(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.MOB_EFFECTS))
		{
			event.getForgeRegistry().register("ameliorated_strength", AMELIORATED_STRENGTH);
	    	event.getForgeRegistry().register("pestilences_odium", PESTILENCES_ODIUM_POTION);
	    	event.getForgeRegistry().register("eternal_flame", ETERNAL_FLAME_POTION);
	    	event.getForgeRegistry().register("thrombosis", THROMBOSIS);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS))
		{
	    	event.getForgeRegistry().register("ue_loot", LootModifier.CODEC);
		}
		else if(event.getRegistryKey().equals(ForgeRegistries.Keys.SOUND_EVENTS))
		{
			event.getForgeRegistry().register("ender_librarian", ENDER_LIBRARIAN_SOUND);
			event.getForgeRegistry().register("grimoire", GRIMOIRE_SOUND);
			event.getForgeRegistry().register("momentum", MOMENTUM_SOUND);
		}
		else if(event.getRegistryKey().equals(Registry.BANNER_PATTERN_REGISTRY)) {
			for(Entry<ResourceKey<BannerPattern>, String> entry:BannerUtils.getBanners().entrySet()) {
				Registry.register(Registry.BANNER_PATTERN, entry.getKey(), new BannerPattern(entry.getValue()));
			}
		}
	}
	
	@Override
	protected void loadUpgrades()
	{
		registerUpgrade(AMELIORATED_UPGRADE);
		registerUpgrade(DEATHS_UPGRADE);
		registerUpgrade(GRIMOIRES_UPGRADE);
		registerUpgrade(PESTILENCE_UPGRADE);
		registerUpgrade(PHOENIX_UPGRADE);
		registerUpgrade(PROTECTION_UPGRADE);
	}
	
	@Override
	protected void loadBanners()
	{
		registerPattern("uniquee", "alchemists_grace_large", "ueelchmstsgrclrg", Rarity.RARE);
		registerPattern("uniquee", "alchemists_grace_large_color", "ueelchmstsgrclrgclr", Rarity.EPIC);

		registerPattern("uniquee", "alchemists_grace_small", "ueelchmstsgrcsmll", Rarity.RARE);
		registerPattern("uniquee", "alchemists_grace_small_color", "ueelchmstsgrcsmllclr", Rarity.EPIC);

		registerPattern("uniquee", "ameliorated_bane_of_arthropods", "ueemlrtdbnfrthrpds", Rarity.RARE);
		registerPattern("uniquee", "ameliorated_bane_of_arthropods_color", "ueemlrtdbnfrthrpdsclr", Rarity.EPIC);

		registerPattern("uniquee", "ameliorated_sharpness_large", "ueemlrtdshrpnsslrg", Rarity.RARE);
		registerPattern("uniquee", "ameliorated_sharpness_large_color", "ueemlrtdshrpnsslrgclr", Rarity.EPIC);

		registerPattern("uniquee", "ameliorated_sharpness_small", "ueemlrtdshrpnsssmll", Rarity.RARE);
		registerPattern("uniquee", "ameliorated_sharpness_small_color", "ueemlrtdshrpnsssmllclr", Rarity.EPIC);

		registerPattern("uniquee", "ameliorated_smite", "ueemlrtdsmt", Rarity.RARE);
		registerPattern("uniquee", "ameliorated_smite_color", "ueemlrtdsmtclr", Rarity.EPIC);

		registerPattern("uniquee", "ares_blessing", "ueersblssng", Rarity.RARE);
		registerPattern("uniquee", "ares_blessing_color", "ueersblssngclr", Rarity.EPIC);

		registerPattern("uniquee", "berserker", "ueebrsrkr", Rarity.RARE);
		registerPattern("uniquee", "berserker_color", "ueebrsrkrclr", Rarity.EPIC);

		registerPattern("uniquee", "bone_crusher", "ueebncrshr", Rarity.RARE);
		registerPattern("uniquee", "bone_crusher_color", "ueebncrshrclr", Rarity.EPIC);

		registerPattern("uniquee", "climate_tranquility", "ueeclmttrnqlty", Rarity.RARE);
		registerPattern("uniquee", "climate_tranquility_color", "ueeclmttrnqltyclr", Rarity.EPIC);

		registerPattern("uniquee", "cloud_walker", "ueecldwlkr", Rarity.RARE);
		registerPattern("uniquee", "cloud_walker_color", "ueecldwlkrclr", Rarity.EPIC);

		registerPattern("uniquee", "combo_star", "ueecmbstr", Rarity.RARE);
		registerPattern("uniquee", "combo_star_color", "ueecmbstrclr", Rarity.EPIC);

		registerPattern("uniquee", "deaths_odium", "ueedthsdm", Rarity.RARE);
		registerPattern("uniquee", "deaths_odium_color", "ueedthsdmclr", Rarity.EPIC);

		registerPattern("uniquee", "ecological", "ueeclgcl", Rarity.RARE);
		registerPattern("uniquee", "ecological_color", "ueeclgclclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_eyes_left", "ueendryslft", Rarity.RARE);
		registerPattern("uniquee", "ender_eyes_left_color", "ueendryslftclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_eyes_right", "ueendrysrght", Rarity.RARE);
		registerPattern("uniquee", "ender_eyes_right_color", "ueendrysrghtclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_librarian", "ueendrlbrrn", Rarity.RARE);
		registerPattern("uniquee", "ender_librarian_color", "ueendrlbrrnclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_markmen", "ueendrmrkmn", Rarity.RARE);
		registerPattern("uniquee", "ender_markmen_color", "ueendrmrkmnclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_mending_border", "ueendrmndngbrdr", Rarity.RARE);
		registerPattern("uniquee", "ender_mending_border_color", "ueendrmndngbrdrclr", Rarity.EPIC);

		registerPattern("uniquee", "ender_mending_small", "ueendrmndngsmll", Rarity.RARE);
		registerPattern("uniquee", "ender_mending_small_color", "ueendrmndngsmllclr", Rarity.EPIC);

		registerPattern("uniquee", "endest_reap", "ueendstrp", Rarity.RARE);
		registerPattern("uniquee", "endest_reap_color", "ueendstrpclr", Rarity.EPIC);

		registerPattern("uniquee", "fast_food", "ueefstfd", Rarity.RARE);
		registerPattern("uniquee", "fast_food_color", "ueefstfdclr", Rarity.EPIC);

		registerPattern("uniquee", "focused_impact", "ueefcsdmpct", Rarity.RARE);
		registerPattern("uniquee", "focused_impact_color", "ueefcsdmpctclr", Rarity.EPIC);

		registerPattern("uniquee", "grimoire", "ueegrmr", Rarity.RARE);
		registerPattern("uniquee", "grimoire_color", "ueegrmrclr", Rarity.EPIC);

		registerPattern("uniquee", "icarus_aegis", "ueecrsgs", Rarity.RARE);
		registerPattern("uniquee", "icarus_aegis_color", "ueecrsgsclr", Rarity.EPIC);

		registerPattern("uniquee", "ifrits_grace", "ueefrtsgrc", Rarity.RARE);
		registerPattern("uniquee", "ifrits_grace_color", "ueefrtsgrcclr", Rarity.EPIC);

		registerPattern("uniquee", "midas_blessing_border", "ueemdsblssngbrdr", Rarity.RARE);
		registerPattern("uniquee", "midas_blessing_border_color", "ueemdsblssngbrdrclr", Rarity.EPIC);

		registerPattern("uniquee", "midas_blessing_small", "ueemdsblssngsmll", Rarity.RARE);
		registerPattern("uniquee", "midas_blessing_small_color", "ueemdsblssngsmllclr", Rarity.EPIC);

		registerPattern("uniquee", "momentum", "ueemmntm", Rarity.RARE);
		registerPattern("uniquee", "momentum_color", "ueemmntmclr", Rarity.EPIC);

		registerPattern("uniquee", "natures_grace", "ueentrsgrc", Rarity.RARE);
		registerPattern("uniquee", "natures_grace_color", "ueentrsgrcclr", Rarity.EPIC);

		registerPattern("uniquee", "perpetual_strike", "ueeprptlstrk", Rarity.RARE);
		registerPattern("uniquee", "perpetual_strike_color", "ueeprptlstrkclr", Rarity.EPIC);

		registerPattern("uniquee", "pestilences_odium", "ueepstlncsdm", Rarity.RARE);
		registerPattern("uniquee", "pestilences_odium_color", "ueepstlncsdmclr", Rarity.EPIC);

		registerPattern("uniquee", "pheonixs_blessing", "ueephnxsblssng", Rarity.RARE);
		registerPattern("uniquee", "pheonixs_blessing_color", "ueephnxsblssngclr", Rarity.EPIC);

		registerPattern("uniquee", "range_large_vertical", "ueernglrgvrtcl", Rarity.RARE);
		registerPattern("uniquee", "range_large_vertical_color", "ueernglrgvrtclclr", Rarity.EPIC);

		registerPattern("uniquee", "range_small_horizontal", "ueerngsmllhrzntl", Rarity.RARE);
		registerPattern("uniquee", "range_small_horizontal_color", "ueerngsmllhrzntlclr", Rarity.EPIC);

		registerPattern("uniquee", "range_small_vertical", "ueerngsmllvrtcl", Rarity.RARE);
		registerPattern("uniquee", "range_small_vertical_color", "ueerngsmllvrtclclr", Rarity.EPIC);

		registerPattern("uniquee", "sages_blessing", "ueesgsblssng", Rarity.RARE);
		registerPattern("uniquee", "sages_blessing_color", "ueesgsblssngclr", Rarity.EPIC);

		registerPattern("uniquee", "smart_ass", "ueesmrtss", Rarity.RARE);
		registerPattern("uniquee", "smart_ass_color", "ueesmrtssclr", Rarity.EPIC);

		registerPattern("uniquee", "spartan_weapon", "ueesprtnwpn", Rarity.RARE);
		registerPattern("uniquee", "spartan_weapon_color", "ueesprtnwpnclr", Rarity.EPIC);

		registerPattern("uniquee", "swift", "ueeswft", Rarity.RARE);
		registerPattern("uniquee", "swift_color", "ueeswftclr", Rarity.EPIC);

		registerPattern("uniquee", "swift_blade_large", "ueeswftbldlrg", Rarity.RARE);
		registerPattern("uniquee", "swift_blade_large_color", "ueeswftbldlrgclr", Rarity.EPIC);

		registerPattern("uniquee", "swift_blade_small", "ueeswftbldsmll", Rarity.RARE);
		registerPattern("uniquee", "swift_blade_small_color", "ueeswftbldsmllclr", Rarity.EPIC);

		registerPattern("uniquee", "treasurers_eyes_left", "ueetrsrrsyslft", Rarity.RARE);
		registerPattern("uniquee", "treasurers_eyes_left_color", "ueetrsrrsyslftclr", Rarity.EPIC);

		registerPattern("uniquee", "treasurers_eyes_right", "ueetrsrrsysrght", Rarity.RARE);
		registerPattern("uniquee", "treasurers_eyes_right_color", "ueetrsrrsysrghtclr", Rarity.EPIC);

		registerPattern("uniquee", "vitae", "ueevt", Rarity.RARE);
		registerPattern("uniquee", "vitae_color", "ueevtclr", Rarity.EPIC);

		registerPattern("uniquee", "warriors_grace_large", "ueewrrrsgrclrg", Rarity.RARE);
		registerPattern("uniquee", "warriors_grace_large_color", "ueewrrrsgrclrgclr", Rarity.EPIC);

		registerPattern("uniquee", "warriors_grace_small", "ueewrrrsgrcsmll", Rarity.RARE);
		registerPattern("uniquee", "warriors_grace_small_color", "ueewrrrsgrcsmllclr", Rarity.EPIC);
		
		//Vanilla Icons
		registerPattern("uniquebase", "aqua_affinity", "mcqffnty", Rarity.RARE);
		registerPattern("uniquebase", "aqua_affinity_color", "mcqffntyclr", Rarity.EPIC);

		registerPattern("uniquebase", "aqua_affinity_ocean", "mcqffntycn", Rarity.RARE);
		registerPattern("uniquebase", "aqua_affinity_ocean_color", "mcqffntycnclr", Rarity.EPIC);

		registerPattern("uniquebase", "bane_of_arthropods", "mcbnfrthrpds", Rarity.RARE);
		registerPattern("uniquebase", "bane_of_arthropods_color", "mcbnfrthrpdsclr", Rarity.EPIC);

		registerPattern("uniquebase", "blast_protection_large", "mcblstprtctnlrg", Rarity.RARE);
		registerPattern("uniquebase", "blast_protection_large_color", "mcblstprtctnlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "blast_protection_small", "mcblstprtctnsmll", Rarity.RARE);
		registerPattern("uniquebase", "blast_protection_small_color", "mcblstprtctnsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "channeling", "mcchnnlng", Rarity.RARE);
		registerPattern("uniquebase", "channeling_color", "mcchnnlngclr", Rarity.EPIC);

		registerPattern("uniquebase", "curse_of_binding", "mccrsfbndng", Rarity.RARE);
		registerPattern("uniquebase", "curse_of_binding_color", "mccrsfbndngclr", Rarity.EPIC);

		registerPattern("uniquebase", "curse_of_vanishing", "mccrsfvnshng", Rarity.RARE);
		registerPattern("uniquebase", "curse_of_vanishing_color", "mccrsfvnshngclr", Rarity.EPIC);

		registerPattern("uniquebase", "depth_strider", "mcdpthstrdr", Rarity.RARE);
		registerPattern("uniquebase", "depth_strider_color", "mcdpthstrdrclr", Rarity.EPIC);

		registerPattern("uniquebase", "efficiency_lined", "mcffcncylnd", Rarity.RARE);
		registerPattern("uniquebase", "efficiency_lined_color", "mcffcncylndclr", Rarity.EPIC);

		registerPattern("uniquebase", "efficiency_unlined", "mcffcncynlnd", Rarity.RARE);
		registerPattern("uniquebase", "efficiency_unlined_color", "mcffcncynlndclr", Rarity.EPIC);

		registerPattern("uniquebase", "feather_falling", "mcfthrfllng", Rarity.RARE);
		registerPattern("uniquebase", "feather_falling_color", "mcfthrfllngclr", Rarity.EPIC);

		registerPattern("uniquebase", "fire_aspect_large", "mcfrspctlrg", Rarity.RARE);
		registerPattern("uniquebase", "fire_aspect_large_color", "mcfrspctlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "fire_aspect_small", "mcfrspctsmll", Rarity.RARE);
		registerPattern("uniquebase", "fire_aspect_small_color", "mcfrspctsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "fire_protection_large", "mcfrprtctnlrg", Rarity.RARE);
		registerPattern("uniquebase", "fire_protection_large_color", "mcfrprtctnlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "fire_protection_small", "mcfrprtctnsmll", Rarity.RARE);
		registerPattern("uniquebase", "fire_protection_small_color", "mcfrprtctnsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "flame", "mcflm", Rarity.RARE);
		registerPattern("uniquebase", "flame_color", "mcflmclr", Rarity.EPIC);

		registerPattern("uniquebase", "fortune_large", "mcfrtnlrg", Rarity.RARE);
		registerPattern("uniquebase", "fortune_large_color", "mcfrtnlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "fortune_small", "mcfrtnsmll", Rarity.RARE);
		registerPattern("uniquebase", "fortune_small_color", "mcfrtnsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "frost_walker", "mcfrstwlkr", Rarity.RARE);
		registerPattern("uniquebase", "frost_walker_color", "mcfrstwlkrclr", Rarity.EPIC);

		registerPattern("uniquebase", "impaling_large", "mcmplnglrg", Rarity.RARE);
		registerPattern("uniquebase", "impaling_large_color", "mcmplnglrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "impaling_small", "mcmplngsmll", Rarity.RARE);
		registerPattern("uniquebase", "impaling_small_color", "mcmplngsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "infinity_border", "mcnfntybrdr", Rarity.RARE);
		registerPattern("uniquebase", "infinity_border_color", "mcnfntybrdrclr", Rarity.EPIC);

		registerPattern("uniquebase", "infinity_large", "mcnfntylrg", Rarity.RARE);
		registerPattern("uniquebase", "infinity_large_color", "mcnfntylrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "infinity_small", "mcnfntysmll", Rarity.RARE);
		registerPattern("uniquebase", "infinity_small_color", "mcnfntysmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_large_bottom", "mcknckbcklrgbttm", Rarity.RARE);
		registerPattern("uniquebase", "knockback_large_bottom_color", "mcknckbcklrgbttmclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_large_up", "mcknckbcklrgp", Rarity.RARE);
		registerPattern("uniquebase", "knockback_large_up_color", "mcknckbcklrgpclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_small_bottom", "mcknckbcksmllbttm", Rarity.RARE);
		registerPattern("uniquebase", "knockback_small_bottom_color", "mcknckbcksmllbttmclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_small_left", "mcknckbcksmlllft", Rarity.RARE);
		registerPattern("uniquebase", "knockback_small_left_color", "mcknckbcksmlllftclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_small_right", "mcknckbcksmllrght", Rarity.RARE);
		registerPattern("uniquebase", "knockback_small_right_color", "mcknckbcksmllrghtclr", Rarity.EPIC);

		registerPattern("uniquebase", "knockback_small_up", "mcknckbcksmllp", Rarity.RARE);
		registerPattern("uniquebase", "knockback_small_up_color", "mcknckbcksmllpclr", Rarity.EPIC);

		registerPattern("uniquebase", "looting", "mcltng", Rarity.RARE);
		registerPattern("uniquebase", "looting_color", "mcltngclr", Rarity.EPIC);

		registerPattern("uniquebase", "loyalty", "mclylty", Rarity.RARE);
		registerPattern("uniquebase", "loyalty_color", "mclyltyclr", Rarity.EPIC);

		registerPattern("uniquebase", "luck_of_the_sea", "mclckfths", Rarity.RARE);
		registerPattern("uniquebase", "luck_of_the_sea_color", "mclckfthsclr", Rarity.EPIC);

		registerPattern("uniquebase", "lure", "mclr", Rarity.RARE);
		registerPattern("uniquebase", "lure_color", "mclrclr", Rarity.EPIC);

		registerPattern("uniquebase", "mending_border", "mcmndngbrdr", Rarity.RARE);
		registerPattern("uniquebase", "mending_border_color", "mcmndngbrdrclr", Rarity.EPIC);

		registerPattern("uniquebase", "mending_small", "mcmndngsmll", Rarity.RARE);
		registerPattern("uniquebase", "mending_small_color", "mcmndngsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "multishot", "mcmltsht", Rarity.RARE);
		registerPattern("uniquebase", "multishot_color", "mcmltshtclr", Rarity.EPIC);

		registerPattern("uniquebase", "piercing", "mcprcng", Rarity.RARE);
		registerPattern("uniquebase", "piercing_color", "mcprcngclr", Rarity.EPIC);

		registerPattern("uniquebase", "power", "mcpwr", Rarity.RARE);
		registerPattern("uniquebase", "power_color", "mcpwrclr", Rarity.EPIC);

		registerPattern("uniquebase", "projectile_protection_large", "mcprjctlprtctnlrg", Rarity.RARE);
		registerPattern("uniquebase", "projectile_protection_large_color", "mcprjctlprtctnlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "projectile_protection_small", "mcprjctlprtctnsmll", Rarity.RARE);
		registerPattern("uniquebase", "projectile_protection_small_color", "mcprjctlprtctnsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "protection_large", "mcprtctnlrg", Rarity.RARE);
		registerPattern("uniquebase", "protection_large_color", "mcprtctnlrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "protection_small", "mcprtctnsmll", Rarity.RARE);
		registerPattern("uniquebase", "protection_small_color", "mcprtctnsmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "punch", "mcpnch", Rarity.RARE);
		registerPattern("uniquebase", "punch_color", "mcpnchclr", Rarity.EPIC);

		registerPattern("uniquebase", "quick_charge_green", "mcqckchrggrn", Rarity.RARE);
		registerPattern("uniquebase", "quick_charge_green_color", "mcqckchrggrnclr", Rarity.EPIC);

		registerPattern("uniquebase", "quick_charge_purple", "mcqckchrgprpl", Rarity.RARE);
		registerPattern("uniquebase", "quick_charge_purple_color", "mcqckchrgprplclr", Rarity.EPIC);

		registerPattern("uniquebase", "respiration", "mcrsprtn", Rarity.RARE);
		registerPattern("uniquebase", "respiration_color", "mcrsprtnclr", Rarity.EPIC);

		registerPattern("uniquebase", "riptide", "mcrptd", Rarity.RARE);
		registerPattern("uniquebase", "riptide_color", "mcrptdclr", Rarity.EPIC);

		registerPattern("uniquebase", "sharpness_large", "mcshrpnsslrg", Rarity.RARE);
		registerPattern("uniquebase", "sharpness_large_color", "mcshrpnsslrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "sharpness_small", "mcshrpnsssmll", Rarity.RARE);
		registerPattern("uniquebase", "sharpness_small_color", "mcshrpnsssmllclr", Rarity.EPIC);

		registerPattern("uniquebase", "silk_touch", "mcslktch", Rarity.RARE);
		registerPattern("uniquebase", "silk_touch_color", "mcslktchclr", Rarity.EPIC);

		registerPattern("uniquebase", "smite", "mcsmt", Rarity.RARE);
		registerPattern("uniquebase", "smite_color", "mcsmtclr", Rarity.EPIC);

		registerPattern("uniquebase", "soul_speed", "mcslspd", Rarity.RARE);
		registerPattern("uniquebase", "soul_speed_color", "mcslspdclr", Rarity.EPIC);

		registerPattern("uniquebase", "sweeping_edge", "mcswpngdg", Rarity.RARE);
		registerPattern("uniquebase", "sweeping_edge_color", "mcswpngdgclr", Rarity.EPIC);

		registerPattern("uniquebase", "swift_sneak", "mcswftsnk", Rarity.RARE);
		registerPattern("uniquebase", "swift_sneak_color", "mcswftsnkclr", Rarity.EPIC);

		registerPattern("uniquebase", "thorns", "mcthrns", Rarity.RARE);
		registerPattern("uniquebase", "thorns_color", "mcthrnsclr", Rarity.EPIC);

		registerPattern("uniquebase", "unbreaking_large", "mcnbrknglrg", Rarity.RARE);
		registerPattern("uniquebase", "unbreaking_large_color", "mcnbrknglrgclr", Rarity.EPIC);

		registerPattern("uniquebase", "unbreaking_small", "mcnbrkngsmll", Rarity.RARE);
		registerPattern("uniquebase", "unbreaking_small_color", "mcnbrkngsmllclr", Rarity.EPIC);
	}
    
	@Override
	protected void loadEnchantments()
	{
		BERSERKER = register(new Berserk());
		ADV_SHARPNESS = register(new AmelioratedSharpness());
		ADV_SMITE = register(new AmelioratedSmite());
		ADV_BANE_OF_ARTHROPODS = register(new AmelioratedBaneOfArthropod());
		VITAE = register(new Vitae());
		SWIFT = register(new Swift());
		SAGES_BLESSING = register(new SagesBlessing());
		ENDER_EYES = register(new EnderEyes());
		FOCUS_IMPACT = register(new FocusedImpact());
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
		COMBO_STAR = register(new ComboStar());
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		if(ModList.get().isLoaded("firstaid"))
		{
			try
			{
				Class<?> clz = Class.forName("uniquee.compat.FirstAidHandler");
				Field field = clz.getField("INSTANCE");
				field.setAccessible(true);
				Object obj = field.get(null);
				if(obj != null)
				{
					MinecraftForge.EVENT_BUS.register(obj);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
    
    public void onCommandLoad(RegisterCommandsEvent event)
    {
    	event.getDispatcher().register(Commands.literal("ue").requires(T -> T.hasPermission(3)).then(Commands.literal("remove_deaths_odium").then(Commands.argument("player", EntityArgument.player())).executes(this::removeCurse)));
    }
    
    private int removeCurse(CommandContext<CommandSourceStack> command) throws CommandSyntaxException
    {
    	Player player = EntityArgument.getPlayer(command, "player");
    	CompoundTag nbt = MiscUtil.getPersistentData(player);
		nbt.remove(DeathsOdium.CURSE_RESET);
		nbt.remove(DeathsOdium.CURSE_STORAGE);
		for(EquipmentSlot slot : EquipmentSlot.values())
		{
			ItemStack stack = player.getItemBySlot(slot);
			if(MiscUtil.getEnchantmentLevel(UE.DEATHS_ODIUM, stack) > 0)
			{
				stack.getTag().remove(DeathsOdium.CURSE_STORAGE);
			}
		}
		player.getAttribute(Attributes.MAX_HEALTH).removeModifier(DeathsOdium.REMOVE_UUID);
    	return 0;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void onClientInit(AddLayers event)
    {
		for(String skin : event.getSkins())
		{
			LivingEntityRenderer<Player, PlayerModel<Player>> render = event.getSkin(skin);
			render.addLayer(new EnchantmentLayer<>(render));
		}
		addLayer(EntityType.SKELETON, event);
		addLayer(EntityType.STRAY, event);
		addLayer(EntityType.WITHER_SKELETON, event);
		addLayer(EntityType.ARMOR_STAND, event);
		addLayer(EntityType.GIANT, event);
		addLayer(EntityType.ZOMBIFIED_PIGLIN, event);
		addLayer(EntityType.ZOMBIE_VILLAGER, event);
		addLayer(EntityType.DROWNED, event);
		addLayer(EntityType.ZOMBIE, event);
		addLayer(EntityType.HUSK, event);
    }
    
	@OnlyIn(Dist.CLIENT)
    private <T extends LivingEntity> void addLayer(EntityType<T> entity, AddLayers manager) {
		LivingEntityRenderer<T, EntityModel<T>> renderer = manager.getRenderer(entity);
    	renderer.addLayer(new EnchantmentLayer<>(renderer));
    }
}
