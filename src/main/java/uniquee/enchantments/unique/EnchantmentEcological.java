package uniquee.enchantments.unique;

import java.util.function.Predicate;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentArrowInfinite;
import net.minecraft.enchantment.EnchantmentMending;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentEcological extends UniqueEnchantment
{
	public static Predicate<IBlockState> STATES = new Predicate<IBlockState>(){
		@Override
		public boolean test(IBlockState t)
		{
			return t.getBlock() instanceof BlockLog || t.getBlock() instanceof BlockLeaves;
		}
	};
	public static int SPEED = 220;
	public static double SCALE = 1.85D;
	
	public EnchantmentEcological()
	{
		super(new DefaultData("ecological", Rarity.RARE, 3, true, 22, 2, 40), EnumEnchantmentType.BREAKABLE, EntityEquipmentSlot.values());
	}
	
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof EnchantmentWarriorsGrace || ench instanceof EnchantmentMending || ench instanceof EnchantmentEnderMarksmen || ench instanceof EnchantmentArrowInfinite ? false : super.canApplyTogether(ench);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		SPEED = config.get(getConfigName(), "speed", 220).getInt();
		SCALE = config.get(getConfigName(), "scale", 1.85D).getDouble();
	}
	
}
