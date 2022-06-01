package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class AmelioratedSmite extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");
	public static final DoubleStat TRANSCENDED_DAMAGE_EXPONENT = new DoubleStat(0.25D, "transcended_damage_exponent");

	public AmelioratedSmite()
	{
		super(new DefaultData("ameliorated_smite", Rarity.RARE, 5, true, false, 6, 4, 30), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(BONUS_DAMAGE, TRANSCENDED_DAMAGE_EXPONENT);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS);
		addIncompats(UE.ADV_SHARPNESS, UE.ADV_BANE_OF_ARTHROPODS);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem || stack.getItem() instanceof AxeItem;
	}
	
    @Override
	public float getDamageBonus(int level, CreatureAttribute creatureType)
    {
    	return creatureType == CreatureAttribute.UNDEAD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
}
