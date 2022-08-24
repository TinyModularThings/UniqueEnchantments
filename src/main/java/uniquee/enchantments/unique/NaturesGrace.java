package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
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
			if(value.is(BlockTags.SMALL_FLOWERS)) return 3;
			if(value.is(BlockTags.LEAVES) || value.getBlock() instanceof DoublePlantBlock) return 1;
			if(value.is(BlockTags.LOGS)) return 2;
			return 0;
		}
	};
	
	public NaturesGrace()
	{
		super(new DefaultData("natures_grace", Rarity.RARE, 2, true, false, 10, 16, 10), EnchantmentCategory.ARMOR_CHEST, EquipmentSlot.CHEST);
		addStats(HEALING, DELAY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UE.ARES_BLESSING, UE.ALCHEMISTS_GRACE, UE.WARRIORS_GRACE);
	}
}