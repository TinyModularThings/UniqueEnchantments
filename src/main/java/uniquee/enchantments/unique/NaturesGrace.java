package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.block.BlockState;
import net.minecraft.block.DoublePlantBlock;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntStat;
import uniquee.UE;

public class NaturesGrace extends UniqueEnchantment
{
	public static final DoubleLevelStats HEALING = new DoubleLevelStats("healing", 0.6, 0.2);
	public static final IntStat DELAY = new IntStat(240, "delay");
	public static final ToIntFunction<BlockState> FLOWERS = new ToIntFunction<BlockState>() {
		@Override
		public int applyAsInt(BlockState value) {
			if(BlockTags.SMALL_FLOWERS.contains(value.getBlock())) return 3;
			if(BlockTags.LEAVES.contains(value.getBlock()) || value.getBlock() instanceof DoublePlantBlock) return 1;
			if(BlockTags.LOGS.contains(value.getBlock())) return 2;
			return 0;
		}
	};
	
	public NaturesGrace()
	{
		super(new DefaultData("natures_grace", Rarity.RARE, 2, true, false, 10, 16, 10), EnchantmentType.ARMOR_CHEST, EquipmentSlotType.CHEST);
		addStats(HEALING, DELAY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.ARES_BLESSING, UE.ALCHEMISTS_GRACE, UE.WARRIORS_GRACE);
	}
}