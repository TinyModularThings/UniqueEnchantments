package uniquebase.api;

import net.minecraftforge.common.config.Configuration;

public interface IToggleEnchantment
{
	public String getConfigName();
	
	public void loadFromConfig(Configuration entry);
	public void loadIncompats();
	
	public boolean isEnabled();
}
