package uniqueeutils.enchantments.unique;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import uniquebase.UEBase;
import uniquebase.api.UniqueEnchantment;
import uniquebase.utils.DoubleLevelStats;
import uniquebase.utils.DoubleStat;
import uniquebase.utils.IntStat;
import uniquebase.utils.StackUtils;
import uniqueeutils.UEUtils;
import uniqueeutils.misc.HighlightPacket;

public class Resonance extends UniqueEnchantment
{
	private static final Map<Level, List<ResonanceInstance>> INSTANCES = new Object2ObjectLinkedOpenHashMap<>();
	public static final String COOLDOWN_TAG = "resonance_cooldown";
	public static final String OVERUSED_TAG = "overused";
	public static final DoubleLevelStats RANGE = new DoubleLevelStats("range", 5D, 0.6D);
	public static final DoubleStat BASE_DAMAGE = new DoubleStat(1D, "base_damage");
	public static final IntStat COOLDOWN = new IntStat(400, "cooldown");
	public static final IntStat AMOUNT = new IntStat(6, "amount", "The amount of blocks that are checked per tick");
	
	public Resonance()
	{
		super(new DefaultData("resonance", Rarity.UNCOMMON, 4, false, false, 10, 4, 75), EnchantmentCategory.DIGGER, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
		addStats(RANGE, BASE_DAMAGE, COOLDOWN, AMOUNT);
		setCategory("utils");
	}
	
	public static void addResonance(Level world, BlockPos pos, int radius, Player player, InteractionHand hand, boolean trancended)
	{
		INSTANCES.computeIfAbsent(world, T -> new ObjectArrayList<>()).add(new ResonanceInstance(pos, radius, player, hand, trancended));
	}
	
	public static void tick(Level world)
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
		Player player;
		InteractionHand hand;
		ItemStack reference;
		boolean trancended;
		
		public ResonanceInstance(BlockPos start, int targetRadius, Player player, InteractionHand hand, boolean trancended)
		{
			this.player = player;
			this.hand = hand;
			this.reference = player.getItemInHand(hand);
			this.trancended = trancended;
			add(start);
	        int radius = 1;
	        while (radius <= targetRadius)
	        {
	            for (int q = -radius + 1; q <= radius; q++) {
	            	add(start.offset(radius, 0, q));
	            }
	            for (int q = radius - 1; q >= -radius; q--) {
	            	add(start.offset(q, 0, radius));
	            }
	            for (int q = radius - 1; q >= -radius; q--) {
	            	add(start.offset(-radius, 0, q));
	            }
	            for (int q = -radius + 1; q <= radius; q++) {
	            	add(start.offset(q, 0, -radius));
	            }
	            radius++;
	        }
		}
		
		private void add(BlockPos pos)
		{
			positionsToCheck.add(pos.below());
			positionsToCheck.add(pos);
			positionsToCheck.add(pos.above());
			positionsToCheck.add(pos.above(2));
		}
		
		public boolean tick(Level world)
		{
			if(!player.isAlive() || reference != player.getItemInHand(hand)) return false;
			int max = AMOUNT.get();
			for(int i = 0;i < max && currentIndex < positionsToCheck.size();i++)
			{
				BlockPos pos = positionsToCheck.get(currentIndex);
				BlockState state = world.getBlockState(pos);
				if(StackUtils.isOre(state))
				{
					world.playSound(null, pos.getX()+0.5D, pos.getY()+0.5D, pos.getZ()+0.5D, UEUtils.RESONANCE_SOUND, SoundSource.BLOCKS, 1F, 1F);
					UEBase.NETWORKING.sendToAllChunkWatchers((LevelChunk)world.getChunk(pos), new HighlightPacket(pos));
					if(trancended && world instanceof ServerLevel) {
						List<ItemStack> drops = Block.getDrops(state, (ServerLevel)world, pos, world.getBlockEntity(pos), player, reference);
						world.removeBlock(pos, false);
						StackUtils.addOrDrop(player, drops);
					}
				}
				currentIndex++;
			}
			return currentIndex < positionsToCheck.size();
		}
	}
}
