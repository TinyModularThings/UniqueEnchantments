package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class IcarusAegis extends UniqueEnchantment
{
	public static final DoubleStat BASE_CONSUMPTION = new DoubleStat(24D, "base_consumtion");
	public static String FEATHER_TAG = "feathers";
	public static String FLYING_TAG = "flying";
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return value.getItem() == Items.FEATHER ? 1 : 0;
		}
	};
	
	public IcarusAegis()
	{
		super(new DefaultData("icarus_aegis", Rarity.VERY_RARE, 3, true, 16, 4, 10), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		addStats(BASE_CONSUMPTION);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemElytra;
	}
}