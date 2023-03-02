package uniquebase.api;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public interface ILootModifier
{
	public void handleLoot(ObjectArrayList<ItemStack> generatedLoot, LootContext context);
}
