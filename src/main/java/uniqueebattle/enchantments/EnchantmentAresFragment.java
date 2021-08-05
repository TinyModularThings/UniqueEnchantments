package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class EnchantmentAresFragment extends UniqueEnchantment
{
	public static final DoubleStat ARMOR_PERCENTAGE = new DoubleStat(0.6D, "armor_percentage");
	public static final DoubleStat BASE_CHANCE = new DoubleStat(0.5D, "base_chance");
	public static final DoubleStat CHANCE_MULT = new DoubleStat(3.258D, "chance_multiplier");
	public static final DoubleStat DURABILITY_SCALING = new DoubleStat(1D, "durability_scaling");
	
	public EnchantmentAresFragment()
	{
		super(new DefaultData("ares_fragment", Rarity.RARE, 4, true, 25, 35, 10), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.BERSERKER, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.ENDER_MENDING, Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		ARMOR_PERCENTAGE.handleConfig(config, getConfigName());
		BASE_CHANCE.handleConfig(config, getConfigName());
		CHANCE_MULT.handleConfig(config, getConfigName());
		DURABILITY_SCALING.handleConfig(config, getConfigName());
	}
}