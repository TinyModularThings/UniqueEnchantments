package uniquebase.utils;

import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;

public class BooleanStat implements IStat {

	String name;
	String comment;
	boolean base;
	BooleanValue value;
	
	public BooleanStat(boolean base, String name) {
		this(base, name, null);
	}
	
	public BooleanStat(boolean base, String name, String comment) {
		this.base = base;
		this.name = name;
		this.comment = comment;
	}
	
	public boolean get() {
		return value != null ? value.get() : base;
	}
	
	@Override
	public void handleConfig(Builder config) {
		if(comment != null) config.comment(comment);
		value = config.define(name, base);
	}

}
