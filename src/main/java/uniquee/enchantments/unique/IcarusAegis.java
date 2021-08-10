package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntStat;

public class IcarusAegis extends UniqueEnchantment
{
	public static IntStat SCALAR = new IntStat(24, "scalar");
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
		super(new DefaultData("icarus_aegis", Rarity.VERY_RARE, 2, true, 20, 2, 20), EnchantmentType.ARMOR_CHEST, new EquipmentSlotType[]{EquipmentSlotType.CHEST});
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled.get() && stack.getItem() instanceof ElytraItem;
	}
	
	@Override
	public void loadData(Builder config)
	{
		SCALAR.handleConfig(config);
	}
}
