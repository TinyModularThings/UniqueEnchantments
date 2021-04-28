package uniqueeutils.enchantments;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.enchantments.UniqueEnchantment;

public class EnchantmentClimber extends UniqueEnchantment
{
	public static final String CLIMB_POS = "climb_target";
	public static final String CLIMB_DELAY = "climb_delay";
	public static final String CLIMB_START = "climb_start";
	static final Object2DoubleMap<Block> CLIMB_SPEED = new Object2DoubleOpenHashMap<>();
	static double MIN_WAITING_TIME = 0.5D;
	
	public EnchantmentClimber()
	{
		super(new DefaultData("climber", Rarity.COMMON, 3, true, 14, 8, 30), EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
	}
	
	@Override
	public void loadData(Configuration config)
	{
		CLIMB_SPEED.clear();
		CLIMB_SPEED.defaultReturnValue(config.get(getConfigName(), "climb_default", 0.25D).getDouble());
		MIN_WAITING_TIME = config.get(getConfigName(), "climb_default", 0.25D).getDouble();
		String[] blocks = config.get(getConfigName(), "climb_speed", new String[]{"minecraft:ladder;0.1", "minecraft:vine;0.25"}).getStringList();
		for(int i = 0;i<blocks.length;i++)
		{
			String[] split = blocks[i].split(";");
			if(split.length != 2) continue;
			try
			{
				Block block = Block.getBlockFromName(split[0]);
				if(block != null && block != Blocks.AIR) CLIMB_SPEED.put(block, Double.parseDouble(split[1]));
			}
			catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public static int getClimbTime(int level, List<Block> blocksToClimb)
	{
		double totalTime = 0D;
		for(Block block : blocksToClimb) totalTime += CLIMB_SPEED.getDouble(block);
		return Math.max((int)(((totalTime / level) + MIN_WAITING_TIME) * 20), 2);
	}
}