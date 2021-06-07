package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentLunaticDespair extends UniqueEnchantment
{
	public static double BONUS_DAMAGE = 0.2D;
	public static double SELF_DAMAGE = 0.25D;
	
	public EnchantmentLunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.UNCOMMON, 1, true, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
		setCategory("battle");
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BONUS_DAMAGE = config.get(getConfigName(), "bonus_damage", 0.2D).getDouble();
		SELF_DAMAGE = config.get(getConfigName(), "self_damage", 0.25D).getDouble();
	}
}