package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.LootBonusEnchantment;
import net.minecraft.enchantment.SilkTouchEnchantment;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.Tags;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IBlessingEnchantment;
import uniquee.utils.IntStat;

public class EnchantmentMidasBlessing extends UniqueEnchantment implements IBlessingEnchantment
{
	public static ToIntFunction<ItemStack> VALIDATOR = new ToIntFunction<ItemStack>(){
		@Override
		public int applyAsInt(ItemStack value)
		{
			return Tags.Items.INGOTS_GOLD.contains(value.getItem()) ? 1 : 0;
		}
	};
	public static String GOLD_COUNTER = "gold_storage";
	public static IntStat LEVEL_SCALAR = new IntStat(6, "level_scalar");
	public static IntStat BASE_COST = new IntStat(2, "base_cost");
	
	public EnchantmentMidasBlessing()
	{
		super(new DefaultData("midas_blessing", Rarity.VERY_RARE, true, 22, 2, 75), EnchantmentType.DIGGER, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof LootBonusEnchantment || ench instanceof SilkTouchEnchantment || ench instanceof EnchantmentIfritsGrace ? false : super.canApplyTogether(ench);
	}

	@Override
	public void loadData(Builder config)
	{
		LEVEL_SCALAR.handleConfig(config);
		BASE_COST.handleConfig(config);
	}
	
	
}
