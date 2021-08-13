package uniquebase.api;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

public abstract class BaseUEMod
{
	public static final EnchantmentType ALL_TYPES = EnchantmentType.create("ANY", T -> true);
	static final List<BaseUEMod> ALL_MODS = new ObjectArrayList<>();
	List<IToggleEnchantment> enchantments = new ObjectArrayList<IToggleEnchantment>();
	public ForgeConfigSpec config;

	public BaseUEMod()
	{
		ALL_MODS.add(this);
	}
	
	public void init(IEventBus bus, String name)
	{
		bus.addGenericListener(Enchantment.class, this::registerEnchantments);
		bus.addListener(this::onLoad);
		bus.addListener(this::onFileChange);
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		addConfig(builder);
		loadEnchantments();
		for(int i = 0,m=enchantments.size();i<m;i++)
		{
			enchantments.get(i).loadIncompats();
			enchantments.get(i).loadFromConfig(builder);
		}
		builder.pop();
		config = builder.build();
		ModLoadingContext.get().registerConfig(Type.COMMON, config, name);
	}
	
	protected abstract void loadEnchantments();
	
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

    public void onFileChange(ModConfig.Reloading configEvent) 
    {
    	reloadConfig();
    }
    
	public void registerEnchantments(RegistryEvent.Register<Enchantment> event)
	{
		event.getRegistry().registerAll(enchantments.toArray(new Enchantment[enchantments.size()]));
	}
}
