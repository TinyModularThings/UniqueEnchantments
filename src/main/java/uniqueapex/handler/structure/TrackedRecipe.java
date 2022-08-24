package uniqueapex.handler.structure;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import uniqueapex.handler.recipe.FusionContext;
import uniqueapex.handler.recipe.fusion.FusionRecipe;

public class TrackedRecipe extends BaseTrackedRecipe
{
	FusionRecipe recipe;
	
	protected TrackedRecipe()
	{
	}
	
	public TrackedRecipe(Level world, BlockPos pos, FusionRecipe recipe, FusionContext context, int beaconSize, List<EndCrystal> endCrystals)
	{
		super(world, pos, context, beaconSize, endCrystals);
		this.recipe = recipe;
	}
	
	@Override
	protected void finishRecipe(FusionContext context, Level world) 
	{
		if(!recipe.matches(context, world)) return;
		recipe.assembleEnchantment(context);		
	}
	
	@Override
	public CompoundTag save(CompoundTag data)
	{
		super.save(data);
		data.putString("recipe", recipe.getId().toString());
		return data;
	}
	
	public static BaseTrackedRecipe loadRecipe(CompoundTag data)
	{
		return new TrackedRecipe().load(data);
	}
}