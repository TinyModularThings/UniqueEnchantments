package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IGraceEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntStat;
import uniquee.UniqueEnchantments;

public class NaturesGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static final DoubleLevelStats HEALING = new DoubleLevelStats("healing", 0.6, 0.2);
	public static final IntStat DELAY = new IntStat(240, "delay");
	public static final Predicate<BlockState> FLOWERS = new Predicate<BlockState>(){
		@Override
		public boolean test(BlockState t)
		{
			return BlockTags.SMALL_FLOWERS.contains(t.getBlock()) || t.getBlock() instanceof DoublePlantBlock || BlockTags.LEAVES.contains(t.getBlock());
		}
	};	
	public NaturesGrace()
	{
		super(new DefaultData("naturesgrace", Rarity.RARE, 2, true, false, 10, 6, 10), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		addStats(HEALING, DELAY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ARES_BLESSING, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.WARRIORS_GRACE);
	}
}