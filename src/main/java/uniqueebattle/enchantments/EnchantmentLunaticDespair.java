package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentLunaticDespair extends UniqueEnchantment
{
	public static DoubleStat BONUS_DAMAGE = new DoubleStat(0.2D, "bonus_damage");
	public static DoubleStat SELF_DAMAGE = new DoubleStat(0.25D, "self_damage");
	
	public EnchantmentLunaticDespair()
	{
		super(new DefaultData("lunatic_despair", Rarity.UNCOMMON, 1, true, 10, 4, 40), EnchantmentType.ALL, EquipmentSlotType.values());
		setCategory("battle");
	}
	
	@Override
	public boolean isCurse()
	{
		return true;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		BONUS_DAMAGE.handleConfig(config);
		SELF_DAMAGE.handleConfig(config);
	}
}