package uniquee.utils;

import net.minecraftforge.common.config.Configuration;

public class IntLevelStats
{
	String name;
	final int baseConfig;
	final int levelConfig;
	int base;
	int level;
	
	public IntLevelStats(String name, int base, int level)
	{
		this.name = name;
		this.baseConfig = base;
		this.levelConfig = level;
		this.base = base;
		this.level = level;
	}
	
	public void handleConfig(Configuration config, String category)
	{
		base = config.get(category, name+"_base", baseConfig).getInt();
		level = config.get(category, name+"_level", levelConfig).getInt();
	}
	
	public void handleConfig(Configuration config, String category, String comment)
	{
		base = config.get(category, name+"_base", baseConfig, comment).getInt();
		level = config.get(category, name+"_level", levelConfig).getInt();
	}
	
	public int get(int level)
	{
		return base + (this.level * level);
	}
	
}
