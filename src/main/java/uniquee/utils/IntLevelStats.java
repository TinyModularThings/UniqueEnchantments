package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IntLevelStats
{
	String name;
	String comment;
	final int baseConfig;
	final int levelConfig;
	IntValue base;
	IntValue level;
	
	public IntLevelStats(String name, int base, int level)
	{
		this(name, base, level, null);
	}
	
	public IntLevelStats(String name, int base, int level, String comment)
	{
		this.name = name;
		this.comment = comment;
		baseConfig = base;
		levelConfig = level;
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
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
