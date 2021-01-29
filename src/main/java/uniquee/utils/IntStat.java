package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IntStat
{
	String name;
	int base;
	IntValue value;
	
	public IntStat(int base, String name)
	{
		this.base = base;
		this.name = name;
	}
	
	public int get()
	{
		return value != null ? value.get() : base;
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		value = config.defineInRange(name, base, 0, Integer.MAX_VALUE);
	}
}
