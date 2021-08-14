package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;

public class AresFragment extends UniqueEnchantment
{
	public static final DoubleStat ARMOR_PERCENTAGE = new DoubleStat(0.6D, "armor_percentage");
	public static final DoubleStat BASE_CHANCE = new DoubleStat(0.5D, "base_chance");
	public static final DoubleStat CHANCE_MULT = new DoubleStat(3.258D, "chance_multiplier");
	public static final DoubleStat DURABILITY_SCALING = new DoubleStat(1D, "durability_scaling");
	
	public AresFragment()
	{
		super(new DefaultData("ares_fragment", Rarity.RARE, 4, true, false, 25, 35, 10), EnchantmentType.WEAPON, EquipmentSlotType.MAINHAND);
		addStats(ARMOR_PERCENTAGE, BASE_CHANCE, CHANCE_MULT, DURABILITY_SCALING);
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "berserk"), new ResourceLocation("uniquee", "alchemistsgrace"), new ResourceLocation("uniquee", "ender_mending"));
		addIncompats(Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
}
