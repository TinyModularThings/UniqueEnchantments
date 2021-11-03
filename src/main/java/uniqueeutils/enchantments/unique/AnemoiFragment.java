package uniqueeutils.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class AnemoiFragment extends UniqueEnchantment
{
	public static final String STORAGE = "fuel_storage";
	public static final DoubleStat BOOST = new DoubleStat(1D, "boost");
	public static final IntStat CONSUMPTION = new IntStat(10, "base_consumption");
	public static final DoubleStat CONSUMPTION_SCALE = new DoubleStat(1D, "consumption_anti_scale");
	public static final ToIntFunction<ItemStack> FUEL_SOURCE = TileEntityFurnace::getItemBurnTime;
	
	public AnemoiFragment()
	{
		super(new DefaultData("anemoi_fragment", Rarity.VERY_RARE, 4, true, 40, 10, 75), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		addStats(BOOST, CONSUMPTION, CONSUMPTION_SCALE);
		setCategory("utils");
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack)
	{
		return enabled && stack.getItem() instanceof ItemElytra;
	}
}