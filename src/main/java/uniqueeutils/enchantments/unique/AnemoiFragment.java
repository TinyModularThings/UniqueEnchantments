package uniqueeutils.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class AnemoiFragment extends UniqueEnchantment
{
	public static final String STORAGE = "fuel_storage";
	public static final DoubleStat BOOST = new DoubleStat(1D, "boost");
	public static final IntStat CONSUMPTION = new IntStat(10, "base_consumption");
	public static final DoubleStat CONSUMPTION_SCALE = new DoubleStat(1D, "consumption_anti_scale");
	public static final ToIntFunction<ItemStack> FUEL_SOURCE = ForgeHooks::getBurnTime;
	
	public AnemoiFragment()
	{
		super(new DefaultData("anemoi_fragment", Rarity.VERY_RARE, 4, true, true, 40, 10, 75), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		addStats(BOOST, CONSUMPTION, CONSUMPTION_SCALE);
		setCategory("utils");
		setDisableDefaultItems();
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "icarus_aegis"));;
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ElytraItem;
	}
}