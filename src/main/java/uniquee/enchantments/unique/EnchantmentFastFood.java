package uniquee.enchantments.unique;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntLevelStats;

public class EnchantmentFastFood extends UniqueEnchantment
{
	public static final IntLevelStats NURISHMENT = new IntLevelStats("nourishment", 2, 1);
	public static DoubleStat SATURATION = new DoubleStat(0.5D, "saturation");

	public EnchantmentFastFood()
	{
		super(new DefaultData("fastfood", Rarity.RARE, true, 14, 6, 10), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 2;
	}
		
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof LootBonusEnchantment ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Builder config)
	{
		NURISHMENT.handleConfig(config);
		SATURATION.handleConfig(config);
	}
}
