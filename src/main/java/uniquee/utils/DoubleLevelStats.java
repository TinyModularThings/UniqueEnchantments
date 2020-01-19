package uniquee.utils;

import net.minecraftforge.common.config.Configuration;

public class DoubleLevelStats
{
	String name;
	final double baseConfig;
	final double levelConfig;
	double base;
	double level;
	
	public DoubleLevelStats(String name, double base, double level)
	{
		this.name = name;
		this.baseConfig = base;
		this.levelConfig = level;
		this.base = base;
		this.level = level;
	}
	
	public void handleConfig(Configuration config, String category)
	{
		base = config.get(category, name+"_base", baseConfig).getDouble();
		level = config.get(category, name+"_level", levelConfig).getDouble();
	}
	public void handleConfig(Configuration config, String category, String comment)
	{
		base = config.get(category, name+"_base", baseConfig, comment).getDouble();
		level = config.get(category, name+"_level", levelConfig).getDouble();
	}
	
	public double getAsDouble(int level)
	{
		return base + (this.level * level);
	}
	
	public float getAsFloat(int level)
	{
		return (float)(base + (this.level * level));
	}
	
	public float getDevided(int level)
	{
		return (float)(base / (this.level * level));
	}
}
