package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AmelioratedSharpness extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(1D, "bonus_damage");
	
	public AmelioratedSharpness()
	{
		super(new DefaultData("all", Rarity.RARE, 5, true, 15, 5, 40), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(BONUS_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe;
	}
		
    @Override
	public float calcDamageByCreature(int level, EnumCreatureAttribute creatureType)
    {
    	return BONUS_DAMAGE.getFloat(level);
    }
}