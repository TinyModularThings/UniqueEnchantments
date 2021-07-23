package uniquee.utils;

import net.minecraftforge.common.config.Configuration;

public class DoubleStat
{
	String name;
	String comment;
	double base;
	double value;
	
	public DoubleStat(double base, String name)
	{
		this(base, name, null);
	}
	
	public DoubleStat(double base, String name, String comment)
	{
		this.base = base;
		this.value = base;
		this.name = name;
		this.comment = comment;
	}
	
	public double get()
	{
		return value;
	}
	
	public float getFloat()
	{
		return (float)value;
	}
	
	public void handleConfig(Configuration config, String category)
	{
		value = config.get(category, name, base, comment).getDouble();
	}
}
