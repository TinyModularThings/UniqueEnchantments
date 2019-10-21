package uniquee.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;

public abstract class UniqueEnchantment extends Enchantment implements IToggleEnchantment
{
	boolean enabled = false;
	String configName;
	
	protected UniqueEnchantment(String name, Rarity rarityIn, EnumEnchantmentType typeIn, EntityEquipmentSlot[] slots)
	{
		super(rarityIn, typeIn, slots);
		setName("uniquee."+name);
		setRegistryName(name);
		this.configName = name;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled ? super.canApplyAtEnchantingTable(stack) : false;
	}

	@Override
	public String getConfigName()
	{
		return configName;
	}
	
	@Override
	public final void loadFromConfig(Configuration config)
	{
		enabled = config.get(getConfigName(), "enabled", true).getBoolean();
		loadData(config);
	}
	
	public abstract void loadData(Configuration config);
}
