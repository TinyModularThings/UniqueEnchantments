package uniqueapex.handler.structure;

import java.util.Iterator;
import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.level.Level;

public class ClientRecipeStorage
{
	public static final ClientRecipeStorage INSTANCE = new ClientRecipeStorage();
	List<RecipeAnimator> animations = new ObjectArrayList<>();
	Level world = null;
	
	public void addAnimation(RecipeAnimator animator) {
		this.animations.add(animator);
	}
	
	public void onTick(Level world)
	{
		if(this.world != world) {
			this.world = world;
			animations.clear();
			return;
		}
		if(world == null || animations.isEmpty()) return;
		for(Iterator<RecipeAnimator> iter = animations.iterator();iter.hasNext();) {
			RecipeAnimator entry = iter.next();
			if(!world.isLoaded(entry.pos)) {
				iter.remove();
				continue;
			}
			entry.tick();
			if(entry.isDone()) {
				iter.remove();
			}
		}
	}
	
	public void render(float time) {
		if(world == null || animations.isEmpty()) return;
		for(RecipeAnimator recipe : animations) {
			recipe.render(world, time);
		}
	}
}
