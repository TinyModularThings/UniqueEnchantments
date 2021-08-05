package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.DoubleLevelStats;
import uniquee.utils.IntStat;

public class EnchantmentNaturesGrace extends UniqueEnchantment implements IGraceEnchantment
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
	public EnchantmentNaturesGrace()
	{
		super(new DefaultData("naturesgrace", Rarity.RARE, 2, true, 10, 6, 10), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.ARES_BLESSING, UniqueEnchantments.ALCHEMISTS_GRACE, UniqueEnchantments.WARRIORS_GRACE);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		HEALING.handleConfig(config, getConfigName());
		DELAY.handleConfig(config, getConfigName());
	}
}
