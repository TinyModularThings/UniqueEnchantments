package uniquebase.utils;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class IdStat implements IStat
{
	final String id;
	final IForgeRegistry<?> registry;
	Set<ResourceLocation> values = new ObjectOpenHashSet<>();
	List<String> defaultValues = new ObjectArrayList<>();
	
	final String comment;
	
	public IdStat(String config, IForgeRegistry<?> registry)
	{
		this(config, null, registry, new ResourceLocation[0]);
	}
	
	public IdStat(String config, String comment, IForgeRegistry<?> registry)
	{
		this(config, comment, registry, new ResourceLocation[0]);
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
	
	@Override
	public void handleConfig(Configuration config, String category)
	{
		String[] array = config.get(category, id, defaultValues.toArray(new String[defaultValues.size()]), comment).getStringList();
		values.clear();
		for(int i = 0;i<array.length;i++)
		{
			try
			{
				ResourceLocation location = new ResourceLocation(array[i]);
				if(registry.containsKey(location)) values.add(location);
			}
			catch(Exception e) {}
		}
	}
		
	public boolean contains(ResourceLocation location)
	{
		return values.contains(location);
	}
}
