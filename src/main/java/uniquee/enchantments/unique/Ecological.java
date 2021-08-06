package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquee.UniqueEnchantments;

public class Ecological extends UniqueEnchantment
{
	public static Predicate<IBlockState> STATES = new Predicate<IBlockState>(){
		@Override
		public boolean test(IBlockState t)
		{
			return t.getBlock() instanceof BlockLog || t.getBlock() instanceof BlockLeaves;
		}
	};
	public static final IntStat SPEED = new IntStat(220, "speed");
	public static final DoubleStat SPEED_SCALE = new DoubleStat(1.85D, "speed_scale");
	
	public Ecological()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, true, 4, 8, 10), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
		addStats(SPEED, SPEED_SCALE);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.ENDERMARKSMEN, Enchantments.MENDING, Enchantments.INFINITY);
	}
}