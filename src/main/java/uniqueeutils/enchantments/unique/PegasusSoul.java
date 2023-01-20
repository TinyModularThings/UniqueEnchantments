package uniqueeutils.enchantments.unique;

import java.util.UUID;

import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class PegasusSoul extends UniqueEnchantment
{
	public static final UUID SPEED_MOD = UUID.fromString("2f492ac4-5762-4508-8978-2a6a73159f48");
	public static final String TRIGGER = "pegasus_trigger";
	public static final String ENABLED = "pegasus_enabled";
	public static final DoubleStat SPEED = new DoubleStat(0.01D, "speed buff");
	
	public PegasusSoul()
	{
		super(new DefaultData("pegasus_soul", Rarity.VERY_RARE, 5, true, true, 18, 2, 60), EnchantmentCategory.ARMOR_CHEST);
		setCategory("utils");
		addStats(SPEED);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof HorseArmorItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return EnchantmentCategory.ARMOR_CHEST.canEnchant(stack.getItem());
	}
}
