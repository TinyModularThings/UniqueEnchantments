package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntStat;

public class EnchantmentIcarusAegis extends UniqueEnchantment
{
	public static final IntStat SCALAR = new IntStat(24, "scalar");
	public static String FEATHER_TAG = "feathers";
	public static String FLYING_TAG = "flying";
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return value.getItem() == Items.FEATHER ? 1 : 0;
		}
	};
	
	public EnchantmentIcarusAegis()
	{
		super(new DefaultData("icarus_aegis", Rarity.VERY_RARE, 2, true, 20, 2, 20), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
		
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemElytra;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SCALAR.handleConfig(config, getConfigName());
	}
	
}
