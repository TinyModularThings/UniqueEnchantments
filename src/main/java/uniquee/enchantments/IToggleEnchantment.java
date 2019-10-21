package uniquee.enchantments;

import net.minecraftforge.common.config.Configuration;

public interface IToggleEnchantment
{
	public String getConfigName();
	
	public void loadFromConfig(Configuration entry);
}
