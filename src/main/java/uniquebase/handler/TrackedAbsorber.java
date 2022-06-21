package uniquebase.handler;

import java.util.List;
import java.util.Map;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniquebase.api.EnchantedUpgrade;

public class TrackedAbsorber
{
	ItemFrameEntity frame;
	World world;
	BlockPos absorbPos;
	List<EnchantedUpgrade> supportedUpgrade;
	ItemStack myItem;
	
	public TrackedAbsorber(ItemFrameEntity frame, World world, BlockPos absorbPos, List<EnchantedUpgrade> supportedUpgrade)
	{
		this.frame = frame;
		this.world = world;
		this.absorbPos = absorbPos;
		this.supportedUpgrade = supportedUpgrade;
		myItem = frame.getItem();
	}

	public boolean process()
	{
		if(!world.isLoaded(absorbPos) || !frame.isAlive() || frame.getItem() != myItem) return true;
		if(world.getBlockState(absorbPos).getBlock() != Blocks.ENCHANTING_TABLE) return true;
		for(ItemEntity item : world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(absorbPos)))
		{
			ItemStack stack = item.getItem();
			Map<Enchantment, Integer> levels = EnchantmentHelper.getEnchantments(stack);
			boolean remove = false;
			for(EnchantedUpgrade entry : supportedUpgrade)
			{
				Integer level = levels.get(entry.getSource());
				if(level != null && level > 0)
				{
					entry.storePoints(myItem, (int)Math.pow(level, 2));
					remove = true;
				}
			}
			if(remove) item.remove();
		}
		return false;
	}
}
