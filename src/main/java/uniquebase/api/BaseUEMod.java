package uniquebase.api;

import java.io.File;
import java.util.Collections;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public abstract class BaseUEMod
{
	static final List<BaseUEMod> MODS = new ObjectArrayList<>();
	public final List<Enchantment> enchantments = new ObjectArrayList<>();
	private Configuration config;
	String modName;
	
	public BaseUEMod()
	{
		MODS.add(this);
	}
	
	public Enchantment register(Enchantment ench)
	{
		enchantments.add(ench);
		return ench;
	}
	
	public Configuration getConfig()
	{
		return config;
	}
	
	public void init(String modName, File file)
	{
		this.modName = modName;
		config = new Configuration(file);
		MinecraftForge.EVENT_BUS.register(this);
		addEnchantments();
		for(int i = 0;i<enchantments.size();i++)
		{
			Enchantment ench = enchantments.get(i);
			ForgeRegistries.ENCHANTMENTS.register(ench);
			if(ench instanceof IToggleEnchantment)
			{
				((IToggleEnchantment)ench).loadIncompats();
			}
		}
		loadConfig();
		ModContainer baseContainer = Loader.instance().getIndexedModList().get("uniquebase");
		ModContainer current = Loader.instance().activeModContainer();
		baseContainer.getMetadata().childMods.add(current);
		current.getMetadata().parentMod = baseContainer;
	}
	
	protected abstract void addEnchantments();
	
	@SubscribeEvent
	public void onConfigChange(OnConfigChangedEvent evt)
	{
		if(evt.getModID().equalsIgnoreCase(modName)) return;
		try {
			for(Enchantment ench : enchantments)
			{
				if(ench instanceof IToggleEnchantment)
				{
					((IToggleEnchantment)ench).loadFromConfig(config);	
				}
			}
			config.save();
		}
		catch(Exception e) {}
	}
	
	private void loadConfig()
	{
		try {
			config.load();
			for(Enchantment ench : enchantments)
			{
				if(ench instanceof IToggleEnchantment)
				{
					((IToggleEnchantment)ench).loadFromConfig(config);	
				}
			}
		}
		catch(Exception e) { e.printStackTrace(); }
		finally { config.save(); }
	}
	
	public static void reload()
	{
		for(BaseUEMod mod : MODS)
		{
			mod.loadConfig();
		}
	}
	
	public static List<BaseUEMod> getMods()
	{
		return Collections.unmodifiableList(MODS);
	}
}
