package uniquee.utils;

import net.minecraftforge.common.config.Configuration;

public class IntStat
{
	String name;
	String comment;
	int base;
	int value;
	
	public IntStat(int base, String name)
	{
		this(base, name, null);
	}
	
	public IntStat(int base, String name, String comment)
	{
		this.base = base;
		this.value = base;
		this.name = name;
		this.comment = comment;
	}
	
	public int get()
	{
		return value;
	}
	
	public void handleConfig(Configuration config, String category)
	{
		value = config.get(category, name, base, comment).getInt();
	}
}
