package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class AmelioratedSharpness extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(1D, "bonus_attack_damage");
	public static final DoubleStat TRANSCENDED_DAMAGE_MULTIPLIER = new DoubleStat(2.0D, "transcended_damage_multiplier");
	
	public AmelioratedSharpness()
	{
		super(new DefaultData("ameliorated_sharpness", Rarity.VERY_RARE, 5, true, false, 25, 8, 20), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE, TRANSCENDED_DAMAGE_MULTIPLIER);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS);
		addIncompats(UE.ADV_SMITE, UE.ADV_BANE_OF_ARTHROPODS);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem;
	}
		
    @Override
	public float getDamageBonus(int level, CreatureAttribute creatureType)
    {
    	return BONUS_DAMAGE.getFloat(level);
    }
}
