package uniquee;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.client.EnchantmentLayer;
import uniquee.enchantments.IToggleEnchantment;
import uniquee.enchantments.complex.EnderMendingEnchantment;
import uniquee.enchantments.complex.MomentumEnchantment;
import uniquee.enchantments.complex.PerpetualStrikeEnchantment;
import uniquee.enchantments.complex.SmartAssEnchantment;
import uniquee.enchantments.complex.SpartanWeaponEnchantment;
import uniquee.enchantments.complex.SwiftBladeEnchantment;
import uniquee.enchantments.curse.EnchantmentDeathsOdium;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;
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
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentClimateTranquility;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentDemetersSoul;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderLibrarian;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentIcarusAegis;
import uniquee.enchantments.unique.EnchantmentIfritsGrace;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentPhoenixBlessing;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.handler.potion.PotionPestilencesOdium;

@Mod("uniquee")
public class UniqueEnchantments
{
	static List<IToggleEnchantment> ENCHANTMENTS = new ObjectArrayList<IToggleEnchantment>();
	public static Enchantment BERSERKER = new BerserkEnchantment();
	public static Enchantment ADV_SHARPNESS = new AdvancedDamageEnchantment(0);
	public static Enchantment ADV_SMITE = new AdvancedDamageEnchantment(1);
	public static Enchantment ADV_BANE_OF_ARTHROPODS = new AdvancedDamageEnchantment(2);
	public static Enchantment VITAE = new VitaeEnchantment();
	public static Enchantment SWIFT = new SwiftEnchantment();
	public static Enchantment SAGES_BLESSING = new SagesBlessingEnchantment();
	public static Enchantment ENDER_EYES = new EnderEyesEnchantment();
	public static Enchantment FOCUS_IMPACT = new FocusImpactEnchantment();
	public static Enchantment BONE_CRUSH = new BoneCrusherEnchantment();
	public static Enchantment RANGE = new RangeEnchantment();
	public static Enchantment TREASURERS_EYES = new TreasurersEyesEnchantment();
	
	//Complex
	public static Enchantment SWIFT_BLADE = new SwiftBladeEnchantment();
	public static Enchantment SPARTAN_WEAPON = new SpartanWeaponEnchantment();
	public static Enchantment PERPETUAL_STRIKE = new PerpetualStrikeEnchantment();
	public static Enchantment CLIMATE_TRANQUILITY = new EnchantmentClimateTranquility();
	public static Enchantment MOMENTUM = new MomentumEnchantment();
	public static Enchantment ENDER_MENDING = new EnderMendingEnchantment();
	public static Enchantment SMART_ASS = new SmartAssEnchantment();
	
	//Unique
	public static Enchantment WARRIORS_GRACE = new EnchantmentWarriorsGrace();
	public static Enchantment ENDERMARKSMEN = new EnchantmentEnderMarksmen();
	public static Enchantment ARES_BLESSING = new EnchantmentAresBlessing();
	public static Enchantment ALCHEMISTS_GRACE = new EnchantmentAlchemistsGrace();
	public static Enchantment CLOUD_WALKER = new EnchantmentCloudwalker();
	public static Enchantment FAST_FOOD = new EnchantmentFastFood();
	public static Enchantment NATURES_GRACE = new EnchantmentNaturesGrace();
	public static Enchantment ECOLOGICAL = new EnchantmentEcological();
	public static Enchantment PHOENIX_BLESSING = new EnchantmentPhoenixBlessing();
	public static Enchantment MIDAS_BLESSING = new EnchantmentMidasBlessing();
	public static Enchantment IFRIDS_GRACE = new EnchantmentIfritsGrace();
	public static Enchantment ICARUS_AEGIS = new EnchantmentIcarusAegis();
	public static Enchantment ENDER_LIBRARIAN = new EnchantmentEnderLibrarian();
	public static Enchantment DEMETERS_SOUL = new EnchantmentDemetersSoul();
	
	//Curses
	public static Enchantment PESTILENCES_ODIUM = new EnchantmentPestilencesOdium();
	public static Enchantment DEATHS_ODIUM = new EnchantmentDeathsOdium();
	
	//Potions
	public static Effect PESTILENCES_ODIUM_POTION = new PotionPestilencesOdium();
	public static ForgeConfigSpec CONFIG;
	
	/**
	 * TODO Add information about Tags into Documentation!
	 */
	
	public UniqueEnchantments()
	{
		registerEnchantments(BERSERKER, ADV_SHARPNESS, ADV_SMITE, ADV_BANE_OF_ARTHROPODS, VITAE, SWIFT, SAGES_BLESSING, ENDER_EYES, FOCUS_IMPACT, BONE_CRUSH, RANGE, TREASURERS_EYES);
		registerEnchantments(SWIFT_BLADE, SPARTAN_WEAPON, PERPETUAL_STRIKE, CLIMATE_TRANQUILITY, MOMENTUM, ENDER_MENDING, SMART_ASS);
		registerEnchantments(WARRIORS_GRACE, ENDERMARKSMEN, ARES_BLESSING, ALCHEMISTS_GRACE, CLOUD_WALKER, FAST_FOOD, NATURES_GRACE, ECOLOGICAL, PHOENIX_BLESSING, MIDAS_BLESSING, IFRIDS_GRACE, ICARUS_AEGIS, ENDER_LIBRARIAN, DEMETERS_SOUL);
		registerEnchantments(PESTILENCES_ODIUM, DEATHS_ODIUM);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.register(this);
		MinecraftForge.EVENT_BUS.register(this);
		bus.addGenericListener(Enchantment.class, this::loadEnchantments);
		bus.addGenericListener(Effect.class, this::loadPotion);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		for(int i = 0,m=ENCHANTMENTS.size();i<m;i++)
		{
			ENCHANTMENTS.get(i).loadFromConfig(builder);
		}
		builder.pop();
		CONFIG = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "UniqueEnchantments.toml");
	}
	
    @SubscribeEvent
    public void onLoad(ModConfig.Loading configEvent) 
    {
    	reloadConfig();
    }

    @SubscribeEvent
    public void onFileChange(ModConfig.ConfigReloading configEvent) 
    {
    	reloadConfig();
    }
    
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		if(ModList.get().isLoaded("firstaid"))
		{
			System.out.println("Loaded FistAid");
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
		else
		{
			System.out.println("Didn't Loaded FistAid");
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
    
    void reloadConfig()
    {
    	for(int i = 0,m=ENCHANTMENTS.size();i<m;i++)
    	{
    		ENCHANTMENTS.get(i).onConfigChanged();
    	}
    }
    
    public void loadPotion(RegistryEvent.Register<Effect> event)
    {
    	event.getRegistry().register(PESTILENCES_ODIUM_POTION);
    }
    
	public void loadEnchantments(RegistryEvent.Register<Enchantment> event)
	{
		event.getRegistry().registerAll(ENCHANTMENTS.toArray(new Enchantment[ENCHANTMENTS.size()]));
	}
	
	public static void registerEnchantments(Enchantment...enchantments)
	{
		for(Enchantment enchantment : enchantments)
		{
			if(enchantment instanceof IToggleEnchantment)
			{
				ENCHANTMENTS.add((IToggleEnchantment)enchantment);
			}
		}
	}
}
