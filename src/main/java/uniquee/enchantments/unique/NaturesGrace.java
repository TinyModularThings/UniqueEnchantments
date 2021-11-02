package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
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
	public static Predicate<IBlockState> FLOWERS = new Predicate<IBlockState>(){
		@Override
		public boolean test(IBlockState t)
		{
			return t.getBlock() instanceof BlockFlower || t.getBlock() instanceof BlockLeaves;
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