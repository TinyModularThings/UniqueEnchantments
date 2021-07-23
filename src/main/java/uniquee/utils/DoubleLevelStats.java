package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class DoubleLevelStats
{
	String name;
	String comment;
	final double baseConfig;
	final double levelConfig;
	DoubleValue base;
	DoubleValue level;
	
	public DoubleLevelStats(String name, double base, double level)
	{
		this(name, base, level, null);
	}
	
	public DoubleLevelStats(String name, double base, double level, String comment)
	{
		this.name = name;
		this.comment = comment;
		baseConfig = base;
		levelConfig = level;
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
		base = config.defineInRange(name+"_base", baseConfig, 0, Double.MAX_VALUE);
		level = config.defineInRange(name+"_level", levelConfig, 0, Double.MAX_VALUE);
	}
	
	public double getBase()
	{
		return base != null ? base.get() : baseConfig;
	}
	
	public double getLevel()
	{
		return level != null ? level.get() : levelConfig;
	}
	
	public double getAsDouble(int level)
	{
		return getBase() + (getLevel() * level);
	}
	
	public float getAsFloat(int level)
	{
		return (float)(getBase() + (getLevel() * level));
	}
	
	public float getDevided(int level)
	{
		return (float)(getBase() / (getLevel() * level));
	}
}
