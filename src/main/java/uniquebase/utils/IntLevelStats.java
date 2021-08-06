package uniquebase.utils;

import net.minecraftforge.common.config.Configuration;

public class IntLevelStats implements IStat
{
	String name;
	String comment;
	final int baseConfig;
	final int levelConfig;
	int base;
	int level;
	
	public IntLevelStats(String name, int base, int level)
	{
		this(name, base, level, null);
	}
	
	public IntLevelStats(String name, int base, int level, String comment)
	{
		this.name = name;
		this.comment = comment;
		this.baseConfig = base;
		this.levelConfig = level;
		this.base = base;
		this.level = level;
	}
	
	@Override
	public void handleConfig(Configuration config, String category)
	{
		base = config.get(category, name+"_base", baseConfig, comment).getInt();
		level = config.get(category, name+"_level", levelConfig).getInt();
	}
		
	public int get(int level)
	{
		return base + (this.level * level);
	}
	
}
