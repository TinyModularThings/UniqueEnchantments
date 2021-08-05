package uniquee.enchantments.simple;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentBerserk extends UniqueEnchantment
{
	public static final DoubleStat PERCENTUAL_DAMAGE = new DoubleStat(0.503D, "percentual_damage");
	public static final DoubleStat MIN_HEALTH = new DoubleStat(1D, "min_health");
	
	public EnchantmentBerserk()
	{
		super(new DefaultData("berserk", Rarity.RARE, 2, false, 10, 8, 22), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
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
	
	@Override
	public void loadData(Configuration config)
	{
		PERCENTUAL_DAMAGE.handleConfig(config, getConfigName());
		MIN_HEALTH.handleConfig(config, getConfigName());
	}
}
