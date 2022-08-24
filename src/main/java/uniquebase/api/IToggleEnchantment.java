package uniquebase.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

public interface IToggleEnchantment
{
	public ResourceLocation getId();
	
	public String getConfigName();
	
	public void loadFromConfig(ForgeConfigSpec.Builder entry);
	public default void onConfigChanged() {}
	public default void loadIncompats(){}
	public boolean isEnabled();
	public int getHardCap();
	public default int getTranscendedLevel() { return 1000; }
}
