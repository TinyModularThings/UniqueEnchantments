package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquee.UniqueEnchantments;

public class Ecological extends UniqueEnchantment
{
	public static final Tag<Block> ECHOLOGICAL = new BlockTags.Wrapper(new ResourceLocation("uniquee", "ecological"));
	public static Predicate<BlockState> STATES = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return BlockTags.LOGS.contains(t.getBlock()) || BlockTags.LEAVES.contains(t.getBlock()) || ECHOLOGICAL.contains(t.getBlock());
		}
	};
	public static final IntStat SPEED = new IntStat(220, "baseDuration");
	public static final DoubleStat SPEED_SCALE = new DoubleStat(16D, "scalingReduction");
	
	public Ecological()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, true, 4, 8, 10), EnchantmentType.BREAKABLE, EquipmentSlotType.values());
		addStats(SPEED, SPEED_SCALE);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ENDERMARKSMEN, Enchantments.MENDING, Enchantments.INFINITY);
	}
}