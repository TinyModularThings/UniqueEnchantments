package uniquebase.handler;

import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import uniquebase.api.EnchantedUpgrade;

public class TrackedAbsorber
{
	ItemFrame frame;
	Level world;
	BlockPos absorbPos;
	List<EnchantedUpgrade> supportedUpgrade;
	ItemStack myItem;
	
	public TrackedAbsorber(ItemFrame frame, Level world, BlockPos absorbPos, List<EnchantedUpgrade> supportedUpgrade)
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
		for(ItemEntity item : world.getEntitiesOfClass(ItemEntity.class, new AABB(absorbPos)))
		{
			ItemStack stack = item.getItem();
			Map<Enchantment, Integer> levels = EnchantmentHelper.getEnchantments(stack);
			boolean remove = false;
			for(EnchantedUpgrade entry : supportedUpgrade)
			{
				Integer level = levels.get(entry.getSource());
				if(level != null && level > 0)
				{
					entry.storePoints(myItem, (int)Math.pow(2, level-1));
					remove = true;
				}
			}
			if(remove) item.remove(RemovalReason.DISCARDED);
		}
		return false;
	}
}
