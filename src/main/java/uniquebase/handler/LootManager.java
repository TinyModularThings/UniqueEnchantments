package uniquebase.handler;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import uniquebase.api.BaseUEMod;

public class LootManager implements IGlobalLootModifier
{
	public static final Codec<LootManager> CODEC = Codec.unit(LootManager::new);
	
	
	@Override
	public @NotNull ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		for(BaseUEMod mod : BaseUEMod.getAllMods())
		{
			if(mod.getLootManager() != null) mod.getLootManager().handleLoot(generatedLoot, context);
		}
		return generatedLoot;
	}
	
	@Override
	public Codec<? extends IGlobalLootModifier> codec()
	{
		return CODEC;
	}
	
}
