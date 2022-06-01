package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag.INamedTag;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class Ecological extends UniqueEnchantment
{
	public static final INamedTag<Block> ECOLOGICAL = BlockTags.createOptional(new ResourceLocation("uniquee", "ecological"));
	public static ToIntFunction<BlockState> STATES = new ToIntFunction<BlockState>() {
		@Override
		public int applyAsInt(BlockState t)
		{
			return BlockTags.LOGS.contains(t.getBlock()) || BlockTags.LEAVES.contains(t.getBlock()) || ECOLOGICAL.contains(t.getBlock()) ? 1 : 0;
		}
	};
	public static final IntStat SPEED = new IntStat(330, "baseDuration");
	public static final DoubleStat SPEED_SCALE = new DoubleStat(16D, "scalingReduction");
	
	public Ecological()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, false, true, 20, 8, 10), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(SPEED, SPEED_SCALE);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof CrossbowItem;
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.WARRIORS_GRACE, UE.ENDERMARKSMEN, Enchantments.MENDING, Enchantments.INFINITY_ARROWS);
	}
}