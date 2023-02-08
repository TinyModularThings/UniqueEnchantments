package uniqueapex.enchantments.simple;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import uniqueapex.UEApex;
import uniqueapex.enchantments.ApexEnchantment;
import uniquebase.utils.IdStat;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;

public class Pickaxe404 extends ApexEnchantment
{
	private static final String DAMAGE = "404Damage";
	public static final IdStat<Block> BLACK_LIST = new IdStat<>("blacklist", ForgeRegistries.BLOCKS);
	
	public Pickaxe404()
	{
		super("pickaxe404", EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND);
		addStats(BLACK_LIST);
		setCategory("apex");
	}
	
	@Override
	protected boolean canNotApplyToItems(ItemStack stack)
	{
		return !(stack.getItem() instanceof PickaxeItem);
	}
	
	public static void increaseUsage(ItemStack stack, int level) {
		int current = StackUtils.getInt(stack, DAMAGE, 0)+1;
		if(current >= level) {
			MiscUtil.decreaseEnchantmentLevel(UEApex.PICKAXE_404, stack);
			current = 0;
		}
		StackUtils.setInt(stack, DAMAGE, current);
	}
}
