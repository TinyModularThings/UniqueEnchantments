package uniquee.enchantments;

import net.minecraftforge.common.ForgeConfigSpec;

public interface IToggleEnchantment
{
	public String getConfigName();
	
	public void loadFromConfig(ForgeConfigSpec.Builder entry);
	
	public default void onConfigChanged(){}
	public default void loadIncompats(){}
	public boolean isEnabled();
}
