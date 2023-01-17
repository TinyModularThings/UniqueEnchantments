package uniquee;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.api.BaseUEMod;
import uniquebase.api.EnchantedUpgrade;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.utils.BannerUtils;
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

	public static final DeferredRegister<Item> BANNER_PATTERNS_ITEMS = DeferredRegister.create(Registry.ITEM_REGISTRY, "uniquee");
	
	public static final ResourceKey<BannerPattern> AMEL_SHARPNESS_BANNER = BannerUtils.createBanner("uniquee", "ameliorated_sharpness", "ueamlshrp", BANNER_PATTERNS_ITEMS, new Item.Properties().rarity(Rarity.RARE));
	public static final ResourceKey<BannerPattern> AMEL_SHARPNESS_COLOR_BANNER = BannerUtils.createBanner("uniquee", "ameliorated_sharpness_color", "ueamlshrpc", BANNER_PATTERNS_ITEMS, new Item.Properties().rarity(Rarity.EPIC));

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
		
		BaseHandler.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniquee.stored.gold.name", MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniquee.stored.lava.name", IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniquee.stored.feather.name", IcarusAegis.FEATHER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniquee.stored.repair.name", EnderMending.ENDER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.uniquee.stored.reap.name", EndestReap.REAP_STORAGE);
		
		BaseHandler.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessing.VALIDATOR, MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGrace.VALIDATOR, IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegis.VALIDATOR, IcarusAegis.FEATHER_TAG);
		
//		BANNER_PATTERNS.register(bus);
		BANNER_PATTERNS_ITEMS.register(bus);
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
