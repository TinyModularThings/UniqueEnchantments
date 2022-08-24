package uniqueeutils.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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
	public static final ToIntFunction<ItemStack> FUEL_SOURCE = T -> ForgeHooks.getBurnTime(T, RecipeType.SMELTING);
	
	public AnemoiFragment()
	{
		super(new DefaultData("anemoi_fragment", Rarity.VERY_RARE, 4, true, true, 40, 10, 75), EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);
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