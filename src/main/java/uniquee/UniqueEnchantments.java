package uniquee;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.potion.Effect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.BaseUEMod;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.client.EnchantmentLayer;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.complex.EnderMendingEnchantment;
import uniquee.enchantments.complex.MomentumEnchantment;
import uniquee.enchantments.complex.PerpetualStrikeEnchantment;
import uniquee.enchantments.complex.SmartAssEnchantment;
import uniquee.enchantments.complex.SpartanWeaponEnchantment;
import uniquee.enchantments.complex.SwiftBladeEnchantment;
import uniquee.enchantments.curse.DeathsOdiumEnchantment;
import uniquee.enchantments.curse.PestilencesOdiumEnchantment;
import uniquee.enchantments.simple.AdvancedDamageEnchantment;
import uniquee.enchantments.simple.BerserkEnchantment;
import uniquee.enchantments.simple.BoneCrusherEnchantment;
import uniquee.enchantments.simple.EnderEyesEnchantment;
import uniquee.enchantments.simple.FocusImpactEnchantment;
import uniquee.enchantments.simple.RangeEnchantment;
import uniquee.enchantments.simple.SagesBlessingEnchantment;
import uniquee.enchantments.simple.SwiftEnchantment;
import uniquee.enchantments.simple.TreasurersEyesEnchantment;
import uniquee.enchantments.simple.VitaeEnchantment;
import uniquee.enchantments.unique.AlchemistsGraceEnchantment;
import uniquee.enchantments.unique.AresBlessingEnchantment;
import uniquee.enchantments.unique.ClimateTranquilityEnchantment;
import uniquee.enchantments.unique.CloudwalkerEnchantment;
import uniquee.enchantments.unique.DemetersSoulEnchantment;
import uniquee.enchantments.unique.EcologicalEnchantment;
import uniquee.enchantments.unique.EnderLibrarianEnchantment;
import uniquee.enchantments.unique.EnderMarksmenEnchantment;
import uniquee.enchantments.unique.FastFoodEnchantment;
import uniquee.enchantments.unique.IcarusAegisEnchantment;
import uniquee.enchantments.unique.IfritsGraceEnchantment;
import uniquee.enchantments.unique.MidasBlessingEnchantment;
import uniquee.enchantments.unique.NaturesGraceEnchantment;
import uniquee.enchantments.unique.PhoenixBlessingEnchantment;
import uniquee.enchantments.unique.WarriorsGraceEnchantment;
import uniquee.handler.EntityEvents;
import uniquee.handler.potion.PestilencesOdiumPotion;

@Mod("uniquee")
public class UniqueEnchantments extends BaseUEMod
{
	static List<IToggleEnchantment> ENCHANTMENTS = ObjectLists.synchronize(new ObjectArrayList<IToggleEnchantment>());
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
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM;
	public static Enchantment DEATHS_ODIUM;
	
	//Potions
	public static Effect PESTILENCES_ODIUM_POTION;
	public static ForgeConfigSpec CONFIG;
	
