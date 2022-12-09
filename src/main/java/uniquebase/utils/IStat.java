package uniquebase.utils;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IStat
{
	public void handleConfig(ForgeConfigSpec.Builder config);
	public default void onConfigChanged() {}
}
