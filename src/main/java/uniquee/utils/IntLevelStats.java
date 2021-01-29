package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IntLevelStats
{
	String name;
	final int baseConfig;
	final int levelConfig;
	IntValue base;
	IntValue level;
	
	public IntLevelStats(String name, int base, int level)
	{
		this.name = name;
		baseConfig = base;
		levelConfig = level;
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		base = config.defineInRange(name+"_base", baseConfig, 0, Integer.MAX_VALUE);
		level = config.defineInRange(name+"_level", levelConfig, 0, Integer.MAX_VALUE);
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config, String comment)
	{
		config.comment(comment);
		base = config.defineInRange(name+"_base", baseConfig, 0, Integer.MAX_VALUE);
		level = config.defineInRange(name+"_level", levelConfig, 0, Integer.MAX_VALUE);
	}
	
	public int getBase()
	{
		return base != null ? base.get() : baseConfig;
	}
	
	public int getLevel()
	{
		return level != null ? level.get() : levelConfig;
	}
	
	public int get(int level)
	{
		return getBase() + (getLevel() * level);
	}
	
}
