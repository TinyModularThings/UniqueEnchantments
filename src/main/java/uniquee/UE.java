package uniquee;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.BaseUEMod;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquee.client.EnchantmentLayer;
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
import uniquee.handler.EntityEvents;
import uniquee.handler.LootModifier;
import uniquee.handler.potion.EternalFlamePotion;
import uniquee.handler.potion.PestilencesOdiumPotion;

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
	
	//Potions
	public static Effect ETERNAL_FLAME_POTION;
	public static Effect PESTILENCES_ODIUM_POTION;
	public static ForgeConfigSpec CONFIG;
	

	public static final SoundEvent ENDER_LIBRARIAN_SOUND = new SoundEvent(new ResourceLocation("uniquee", "ender_librarian"));
	public static final SoundEvent GRIMOIRE_SOUND = new SoundEvent(new ResourceLocation("uniquee", "grimoire"));
	public static final SoundEvent MOMENTUM_SOUND = new SoundEvent(new ResourceLocation("uniquee", "momentum"));
	
	public UE()
	{
		ETERNAL_FLAME_POTION = new EternalFlamePotion();
		PESTILENCES_ODIUM_POTION = new PestilencesOdiumPotion();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		init(bus, "UE.toml");
		bus.register(this);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		bus.addGenericListener(Effect.class, this::loadPotion);
		bus.addGenericListener(GlobalLootModifierSerializer.class, this::loadLoot);

		BaseHandler.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniquee.stored.gold.name", MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniquee.stored.lava.name", IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniquee.stored.feather.name", IcarusAegis.FEATHER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniquee.stored.repair.name", EnderMending.ENDER_TAG);
		BaseHandler.INSTANCE.registerStorageTooltip(ENDEST_REAP, "tooltip.uniquee.stored.reap.name", EndestReap.REAP_STORAGE);
		
		BaseHandler.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessing.VALIDATOR, MidasBlessing.GOLD_COUNTER);
		BaseHandler.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGrace.VALIDATOR, IfritsGrace.LAVA_COUNT);
		BaseHandler.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegis.VALIDATOR, IcarusAegis.FEATHER_TAG);
		
		ForgeRegistries.SOUND_EVENTS.register(ENDER_LIBRARIAN_SOUND.setRegistryName("ender_librarian"));
		ForgeRegistries.SOUND_EVENTS.register(GRIMOIRE_SOUND.setRegistryName("grimoire"));
		ForgeRegistries.SOUND_EVENTS.register(MOMENTUM_SOUND.setRegistryName("momentum"));
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
    
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientInit(FMLClientSetupEvent event)
    {
		EntityRendererManager manager = Minecraft.getInstance().getEntityRenderDispatcher();
		for(PlayerRenderer player : manager.getSkinMap().values())
		{
			player.addLayer(new EnchantmentLayer<>(player));
		}
		addLayer(EntityType.SKELETON, manager);
		addLayer(EntityType.STRAY, manager);
		addLayer(EntityType.WITHER_SKELETON, manager);
		addLayer(EntityType.ARMOR_STAND, manager);
		addLayer(EntityType.GIANT, manager);
		addLayer(EntityType.ZOMBIFIED_PIGLIN, manager);
		addLayer(EntityType.ZOMBIE_VILLAGER, manager);
		addLayer(EntityType.DROWNED, manager);
		addLayer(EntityType.ZOMBIE, manager);
		addLayer(EntityType.HUSK, manager);
		
    }
    
	@SuppressWarnings("unchecked")
	@OnlyIn(Dist.CLIENT)
    private <T extends LivingEntity> void addLayer(EntityType<T> entity, EntityRendererManager manager) {
		LivingRenderer<T, EntityModel<T>> renderer = (LivingRenderer<T, EntityModel<T>>)manager.renderers.get(entity);
    	renderer.addLayer(new EnchantmentLayer<>(renderer));
    }
    
    public void loadLoot(RegistryEvent.Register<GlobalLootModifierSerializer<?>> event)
    {
    	event.getRegistry().register(LootModifier.Serializer.INSTANCE);
    }
    
    public void loadPotion(RegistryEvent.Register<Effect> event)
    {
    	event.getRegistry().register(PESTILENCES_ODIUM_POTION);
    	event.getRegistry().register(ETERNAL_FLAME_POTION);
    }
}
