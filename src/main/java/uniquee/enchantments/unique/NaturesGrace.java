package uniquee.enchantments.unique;

import java.util.function.ToIntFunction;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.IntStat;
import uniquee.UniqueEnchantments;

public class NaturesGrace extends UniqueEnchantment
{
	public static final DoubleLevelStats HEALING = new DoubleLevelStats("healing", 0.6, 0.2);
	public static final IntStat DELAY = new IntStat(240, "delay");
	public static final ToIntFunction<IBlockState> FLOWERS = new ToIntFunction<IBlockState>() {
		@Override
		public int applyAsInt(IBlockState value) {
			if(value.getBlock() instanceof BlockFlower) return 3;
			if(value.getBlock() instanceof BlockLeaves) return 1;
			if(value.getBlock() instanceof BlockLog) return 2;
			return 0;
		}
	};
	
	public NaturesGrace()
	{
		super(new DefaultData("naturesgrace", Rarity.RARE, 2, true, 10, 16, 10), EnumEnchantmentType.ARMOR_CHEST, EntityEquipmentSlot.CHEST);
		addStats(HEALING, DELAY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ARES_BLESSING, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.WARRIORS_GRACE);
	}
}