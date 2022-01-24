package uniqueebattle.enchantments;

import java.util.UUID;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
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
		super(new DefaultData("iron_bird", Rarity.VERY_RARE, 4, false, 10, 5, 75), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		addStats(ARMOR, TOUGHNESS);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemElytra;
	}
}
