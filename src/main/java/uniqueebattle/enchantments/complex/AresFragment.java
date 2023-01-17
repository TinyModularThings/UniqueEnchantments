package uniqueebattle.enchantments.complex;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;

public class AresFragment extends UniqueEnchantment
{
	public static final DoubleStat ARMOR_PERCENTAGE = new DoubleStat(0.6D, "armor_percentage");
	public static final IntStat BASE_ROLL = new IntStat(10, "base_roll");
	public static final DoubleStat BASE_ROLL_MULTIPLIER = new DoubleStat(1D, "base_roll_multiplier");
	public static final DoubleStat DURABILITY_DISTRIBUTION = new DoubleStat(0.6D, "durability_distribution");
	public static final DoubleStat DURABILITY_REDUCTION_SCALING = new DoubleStat(1.3D, "durability_reduction_scaling");
	public static final IntStat DURABILITY_ANTI_SCALING = new IntStat(11, "durability_anti_scaling");
	public static final DoubleStat TRANSCENDED_CRIT_MULTIPLIER = new DoubleStat(1.5, "transcended_crit_damage_multiplier");
	
	public AresFragment()
	{
		super(new DefaultData("ares_fragment", Rarity.RARE, 4, true, false, 10, 35, 10).setTrancendenceLevel(200), EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND);
		setCategory("battle");
		addStats(ARMOR_PERCENTAGE, BASE_ROLL, BASE_ROLL_MULTIPLIER, DURABILITY_REDUCTION_SCALING, DURABILITY_ANTI_SCALING, DURABILITY_DISTRIBUTION, TRANSCENDED_CRIT_MULTIPLIER);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "alchemists_grace"), new ResourceLocation("uniquee", "ender_mending"));
		addIncompats(Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || stack.getItem() instanceof TridentItem;
	}
	
}
