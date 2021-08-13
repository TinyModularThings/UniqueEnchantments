package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AmelioratedSharpness extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(1D, "bonus_damage");
	
	public AmelioratedSharpness()
	{
		super(new DefaultData("all", Rarity.RARE, 5, true, 15, 5, 40), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
		
    @Override
	public float calcDamageByCreature(int level, CreatureAttribute creatureType)
    {
    	return BONUS_DAMAGE.getFloat(level);
    }
}
