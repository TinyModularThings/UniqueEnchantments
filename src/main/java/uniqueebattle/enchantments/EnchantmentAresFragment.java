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

public class EnchantmentAresFragment extends UniqueEnchantment
{
	public static double ARMOR_PERCENTAGE = 0.6D;
	public static double BASE_CHANCE = 0.5D;
	public static double CHANCE_MULT = 3.258D;
	
	public EnchantmentAresFragment()
	{
		super(new DefaultData("ares_fragment", Rarity.RARE, 4, true, 25, 15, 10), EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
		setCategory("battle");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.BERSERKER, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.ENDER_MENDING, Enchantments.MENDING);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemBow || stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		ARMOR_PERCENTAGE = config.get(getConfigName(), "armor_percentage", 0.6D).getDouble();
		BASE_CHANCE = config.get(getConfigName(), "base_chance", 0.5D).getDouble();
		CHANCE_MULT = config.get(getConfigName(), "chance_multiplier", 3.258D).getDouble();
	}
}