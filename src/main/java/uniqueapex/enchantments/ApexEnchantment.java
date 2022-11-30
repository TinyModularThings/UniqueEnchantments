package uniqueapex.enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.IApexEnchantment;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.MiscUtil;

public class ApexEnchantment extends UniqueEnchantment implements IApexEnchantment
{
	protected ApexEnchantment(String name, EnchantmentCategory typeIn, EquipmentSlot... slots)
	{
		super(new DefaultData(name, Rarity.VERY_RARE, 100, false, false, 0, 0, 0), typeIn, slots);
		setCurse();
	}
	
	@Override
	public Component getFullname(int level) {
		return MiscUtil.createEnchantmentName(this, level, false);
	}
	
	@Override
	public boolean isDiscoverable()
	{
		return false;
	}
	
	@Override
	public int getMaxLevel()
	{
		return 2;
	}

	@Override
	public int getMinLevel()
	{
		return 1;
	}
	
	@Override
	public boolean isTreasureOnly()
	{
		return true;
	}

	@Override
	public boolean isTradeable()
	{
		return false;
	}
	
	@Override
	public boolean allowedInCreativeTab(Item book, CreativeModeTab tab)
	{
		return tab == CreativeModeTab.TAB_SEARCH ? category != null : tab.hasEnchantmentCategory(category);
	}
	
	@Override
	public int getMinCost(int enchantmentLevel)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public int getMaxCost(int enchantmentLevel)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	public Rarity getRarity()
	{
		return Rarity.VERY_RARE;
	}

	@Override
	public boolean isAllowedOnBooks()
	{
		return false;
	}
}
