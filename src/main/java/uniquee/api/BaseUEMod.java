package uniquee.api;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.ConfigReloading;
import net.minecraftforge.fml.config.ModConfig.Type;
import uniquee.enchantments.IToggleEnchantment;

public class BaseUEMod
{
	List<IToggleEnchantment> enchantments = new ObjectArrayList<IToggleEnchantment>();
	public ForgeConfigSpec config;

	
	public void init(IEventBus bus, String name)
	{
		bus.addGenericListener(Enchantment.class, this::loadEnchantments);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		addConfig(builder);
		for(int i = 0,m=enchantments.size();i<m;i++)
		{
			enchantments.get(i).loadIncompats();
			enchantments.get(i).loadFromConfig(builder);
		}
		builder.pop();
		config = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, config, name);
	}
	
	protected void addConfig(ForgeConfigSpec.Builder builder) {}
	
	protected Enchantment register(Enchantment ench)
	{
		if(ench instanceof IToggleEnchantment)
		{
			enchantments.add((IToggleEnchantment)ench);
		}
		return ench;
	}
	
    protected void reloadConfig()
    {
    	for(int i = 0,m=enchantments.size();i<m;i++)
    	{
    		enchantments.get(i).onConfigChanged();
    	}
    }
    
    public void onLoad(ModConfig.Loading configEvent) 
    {
    	reloadConfig();
    }

    public void onFileChange(ConfigReloading configEvent) 
    {
    	reloadConfig();
    }
    
	public void loadEnchantments(RegistryEvent.Register<Enchantment> event)
	{
		event.getRegistry().registerAll(enchantments.toArray(new Enchantment[enchantments.size()]));
	}
}
