package uniquee.utils;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class IdStat
{
	final String id;
	final IForgeRegistry<?> registry;
	Set<ResourceLocation> values = new ObjectOpenHashSet<>();
	List<String> defaultValues = new ObjectArrayList<>();
	ConfigValue<List<? extends String>> config;
	final String comment;
	
	public IdStat(String config, IForgeRegistry<?> registry)
	{
		this(config, null, registry, new ResourceLocation[0]);
	}
	
	public IdStat(String config, IForgeRegistry<?> registry, IForgeRegistryEntry<?>... defaultValues)
	{
		this(config, null, registry, defaultValues);
	}
	
	public IdStat(String config, String comment, IForgeRegistry<?> registry, IForgeRegistryEntry<?>... defaultValues)
	{
		this.id = config;
		this.comment = comment;
		this.registry = registry;
		for(IForgeRegistryEntry<?> entry : defaultValues)
		{
			values.add(entry.getRegistryName());
			this.defaultValues.add(entry.getRegistryName().toString());
		}
	}
	
	public IdStat(String config, IForgeRegistry<?> registry, ResourceLocation... defaultValues)
	{
		this(config, null, registry, defaultValues);
	}
	
	public IdStat(String config, String comment, IForgeRegistry<?> registry, ResourceLocation... defaultValues)
	{
		this.id = config;
		this.comment = comment;
		this.registry = registry;
		for(ResourceLocation entry : defaultValues)
		{
			values.add(entry);
			this.defaultValues.add(entry.toString());
		}
	}
	
	public void addDefault(IForgeRegistryEntry<?>... defaultValues)
	{
		for(IForgeRegistryEntry<?> entry : defaultValues)
		{
			values.add(entry.getRegistryName());
			this.defaultValues.add(entry.getRegistryName().toString());
		}
	}
	
	public void addDefault(ResourceLocation... defaultValues)
	{
		for(ResourceLocation entry : defaultValues)
		{
			values.add(entry);
			this.defaultValues.add(entry.toString());
		}
	}

	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
		this.config = config.defineList(id, defaultValues, T -> registry.containsKey(ResourceLocation.tryCreate((String)T)));
	}
	
	public void onConfigChanged()
	{
		values.clear();
		List<? extends String> list = config.get();
		for(int i = 0;i<list.size();i++)
		{
			ResourceLocation location = ResourceLocation.tryCreate(list.get(i));
			if(location != null) values.add(location);
		}
	}
	
	public boolean contains(ResourceLocation location)
	{
		return values.contains(location);
	}
}
