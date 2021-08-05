package uniqueeutils.enchantments;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Configuration;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.IntStat;

public class EnchantmentClimber extends UniqueEnchantment
{
	public static final String CLIMB_POS = "climb_target";
	public static final String CLIMB_DELAY = "climb_delay";
	public static final String CLIMB_START = "climb_start";
	static final Object2IntMap<Block> CLIMB_SPEED = new Object2IntOpenHashMap<>();
	public static final IntStat DELAY = new IntStat(10, "min_delay");
	
	public EnchantmentClimber()
	{
		super(new DefaultData("climber", Rarity.UNCOMMON, 3, true, 14, 8, 30), EnumEnchantmentType.ARMOR_LEGS, new EntityEquipmentSlot[]{EntityEquipmentSlot.LEGS});
		setCategory("utils");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.SWIFT);
	}
	
	@Override
	public void loadData(Configuration config)
	{
		CLIMB_SPEED.clear();
		CLIMB_SPEED.defaultReturnValue(config.get(getConfigName(), "default_delay", 5).getInt());
		DELAY.handleConfig(config, getConfigName());
		String[] blocks = config.get(getConfigName(), "climb_speed", new String[]{"minecraft:ladder;2", "minecraft:vine;5"}).getStringList();
		for(int i = 0;i<blocks.length;i++)
		{
			String[] split = blocks[i].split(";");
			if(split.length != 2) continue;
			try
			{
				Block block = Block.getBlockFromName(split[0]);
				if(block != null && block != Blocks.AIR) CLIMB_SPEED.put(block, Integer.parseInt(split[1]));
			}
			catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public static int getClimbTime(int level, List<Block> blocksToClimb)
	{
		double totalTime = 0D;
		for(Block block : blocksToClimb) totalTime += CLIMB_SPEED.getInt(block);
		return Math.max((int)(((totalTime / level) + DELAY.get())), 2);
	}
}