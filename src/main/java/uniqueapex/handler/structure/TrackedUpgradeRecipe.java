package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.upgrade.FusionUpgradeRecipe;

public class TrackedUpgradeRecipe extends BaseTrackedRecipe
{
	FusionUpgradeRecipe recipe;
	
	protected TrackedUpgradeRecipe()
	{
	}
	
	public TrackedUpgradeRecipe(World world, BlockPos pos, FusionUpgradeRecipe recipe, FusionContext context, int beaconSize, List<EnderCrystalEntity> endCrystals)
	{
		super(world, pos, context, beaconSize, endCrystals);
		this.recipe = recipe;
	}
	
	@Override
	protected void finishRecipe(FusionContext context, World world)
	{
		if(!recipe.matches(context, world)) return;
		recipe.mergeEnchantments(context);
	}
	
	public static BaseTrackedRecipe loadRecipe(CompoundNBT data)
	{
		return new TrackedUpgradeRecipe().load(data);
	}
}