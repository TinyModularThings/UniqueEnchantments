package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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
		super(new DefaultData("icarus_aegis", Rarity.VERY_RARE, 3, true, false, 16, 4, 10), EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);
		addStats(BASE_CONSUMPTION);
		setDisableDefaultItems();
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ElytraItem;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack) {
		return !stack.isDamageableItem();
	}
}
