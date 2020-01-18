package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class DoubleStat
{
	String name;
	double base;
	DoubleValue value;
	
	public DoubleStat(double base, String name)
	{
		this.base = base;
		this.name = name;
	}
	
	public double get()
	{
		return value != null ? value.get() : base;
	}
	
	public float getFloat()
	{
		return (float)(value != null ? value.get() : base);
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		value = config.defineInRange(name, base, 0F, Double.MAX_VALUE);
	}
}
