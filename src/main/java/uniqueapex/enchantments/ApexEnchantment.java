package uniqueapex.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import uniquebase.api.IApexEnchantment;
import uniquebase.api.UniqueEnchantment;

public class ApexEnchantment extends UniqueEnchantment implements IApexEnchantment
{
	protected ApexEnchantment(String name, EnchantmentType typeIn, EquipmentSlotType... slots)
	{
		super(new DefaultData(name, Rarity.VERY_RARE, 100, false, false, 0, 0, 0), typeIn, slots);
		setCurse();
	}
	
	@Override
	public ITextComponent getFullname(int level)
	{
		IFormattableTextComponent name = new TranslationTextComponent(this.getDescriptionId()).withStyle(TextFormatting.GRAY);
		return level != 1 || this.getMaxLevel() != 1 ? name.append(" ").append(new TranslationTextComponent("enchantment.level." + level)) : name;
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
		return false;
	}

	@Override
	public boolean isTradeable()
	{
		return false;
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
