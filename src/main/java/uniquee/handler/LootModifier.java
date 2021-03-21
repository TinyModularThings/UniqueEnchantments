package uniquee.handler;

import java.util.List;

import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.unique.IfritsGraceEnchantment;
import uniquee.enchantments.unique.MidasBlessingEnchantment;
import uniquee.utils.MiscUtil;

public class LootModifier implements IGlobalLootModifier
{
	@Override
	public List<ItemStack> apply(List<ItemStack> generatedLoot, LootContext context)
	{
		if(context.has(LootParameters.TOOL) && context.has(LootParameters.BLOCK_STATE))
		{
			ItemStack stack = context.get(LootParameters.TOOL);
			BlockState state = context.get(LootParameters.BLOCK_STATE);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MIDAS_BLESSING, stack);
			if(level > 0)
			{
				int gold = EntityEvents.getInt(stack, MidasBlessingEnchantment.GOLD_COUNTER, 0);
				if(gold > 0 && MidasBlessingEnchantment.IS_GEM.test(state))
				{
					gold -= (int)(Math.ceil((double)MidasBlessingEnchantment.LEVEL_SCALAR.get() / level) + MidasBlessingEnchantment.BASE_COST.get());
					EntityEvents.setInt(stack, MidasBlessingEnchantment.GOLD_COUNTER, Math.max(0, gold));
					int multiplier = 1 + level;
					List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
					for(ItemStack drop : generatedLoot)
					{
						EntityEvents.growStack(drop, drop.getCount() * multiplier, newDrops);
					}
					generatedLoot.clear();
					generatedLoot.addAll(newDrops);
				}
			}
			level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.IFRIDS_GRACE, stack);
			if(level > 0)
			{
				int stored = EntityEvents.getInt(stack, IfritsGraceEnchantment.LAVA_COUNT, 0);
				if(stored > 0)
				{
					boolean ore = EntityEvents.isOre(state);
					int smelted = 0;
					List<ItemStack> stacks = generatedLoot;
					for(int i = 0,m=stacks.size();i<m;i++)
					{
						ItemStack toBurn = stacks.get(i).copy();
						toBurn.setCount(1);
						IInventory inventory = new Inventory(toBurn);
						ItemStack result = ItemStack.EMPTY;
						for(IRecipe<IInventory> recipe : ServerLifecycleHooks.getCurrentServer().getRecipeManager().getRecipesForType(IRecipeType.SMELTING))
						{
							if(recipe.matches(inventory, null)) result = recipe.getCraftingResult(inventory);
						}
						if(result.isEmpty()) continue;
						result.setCount(result.getCount() * stacks.get(i).getCount());
						stacks.set(i, result);
						smelted++;
					}
					if(smelted > 0)
					{
						stored -= MathHelper.ceil(smelted * (ore ? 5 : 1) * IfritsGraceEnchantment.SCALAR.get() / level);
						EntityEvents.setInt(stack, IfritsGraceEnchantment.LAVA_COUNT, Math.max(0, stored));
					}
				}
			}
		}
		return generatedLoot;
	}
	
	public static class Serializer extends GlobalLootModifierSerializer<LootModifier>
	{
		public static final Serializer INSTANCE = new Serializer();
		
		public Serializer()
		{
			setRegistryName("ue_loot");
		}
		
		@Override
		public LootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition)
		{
			return new LootModifier();
		}

		@Override
		public JsonObject write(LootModifier instance)
		{
			return new JsonObject();
		}
	}
}
