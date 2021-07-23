package uniquee.utils;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class IntStat
{
	String name;
	String comment;
	int base;
	IntValue value;
	
	public IntStat(int base, String name)
	{
		this(base, name, null);
	}
	
	public IntStat(int base, String name, String comment)
	{
		this.base = base;
		this.name = name;
		this.comment = comment;
	}
	
	public int get()
	{
		return value != null ? value.get() : base;
	}
	
	public void handleConfig(ForgeConfigSpec.Builder config)
	{
		if(comment != null) config.comment(comment);
		value = config.defineInRange(name, base, 0, Integer.MAX_VALUE);
	}
}
