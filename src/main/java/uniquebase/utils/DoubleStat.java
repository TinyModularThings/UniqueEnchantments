package uniquebase.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class DoubleStat implements IStat
{
	String name;
	String comment;
	double base;
	DoubleValue value;
	
	public DoubleStat(double base, String name)
	{
		this(base, name, null);
	}
	
	public DoubleStat(double base, String name, String comment)
	{
		this.base = base;
		this.name = name;
		this.comment = comment;
	}
	
	public double get()
	{
		return value != null ? value.get() : base;
	}
	
	public double get(double level)
	{
		return get() * level;
	}
	
	public float getFloat()
	{
		return (float)(value != null ? value.get() : base);
	}
	
	public float getFloat(float level)
	{
		return getFloat() * level;
	}
	
	public double getMax(double other, double absoluteMin)
	{
		return Math.max(get(), Math.max(other, absoluteMin));
	}
	
	@Override
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
		value = config.defineInRange(name, base, 0F, Double.MAX_VALUE);
	}
}
