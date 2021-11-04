package uniqueeutils.enchantments.unique;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniquebase.UniqueEnchantmentsBase;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.StackUtils;
import uniqueeutils.UniqueEnchantmentsUtils;
import uniqueeutils.misc.HighlightPacket;

public class Resonance extends UniqueEnchantment
{
	private static final Map<World, List<ResonanceInstance>> INSTANCES = new Object2ObjectLinkedOpenHashMap<>();
	public static final String COOLDOWN_TAG = "resonance_cooldown";
	public static final String OVERUSED_TAG = "overused";
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 0.6D);
	public static final DoubleStat BASE_DAMAGE = new DoubleStat(1D, "base_damage");
	public static final IntStat COOLDOWN = new IntStat(400, "cooldown");
	
	public Resonance()
	{
		super(new DefaultData("resonance", Rarity.UNCOMMON, 4, false, 10, 4, 75), EnumEnchantmentType.DIGGER, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
		addStats(RANGE, BASE_DAMAGE, COOLDOWN);
		setCategory("utils");
	}
	
	public static void addResonance(World world, BlockPos pos, int radius)
	{
		INSTANCES.computeIfAbsent(world, T -> new ObjectArrayList<>()).add(new ResonanceInstance(pos, radius));
	}
	
	public static void tick(World world)
	{
		List<ResonanceInstance> ticks = INSTANCES.get(world);
		if(ticks == null) return;
		for(int i = 0,m=ticks.size();i<m;i++)
		{
			if(ticks.get(i).tick(world)) continue;
			ticks.remove(i--);
			m--;
		}
		if(ticks.isEmpty()) INSTANCES.remove(world);
	}
	
	public static class ResonanceInstance
	{
		List<BlockPos> positionsToCheck = new ObjectArrayList<>();
		int currentIndex = 0;
		
		public ResonanceInstance(BlockPos start, int targetRadius)
		{
			add(start);
	        int radius = 1;
	        while (radius <= targetRadius)
	        {
	            for (int q = -radius + 1; q <= radius; q++) {
	            	add(start.add(radius, 0, q));
	            }
	            for (int q = radius - 1; q >= -radius; q--) {
	            	add(start.add(q, 0, radius));
	            }
	            for (int q = radius - 1; q >= -radius; q--) {
	            	add(start.add(-radius, 0, q));
	            }
	            for (int q = -radius + 1; q <= radius; q++) {
	            	add(start.add(q, 0, -radius));
	            }
	            radius++;
	        }
		}
		
		private void add(BlockPos pos)
		{
			positionsToCheck.add(pos.down());
			positionsToCheck.add(pos);
			positionsToCheck.add(pos.up());
			positionsToCheck.add(pos.up(2));
		}
		
		public boolean tick(World world)
		{
			for(int i = 0;i < 4 && currentIndex < positionsToCheck.size();i++)
			{
				BlockPos pos = positionsToCheck.get(currentIndex);
				if(StackUtils.isOre(world.getBlockState(pos)))
				{
					world.playSound(null, pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D, UniqueEnchantmentsUtils.RESONANCE_SOUND, SoundCategory.BLOCKS, 1F, 1F);
					UniqueEnchantmentsBase.NETWORKING.sendToChunk(new HighlightPacket(pos), world.getChunk(pos));
				}
				currentIndex++;
			}
			return currentIndex < positionsToCheck.size();
		}
	}
}
