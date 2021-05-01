package uniqueeutils.enchantments;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.utils.DoubleStat;

public class ClimberEnchantment extends UniqueEnchantment
{
	public static final String CLIMB_POS = "climb_target";
	public static final String CLIMB_DELAY = "climb_delay";
	public static final String CLIMB_START = "climb_start";
	static final Object2DoubleMap<Block> CLIMB_SPEED = new Object2DoubleOpenHashMap<>();
	static final DoubleStat DEFAULT_CLIMB = new DoubleStat(0.25D, "climb_default");
	static final DoubleStat MIN_WAIT_TIME = new DoubleStat(0.5D, "climb_min_waiting");
	ConfigValue<List<? extends String>> values;
	static double MIN_WAITING_TIME = 0.5D;

	public ClimberEnchantment()
	{
		super(new DefaultData("climber", Rarity.COMMON, 3, true, 14, 8, 30), EnchantmentType.ARMOR_LEGS, new EquipmentSlotType[]{EquipmentSlotType.LEGS});
		setCategory("utils");
	}
	
	@Override
	public void loadIncompats()
	{
		addIncomats(UniqueEnchantments.SWIFT);
	}
	
	@Override
	public void loadData(Builder config)
	{
		DEFAULT_CLIMB.handleConfig(config);
		MIN_WAIT_TIME.handleConfig(config);
		values = config.defineList("climb_speed", ObjectArrayList.wrap(new String[]{"minecraft:ladder;0.1", "minecraft:vine;0.25", "minecraft:scaffolding;0.25"}), T -> true);
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