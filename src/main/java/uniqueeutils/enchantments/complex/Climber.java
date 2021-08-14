package uniqueeutils.enchantments.complex;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.IntStat;

public class Climber extends UniqueEnchantment
{
	public static final String CLIMB_POS = "climb_target";
	public static final String CLIMB_DELAY = "climb_delay";
	public static final String CLIMB_START = "climb_start";
	static final Object2IntMap<Block> CLIMB_SPEED = new Object2IntOpenHashMap<>();
	public static final IntStat DELAY = new IntStat(10, "min_delay");
	ConfigValue<List<? extends String>> values;
	
	public Climber()
	{
		super(new DefaultData("climber", Rarity.UNCOMMON, 3, true, true, 14, 8, 30), EnchantmentType.ARMOR_LEGS, EquipmentSlotType.LEGS);
		setCategory("utils");
		addStats(DELAY);
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(new ResourceLocation("uniquee", "swift"));
	}
	
	@Override
	public void loadData(Builder config)
	{
		values = config.defineList("climb_speed", ObjectArrayList.wrap(new String[]{"minecraft:ladder;2", "minecraft:vine;5", "minecraft:scaffolding;5"}), T -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		super.onConfigChanged();
		CLIMB_SPEED.clear();		
		List<? extends String> list = values.get();
		for(int i = 0;i<list.size();i++)
		{
			String[] split = list.get(i).split(";");
			if(split.length != 2) continue;
			try
			{
				Block block = ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryCreate(split[0]));
				if(block != null && block != Blocks.AIR) CLIMB_SPEED.put(block, Integer.parseInt(split[1]));
			}
			catch(Exception e) { e.printStackTrace(); }
		}
	}
	
	public static int getClimbTime(int level, List<Block> blocksToClimb)
	{
		int totalTime = 0;
		for(Block block : blocksToClimb) totalTime += CLIMB_SPEED.getInt(block);
		return Math.max((int)(((totalTime / level) + DELAY.get())), 2);
	}
}