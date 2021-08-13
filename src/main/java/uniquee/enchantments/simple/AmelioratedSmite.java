package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AmelioratedSmite extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");

	public AmelioratedSmite()
	{
		super(new DefaultData("undead", Rarity.RARE, 5, true, 6, 4, 30), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem || stack.getItem() instanceof AxeItem;
	}
	
    @Override
	public float calcDamageByCreature(int level, CreatureAttribute creatureType)
    {
    	return creatureType == CreatureAttribute.UNDEAD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
}
