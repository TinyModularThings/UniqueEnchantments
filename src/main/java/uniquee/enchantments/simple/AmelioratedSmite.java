package uniquee.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UE;

public class AmelioratedSmite extends UniqueEnchantment
{
	public static final DoubleStat BONUS_DAMAGE = new DoubleStat(3D, "bonus_damage");
	public static final DoubleStat TRANSCENDED_DAMAGE_EXPONENT = new DoubleStat(0.25D, "transcended_damage_exponent");

	public AmelioratedSmite()
	{
		super(new DefaultData("ameliorated_smite", Rarity.RARE, 5, true, false, 6, 4, 30).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
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
		return stack.getItem() instanceof HoeItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem;
	}
	
    @Override
	public float getDamageBonus(int level, MobType creatureType)
    {
    	return creatureType == MobType.UNDEAD ? BONUS_DAMAGE.getFloat(level) : 0F;
    }
}
