package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class Ecological extends UniqueEnchantment
{
	public static final TagKey<Block> ECOLOGICAL = BlockTags.create(new ResourceLocation("uniquee", "ecological"));
	public static ToIntFunction<BlockState> STATES = new ToIntFunction<BlockState>() {
		@Override
		public int applyAsInt(BlockState t)
		{
			return t.is(BlockTags.LOGS) || t.is(BlockTags.LEAVES) || t.is(ECOLOGICAL) ? 1 : 0;
		}
	};
	public static final IntStat SPEED = new IntStat(330, "baseDuration");
	public static final DoubleStat SPEED_SCALE = new DoubleStat(16D, "scalingReduction");
	
	public Ecological()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, false, true, 20, 8, 10), EnchantmentCategory.BREAKABLE, EquipmentSlot.values());
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