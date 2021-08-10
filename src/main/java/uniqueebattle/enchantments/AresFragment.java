package uniqueebattle.enchantments;

import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class AresFragment extends UniqueEnchantment
{
	public static DoubleStat ARMOR_PERCENTAGE = new DoubleStat(0.6D, "armor_percentage");
	public static DoubleStat BASE_CHANCE = new DoubleStat(0.5D, "base_chance");
	public static DoubleStat CHANCE_MULT = new DoubleStat(3.258D, "chance_multiplier");
	
	public AresFragment()
	{
		super(new DefaultData("ares_fragment", Rarity.RARE, 4, true, 25, 15, 10), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
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
		return stack.getItem() instanceof BowItem || stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem;
	}
	
	@Override
	public void loadData(ForgeConfigSpec.Builder config)
	{
		ARMOR_PERCENTAGE.handleConfig(config);
		BASE_CHANCE.handleConfig(config);
		CHANCE_MULT.handleConfig(config);
	}
	
}
