package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.upgrade.FusionUpgradeRecipe;

public class TrackedUpgradeRecipe extends BaseTrackedRecipe
{
	FusionUpgradeRecipe recipe;
	
	protected TrackedUpgradeRecipe()
	{
	}
	
	public TrackedUpgradeRecipe(Level world, BlockPos pos, FusionUpgradeRecipe recipe, FusionContext context, int beaconSize, List<EndCrystal> endCrystals)
	{
		super(world, pos, context, beaconSize, endCrystals);
		this.recipe = recipe;
	}
	
	@Override
	protected void finishRecipe(FusionContext context, Level world)
	{
		if(!recipe.matches(context, world)) return;
		recipe.mergeEnchantments(context);
	}
	
	public static BaseTrackedRecipe loadRecipe(CompoundTag data)
	{
		return new TrackedUpgradeRecipe().load(data);
	}
}