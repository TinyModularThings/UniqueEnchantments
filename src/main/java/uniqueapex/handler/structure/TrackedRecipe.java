package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.fusion.FusionRecipe;

public class TrackedRecipe extends BaseTrackedRecipe
{
	FusionRecipe recipe;
	
	protected TrackedRecipe()
	{
	}
	
	public TrackedRecipe(World world, BlockPos pos, FusionRecipe recipe, FusionContext context, int beaconSize, List<EnderCrystalEntity> endCrystals)
	{
		super(world, pos, context, beaconSize, endCrystals);
		this.recipe = recipe;
	}
	
	@Override
	protected void finishRecipe(FusionContext context, World world) 
	{
		if(!recipe.matches(context, world)) return;
		recipe.assembleEnchantment(context);		
	}
	
	@Override
	public CompoundNBT save(CompoundNBT data)
	{
		super.save(data);
		data.putString("recipe", recipe.getId().toString());
		return data;
	}
	
	public static BaseTrackedRecipe loadRecipe(CompoundNBT data)
	{
		return new TrackedRecipe().load(data);
	}
}