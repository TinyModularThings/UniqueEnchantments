package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class AmelioratedSharpness extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(1D, "bonus_attack_damage");
	public static final DoubleStat TRANSCENDED_DAMAGE_MULTIPLIER = new DoubleStat(2.0D, "transcended_damage_multiplier");
	
	public AmelioratedSharpness()
	{
		super(new DefaultData("ameliorated_sharpness", Rarity.VERY_RARE, 5, true, false, 25, 8, 20).setTrancendenceLevel(300), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
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
	public float getDamageBonus(int level, MobType creatureType)
    {
    	return BONUS_DAMAGE.getFloat(level);
    }
}
