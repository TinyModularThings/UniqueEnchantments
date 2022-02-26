package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class IcarusAegis extends UniqueEnchantment
{
	public static final DoubleStat BASE_CONSUMPTION = new DoubleStat(24D, "base_consumtion");
	public static final String FEATHER_TAG = "feathers";
	public static final String FLYING_TAG = "flying";
	public static final ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return value.getItem() == Items.FEATHER ? 1 : 0;
		}
	};
	
	public IcarusAegis()
	{
		super(new DefaultData("icarus_aegis", Rarity.VERY_RARE, 3, true, 16, 4, 10), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		addStats(BASE_CONSUMPTION);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ElytraItem;
	}
}
