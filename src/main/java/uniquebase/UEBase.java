package uniquebase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import uniquebase.api.BaseUEMod;
import uniquebase.api.IKeyBind;
import uniquebase.api.crops.CropHarvestRegistry;
import uniquebase.handler.BaseHandler;
import uniquebase.handler.ClientProxy;
import uniquebase.handler.EnchantmentHandler;
import uniquebase.handler.LootManager;
import uniquebase.handler.PackHandler;
import uniquebase.handler.Proxy;
import uniquebase.networking.PacketHandler;
import uniquebase.utils.ICurioHelper;

@Mod("uniquebase")
public class UEBase
{
	public static Logger LOGGER = LogManager.getLogger("UE");
	public static final PacketHandler NETWORKING = new PacketHandler();
	public static final Proxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> Proxy::new);
	public static IKeyBind ENCHANTMENT_GUI = IKeyBind.empty();
	public static IKeyBind ENCHANTMENT_ICONS = IKeyBind.empty();
	public static ICurioHelper CURIO = ICurioHelper.dummy();
	
	public static BooleanValue DISABLE_JEI;
	public static ForgeConfigSpec CONFIG;
	
	public static boolean SHOULD_LOAD_RESOURCEPACK = false;
	
	public static final SoundEvent ANVIL_STACK = new SoundEvent(new ResourceLocation("uniquebase", "anvil_stacks"));
	
	public UEBase()
	{
		boolean addonsLoaded = ModList.get().isLoaded("uniquee") || ModList.get().isLoaded("uniqueapex") || ModList.get().isLoaded("uniquebattle") || ModList.get().isLoaded("uniqueutil");
		ENCHANTMENT_GUI = PROXY.registerKey("Enchantment Gui", 342);
		ENCHANTMENT_ICONS = PROXY.registerKey("Enchantment Icons", 342);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		BaseConfig.TWEAKS.load(builder, addonsLoaded);
		BaseConfig.BOOKS.load(builder, addonsLoaded);
		BaseConfig.ICONS.load(builder, addonsLoaded);
		BaseConfig.TOOLTIPS.load(builder, addonsLoaded);
		builder.push("jei");
		builder.comment("If the JEI plugin should be disabled");
		DISABLE_JEI = builder.define("Disable JEI Plugin", false);		
		builder.pop();
		CONFIG = builder.build();
		BaseUEMod.validateConfigFolder();
		ModLoadingContext.get().registerConfig(Type.COMMON, CONFIG, "ue/UEBase.toml");
		MinecraftForge.EVENT_BUS.register(BaseHandler.INSTANCE);
		MinecraftForge.EVENT_BUS.register(EnchantmentHandler.INSTANCE);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		PROXY.preInit(bus);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		bus.register(this);
		bus.addListener(this::registerContent);
		
		ForgeRegistries.SOUND_EVENTS.register("anvil_stacks", ANVIL_STACK);
		Path path = FMLPaths.CONFIGDIR.get().resolve("ue").resolve("resourcePack.txt");
		if(Files.notExists(path))
		{
			try { Files.createDirectories(path.getParent()); }
			catch(IOException e1) {}
			SHOULD_LOAD_RESOURCEPACK = true;
			try(BufferedWriter writer = Files.newBufferedWriter(path))
			{
				writer.write("This File only exists so the new Unique Enchantments Enchanted Book texture pack is loaded by default but still can be turned off. Since neither Vanilla or forge are capable of having defaulty enabled ResourcePacks and forges config sucks so bad that you can't have configs that load before texture packs load so we have to do this shitty solution to make it work!");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		if(FMLEnvironment.dist.isClient()) {
			PackHandler.loaded();
		}
	}
	
    @SubscribeEvent
	public void postInit(FMLCommonSetupEvent setup) 
	{
		CropHarvestRegistry.INSTANCE.init();
		PROXY.init();
	}
    
	public void registerContent(RegisterEvent event)
	{
		if(event.getRegistryKey().equals(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS))
		{
	    	event.getForgeRegistry().register("ue_loot", LootManager.CODEC);
		}	
	}
    
	protected void reloadConfig()
    {
		EnchantmentHandler.INSTANCE.cleanCache();
		BaseConfig.TWEAKS.onConfigChanged();
		BaseConfig.BOOKS.onConfigsChanged();
		BaseConfig.ICONS.onConfigsChanged();
		BaseConfig.TOOLTIPS.onConfigsChanged();
    }
    
    public void onLoad(ModConfigEvent.Loading configEvent) 
    {
    	reloadConfig();
    }

    public void onFileChange(ModConfigEvent.Reloading configEvent) 
    {
    	reloadConfig();
    }
}
