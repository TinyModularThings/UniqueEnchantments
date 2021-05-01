package uniqueeutils.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class PhanesRegretEnchantment extends UniqueEnchantment
{
	public static DoubleStat CHANCE = new DoubleStat(0.125D, "chance");

	public PhanesRegretEnchantment()
	{
		super(new DefaultData("phanes_regret", Rarity.UNCOMMON, 1, true, 10, 2, 75), EnchantmentType.ALL, EquipmentSlotType.values());
		setCategory("utils");
	}

	@Override
	public boolean isCurse()
	{
		return true;
	}

	@Override
	public void loadData(Builder config)
	{
		CHANCE.handleConfig(config);
	}

}