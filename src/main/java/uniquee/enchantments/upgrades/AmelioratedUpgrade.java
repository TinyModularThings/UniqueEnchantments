package uniquee.enchantments.upgrades;

import java.util.UUID;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.EnchantedUpgrade;
import uniquee.UE;

public class AmelioratedUpgrade extends EnchantedUpgrade
{
	public static final UUID DAMAGE_ID = UUID.fromString("162f0952-2306-4ed0-b7ef-c052077af1c4");
	
	public AmelioratedUpgrade()
	{
		super("uniquee", "amolirated", "upgrade.uniquee.attack", () -> UE.ADV_SHARPNESS);
	}
	
	@Override
	public boolean isValid(ItemStack stack)
	{
		return EnchantmentCategory.WEAPON.canEnchant(stack.getItem()) || stack.getItem() instanceof HoeItem;
	}
}
