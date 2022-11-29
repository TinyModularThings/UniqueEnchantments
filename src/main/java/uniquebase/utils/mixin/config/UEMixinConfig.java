package uniquebase.utils.mixin.config;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

public final class UEMixinConfig
{
	Map<String, Entry> entries = new Object2ObjectLinkedOpenHashMap<>();
	
	private UEMixinConfig() {}

	public static Builder builder()
	{
		return new Builder(new UEMixinConfig());
	}
	
	public boolean isMixinEnabled(String key)
	{
		Entry entry = entries.get(key);
		return entry == null || entry.enabled;
	}
	
	public Entry getEntry(String key)
	{
		return entries.get(key);
	}
	
	public static class Builder
	{
		UEMixinConfig config;
		
		public Builder(UEMixinConfig config)
		{
			this.config = config;
		}
		
		public Builder add(String name)
		{
			return add(name, "", false);
		}
		
		public Builder add(String name, String comment)
		{
			return add(name, comment, false);
		}
		
		public Builder add(String name, String comment, boolean required)
		{
			config.entries.put(name, new Entry(name, comment, required));
			return this;
		}
		
		public Builder addDependency(String name, String dependency)
		{
			return add(name, "", false);
		}
		
		public Builder addDependency(String name, String dependency, String comment)
		{
			return add(name, comment, false);
		}
		
		public Builder addDependency(String name, String dependency, String comment, boolean required)
		{
			Entry entry = new Entry(name, comment, required);
			entry.dependency = dependency;
			config.entries.put(name, entry);
			return this;
		}
		
		private void loadMixins(Path path)
		{
			Properties props = new Properties();
			try(InputStream stream = Files.newInputStream(path)) {
				props.load(stream);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			for(Entry entry : config.entries.values()) {
				String value = props.getProperty(entry.name);
				if(value == null) continue;
				entry.setConfig(Boolean.valueOf(value));
			}
			for(Entry entry : config.entries.values()) {
				entry.validateDependency(config.entries);
			}
		}
		
		private void saveMixins(Path path)
		{
			try(BufferedWriter writer = Files.newBufferedWriter(path))
			{
				for(Entry entry : config.entries.values()) {
					if(entry.required) continue;
					if(entry.comment != null && !entry.comment.isBlank()) {
						writer.append("#"+entry.comment);
						writer.newLine();
					}
					writer.append(entry.name+"="+Boolean.toString(entry.enabled));
					writer.newLine();
					writer.newLine();
				}
			}
			catch(Exception e) { e.printStackTrace(); }
		}
		
		public UEMixinConfig build(Path path)
		{
			if(Files.exists(path)) loadMixins(path);
			else saveMixins(path);
			return config;
		}
	}
	
	public static class Entry
	{
		String name;
		String comment;
		boolean enabled = true;
		boolean required;
		String dependency = null;
		
		public Entry(String name, String comment)
		{
			this(name, comment, false);
		}
		
		public Entry(String name, String comment, boolean required)
		{
			this.name = name;
			this.comment = comment;
			this.required = required;
		}
		
		public void setConfig(boolean newValue)
		{
			if(required && !newValue) return;
			this.enabled = newValue;
		}
		
		private void validateDependency(Map<String, Entry> map)
		{
			if(dependency == null) return;
			Entry entry = map.get(dependency);
			if(entry == null || entry.isEnabled()) return;
			setConfig(false);
		}
		
		public String getDependency()
		{
			return dependency;
		}
		
		public boolean isEnabled()
		{
			return enabled;
		}
		
		public boolean isRequired()
		{
			return required;
		}
		
		public String getComment()
		{
			return comment;
		}
		
		public String getName()
		{
			return name;
		}
	}
}
