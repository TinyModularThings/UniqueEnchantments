package uniquebase.utils;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.IForgeRegistry;

public class IdStat<T> implements IStat
{
	final String id;
	final IForgeRegistry<T> registry;
	Set<ResourceLocation> values = new ObjectOpenHashSet<>();
	List<String> defaultValues = new ObjectArrayList<>();
	ConfigValue<List<? extends String>> config;
	final String comment;
	
	public IdStat(String config, IForgeRegistry<T> registry)
	{
		this(config, null, registry, new ResourceLocation[0]);
	}
	
	public IdStat(String config, String comment, IForgeRegistry<T> registry)
	{
		this(config, comment, registry, new ResourceLocation[0]);
	}
	
	@SafeVarargs
	public IdStat(String config, IForgeRegistry<T> registry, T... defaultValues)
	{
		this(config, null, registry, defaultValues);
	}
	
	@SafeVarargs
	public IdStat(String config, String comment, IForgeRegistry<T> registry, T... defaultValues)
	{
		this.id = config;
		this.comment = comment;
		this.registry = registry;
		for(T entry : defaultValues)
		{
			values.add(registry.getKey(entry));
			this.defaultValues.add(registry.getKey(entry).toString());
		}
	}
	
	public IdStat(String config, IForgeRegistry<T> registry, ResourceLocation... defaultValues)
	{
		this(config, null, registry, defaultValues);
	}
	
	public IdStat(String config, String comment, IForgeRegistry<T> registry, ResourceLocation... defaultValues)
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
	
	
	@SuppressWarnings("unchecked")
	public void addDefault(T... defaultValues)
	{
		for(T entry : defaultValues)
		{
			values.add(registry.getKey(entry));
			this.defaultValues.add(registry.getKey(entry).toString());
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
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
		this.config = config.defineList(id, defaultValues, T -> registry.containsKey(ResourceLocation.tryParse(T.toString())));
	}
	
	public void onConfigChanged()
	{
		values.clear();
		List<? extends String> list = config.get();
		for(int i = 0;i<list.size();i++)
		{
			ResourceLocation location = ResourceLocation.tryParse(list.get(i));
			if(location != null) values.add(location);
		}
	}
	
	public boolean isEmpty()
	{
		return values.isEmpty();
	}
	
	public boolean contains(T entry)
	{
		return values.contains(registry.getKey(entry));
	}
	
	public boolean contains(ResourceLocation location)
	{
		return values.contains(location);
	}
}
