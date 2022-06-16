package uniqueapex.handler.recipe.upgrade;

import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import net.minecraft.enchantment.Enchantment;

public class DefaultFusionUpgradeRecipe extends FusionUpgradeRecipe
{
	public DefaultFusionUpgradeRecipe(Enchantment enchantment)
	{
		super(null, Object2IntMaps.emptyMap(), enchantment, 300, 2);
	}
	
}
