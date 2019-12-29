package uniquee.enchantments.complex;

import java.util.Set;
import java.util.function.Predicate;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntLevelStats;

public class EnchantmentSmartAss extends UniqueEnchantment
{
	public static final IntLevelStats STATS = new IntLevelStats("range", 2, 3);
	static final Set<Block> BLOCKS = new ObjectOpenHashSet();
	public static final Predicate<IBlockState> VALID_STATES = new Predicate<IBlockState>(){
		@Override
		public boolean test(IBlockState t)
		{
			Block block = t.getBlock();
			return block instanceof BlockLog || block instanceof BlockFalling || BLOCKS.contains(block);
		}
	};
	
	public EnchantmentSmartAss()
	{
		super(new DefaultData("smart_ass", Rarity.RARE, false, 28, 6, 40), EnumEnchantmentType.DIGGER, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
	}
	
	@Override
	public int getMaxLevel()
	{
		return 3;
	}
	
	@Override
	public void loadData(Configuration config)
	{
		BLOCKS.clear();
		STATS.handleConfig(config, getConfigName());
		String[] list = config.get(getConfigName(), "valid_blocks", new String[0], "Valid Blocks that use that feature. Registry names. Example: minecraft:dirt, minecraft:stone").getStringList();
		for(int i = 0;i<list.length;i++)
		{
			Block block = Block.getBlockFromName(list[i]);
			if(block != null && block != Blocks.AIR)
			{
				BLOCKS.add(block);
			}
		}
	}
}
