package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentLunaticDespair extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(0.2D, "bonus_damage");
	public static final DoubleStat SELF_DAMAGE = new DoubleStat(0.25D, "self_damage");
	
	public EnchantmentLunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.VERY_RARE, 2, true, 10, 4, 40), EnumEnchantmentType.ALL, EntityEquipmentSlot.values());
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
		BONUS_DAMAGE.handleConfig(config, getConfigName());
		SELF_DAMAGE.handleConfig(config, getConfigName());
	}
}