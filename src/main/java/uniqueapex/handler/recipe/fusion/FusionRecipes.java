package uniqueapex.handler.recipe.fusion;

import java.util.List;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class FusionRecipes
{
	public static class AverageFusionRecipe extends FusionRecipe
	{
		public AverageFusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
		{
			super(id, requestedItems, enchantments, lock, max, output);
		}
		
		@Override
		protected int calculateLevel(Object2IntMap<Enchantment> list)
		{
			int total = 0;
			for(IntIterator iter = list.values().iterator();iter.hasNext();total+=iter.nextInt());
			return total / list.size();
		}

		@Override
		public void writePacket(PacketBuffer buffer)
		{
			buffer.writeVarInt(0);
			super.writePacket(buffer);
		}
	}
	
	public static class MinFusionRecipe extends FusionRecipe
	{
		public MinFusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
		{
			super(id, requestedItems, enchantments, lock, max, output);
		}
		
		@Override
		protected int calculateLevel(Object2IntMap<Enchantment> list)
		{
			int total = Integer.MAX_VALUE;
			for(IntIterator iter = list.values().iterator();iter.hasNext();total = Math.min(total, iter.nextInt()));
			return total;
		}
		
		@Override
		public void writePacket(PacketBuffer buffer)
		{
			buffer.writeVarInt(1);
			super.writePacket(buffer);
		}
	}
	
	public static class MaxFusionRecipe extends FusionRecipe
	{
		public MaxFusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
		{
			super(id, requestedItems, enchantments, lock, max, output);
		}
		
		@Override
		protected int calculateLevel(Object2IntMap<Enchantment> list)
		{
			int total = 0;
			for(IntIterator iter = list.values().iterator();iter.hasNext();total = Math.max(total, iter.nextInt()));
			return total;
		}
		
		@Override
		public void writePacket(PacketBuffer buffer)
		{
			buffer.writeVarInt(2);
			super.writePacket(buffer);
		}
	}
	
	public static class SumFusionRecipe extends FusionRecipe
	{
		public SumFusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
		{
			super(id, requestedItems, enchantments, lock, max, output);
		}
		
		@Override
		protected int calculateLevel(Object2IntMap<Enchantment> list)
		{
			int total = 0;
			for(IntIterator iter = list.values().iterator();iter.hasNext();total+=iter.nextInt());
			return total;
		}
		
		@Override
		public void writePacket(PacketBuffer buffer)
		{
			buffer.writeVarInt(3);
			super.writePacket(buffer);
		}
	}
	
	public static class MulFusionRecipe extends FusionRecipe
	{
		public MulFusionRecipe(ResourceLocation id, Object2IntMap<Ingredient> requestedItems, List<Entry<Enchantment>> enchantments, boolean lock, int max, Enchantment output)
		{
			super(id, requestedItems, enchantments, lock, max, output);
		}
		
		@Override
		protected int calculateLevel(Object2IntMap<Enchantment> list)
		{
			int total = 1;
			for(IntIterator iter = list.values().iterator();iter.hasNext();total*=iter.nextInt());
			return total;
		}
		
		@Override
		public void writePacket(PacketBuffer buffer)
		{
			buffer.writeVarInt(4);
			super.writePacket(buffer);
		}
	}
}
