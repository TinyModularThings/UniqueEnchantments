package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;

public class IronBird extends UniqueEnchantment
{
	public static final UUID DAMAGE_MOD = UUID.fromString("4078263f-7bc9-471b-a7c2-9c0f44f249d1");
	public static final UUID TOUGHNESS_MOD = UUID.fromString("082819f1-fa1d-4ece-94c2-b376b0499dd8");
	
	public static final DoubleLevelStats ARMOR = new DoubleLevelStats("armor", 3D, 1D);
	public static final DoubleStat TOUGHNESS = new DoubleStat(1D, "toughness");
	
	public IronBird()
	{
		super(new DefaultData("iron_bird", Rarity.VERY_RARE, 4, false, false, 10, 5, 75), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		addStats(ARMOR, TOUGHNESS);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ElytraItem;
	}
}