	public UniqueEnchantments()
	{
		BERSERKER = register(new BerserkEnchantment());
		ADV_SHARPNESS = register(new AdvancedDamageEnchantment(0));
		ADV_SMITE = register(new AdvancedDamageEnchantment(1));
		ADV_BANE_OF_ARTHROPODS = register(new AdvancedDamageEnchantment(2));
		VITAE = register(new VitaeEnchantment());
		SWIFT = register(new SwiftEnchantment());
		SAGES_BLESSING = register(new SagesBlessingEnchantment());
		ENDER_EYES = register(new EnderEyesEnchantment());
		FOCUS_IMPACT = register(new FocusImpactEnchantment());
		BONE_CRUSH = register(new BoneCrusherEnchantment());
		RANGE = register(new RangeEnchantment());
		TREASURERS_EYES = register(new TreasurersEyesEnchantment());
		
		SWIFT_BLADE = register(new SwiftBladeEnchantment());
		SPARTAN_WEAPON = register(new SpartanWeaponEnchantment());
		PERPETUAL_STRIKE = register(new PerpetualStrikeEnchantment());
		CLIMATE_TRANQUILITY = register(new ClimateTranquilityEnchantment());
		MOMENTUM = register(new MomentumEnchantment());
		ENDER_MENDING = register(new EnderMendingEnchantment());
		SMART_ASS = register(new SmartAssEnchantment());

		WARRIORS_GRACE = register(new WarriorsGraceEnchantment());
		ENDERMARKSMEN = register(new EnderMarksmenEnchantment());
		ARES_BLESSING = register(new AresBlessingEnchantment());
		ALCHEMISTS_GRACE = register(new AlchemistsGraceEnchantment());
		CLOUD_WALKER = register(new CloudwalkerEnchantment());
		FAST_FOOD = register(new FastFoodEnchantment());
		NATURES_GRACE = register(new NaturesGraceEnchantment());
		ECOLOGICAL = register(new EcologicalEnchantment());
		PHOENIX_BLESSING = register(new PhoenixBlessingEnchantment());
		MIDAS_BLESSING = register(new MidasBlessingEnchantment());
		IFRIDS_GRACE = register(new IfritsGraceEnchantment());
		ICARUS_AEGIS = register(new IcarusAegisEnchantment());
		ENDER_LIBRARIAN = register(new EnderLibrarianEnchantment());
		DEMETERS_SOUL = register(new DemetersSoulEnchantment());
		
		PESTILENCES_ODIUM = register(new PestilencesOdiumEnchantment());
		DEATHS_ODIUM = register(new DeathsOdiumEnchantment());
		
		PESTILENCES_ODIUM_POTION = new PestilencesOdiumPotion();
		
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		init(bus, "UniqueEnchantment.toml");
		bus.register(this);
		MinecraftForge.EVENT_BUS.register(EntityEvents.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		bus.addGenericListener(Effect.class, this::loadPotion);

		EntityEvents.INSTANCE.registerStorageTooltip(MIDAS_BLESSING, "tooltip.uniqee.stored.gold.name", MidasBlessingEnchantment.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerStorageTooltip(IFRIDS_GRACE, "tooltip.uniqee.stored.lava.name", IfritsGraceEnchantment.LAVA_COUNT);
		EntityEvents.INSTANCE.registerStorageTooltip(ICARUS_AEGIS, "tooltip.uniqee.stored.feather.name", IcarusAegisEnchantment.FEATHER_TAG);
		EntityEvents.INSTANCE.registerStorageTooltip(ENDER_MENDING, "tooltip.uniqee.stored.repair.name", EnderMendingEnchantment.ENDER_TAG);
		
		
		EntityEvents.INSTANCE.registerAnvilHelper(MIDAS_BLESSING, MidasBlessingEnchantment.VALIDATOR, MidasBlessingEnchantment.GOLD_COUNTER);
		EntityEvents.INSTANCE.registerAnvilHelper(IFRIDS_GRACE, IfritsGraceEnchantment.VALIDATOR, IfritsGraceEnchantment.LAVA_COUNT);
		EntityEvents.INSTANCE.registerAnvilHelper(ICARUS_AEGIS, IcarusAegisEnchantment.VALIDATOR, IcarusAegisEnchantment.FEATHER_TAG);
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
    
    @SuppressWarnings({"rawtypes", "unchecked" })
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientInit(FMLClientSetupEvent event)
    {
		EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
		for(PlayerRenderer player : manager.getSkinMap().values())
		{
			player.addLayer(new EnchantmentLayer<>(player));
		}
		for(Entry<Class<? extends Entity>, EntityRenderer<? extends Entity>> entry : manager.renderers.entrySet())
		{
			if(entry.getValue() instanceof LivingRenderer && (AbstractSkeletonEntity.class.isAssignableFrom(entry.getKey()) || ZombieEntity.class.isAssignableFrom(entry.getKey())))
			{
				((LivingRenderer)entry.getValue()).addLayer(new EnchantmentLayer<>((LivingRenderer)entry.getValue()));
			}
		}		
    }
    
    public void loadPotion(RegistryEvent.Register<Effect> event)
    {
    	event.getRegistry().register(PESTILENCES_ODIUM_POTION);
    }
}
