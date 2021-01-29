package uniquee;

import java.lang.reflect.Field;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.command.Commands;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Reloading;
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
public class UniqueEnchantments
{
	public static final EnchantmentType ALL_TYPES = EnchantmentType.create("ANY", T -> true);
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
		bus.register(this);
//		bus.addListener(this::onCommandLoad);
		MinecraftForge.EVENT_BUS.register(new EntityEvents());
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
    public void onFileChange(Reloading configEvent) 
    {
    	reloadConfig();
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
    
	public void onCommandLoad(RegisterCommandsEvent event) {
    	event.getDispatcher().register(Commands.literal("uniquee").executes((T) -> {
			reloadConfig();
			T.getSource().sendFeedback(new StringTextComponent("Updated Config Data"), true);
			return 0;
		}));
    	
    }
    
	@SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onClientInit(FMLClientSetupEvent event)
    {
		EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
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
    private <T extends LivingEntity> void addLayer(EntityType<T> entity, EntityRendererManager manager) {
		LivingRenderer<T, EntityModel<T>> renderer = (LivingRenderer<T, EntityModel<T>>)manager.renderers.get(entity);
    	renderer.addLayer(new EnchantmentLayer<>(renderer));
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
	
	public static Enchantment register(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment)
		{
			ENCHANTMENTS.add((IToggleEnchantment)ench);
		}
		return ench;
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
