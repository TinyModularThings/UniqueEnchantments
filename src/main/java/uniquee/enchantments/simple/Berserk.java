package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquee.UniqueEnchantments;

public class Berserk extends UniqueEnchantment
{
	public static final DoubleStat PERCENTUAL_DAMAGE = new DoubleStat(0.503D, "percentual_damage");
	public static final DoubleStat MIN_HEALTH = new DoubleStat(1D, "min_health");
	
	public Berserk()
	{
		super(new DefaultData("berserk", Rarity.RARE, 2, false, 10, 8, 22), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND);
		addStats(MIN_HEALTH, PERCENTUAL_DAMAGE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
        return stack.getItem() instanceof ItemAxe;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.SWIFT_BLADE, UniqueEnchantments.SPARTAN_WEAPON);
	}
}
