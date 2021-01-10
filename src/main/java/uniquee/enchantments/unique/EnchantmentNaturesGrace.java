package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.DoubleLevelStats;

public class EnchantmentNaturesGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static final DoubleLevelStats HEALING = new DoubleLevelStats("healing", 0.6, 0.2);
	public static int DELAY = 100;
	public static Predicate<IBlockState> FLOWERS = new Predicate<IBlockState>(){
		@Override
		public boolean test(IBlockState t)
		{
			return t.getBlock() instanceof BlockFlower || t.getBlock() instanceof BlockLeaves;
		}
	};	
	public EnchantmentNaturesGrace()
	{
		super(new DefaultData("naturesgrace", Rarity.VERY_RARE, 2, true, 22, 4, 22), EnumEnchantmentType.ARMOR_CHEST, new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST});
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentAresBlessing || ench instanceof EnchantmentAlchemistsGrace || ench instanceof EnchantmentWarriorsGrace ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		HEALING.handleConfig(config, getConfigName());
		DELAY = config.get(getConfigName(), "delay", 100).getInt();
	}
}
