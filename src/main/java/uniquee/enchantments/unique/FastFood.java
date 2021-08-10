package uniquee.enchantments.unique;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;
import uniquee.utils.IntLevelStats;

public class FastFood extends UniqueEnchantment
{
	public static final IntLevelStats NURISHMENT = new IntLevelStats("nourishment", 1, 2);
	public static DoubleStat SATURATION = new DoubleStat(2D, "saturation");

	public FastFood()
	{
		super(new DefaultData("fastfood", Rarity.RARE, 2, true, 14, 6, 10), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
	
	@Override
	public void loadData(Builder config)
	{
		NURISHMENT.handleConfig(config);
		SATURATION.handleConfig(config);
	}
}
