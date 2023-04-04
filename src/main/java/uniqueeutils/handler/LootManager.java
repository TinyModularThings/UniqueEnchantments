package uniqueeutils.handler;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import uniquebase.api.ILootModifier;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniqueeutils.UEUtils;
import uniqueeutils.enchantments.complex.AlchemistsBlessing;
import uniqueeutils.enchantments.complex.AlchemistsBlessing.ConversionEntry;

public class LootManager implements ILootModifier
{
	@Override
	public void handleLoot(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		if(context.hasParam(LootContextParams.TOOL) && context.hasParam(LootContextParams.BLOCK_STATE))
		{
			if(context.hasParam(LootContextParams.THIS_ENTITY))
			{
				Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
				if(entity instanceof LivingEntity) {
					process((LivingEntity) entity, generatedLoot, context);
				}
			}
		}
		else if(context.hasParam(LootContextParams.THIS_ENTITY))
		{
			Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
			if(entity instanceof LivingEntity)
			{
				process((LivingEntity) entity, generatedLoot, context);
			}
		}
	}
	
	public void process(LivingEntity living, List<ItemStack> result, LootContext context) {
		ItemStack tool = living.getMainHandItem();
		Object2IntMap<Enchantment> enchants = MiscUtil.getEnchantments(tool); 
		int level = enchants.getInt(UEUtils.ALCHEMISTS_BLESSING);
		if(level > 0)
		{
			int consumed = 0;
			RandomSource random = context.getRandom();
			List<ItemStack> toProcess = new ObjectArrayList<>();
			for(int i = 0,m=result.size();i<m;i++) {
				ItemStack stack = result.get(i);
				ConversionEntry entry = AlchemistsBlessing.RECIPES.get(stack.getItem());
				if(entry == null) continue;
				consumed += stack.getCount();
				entry.generateOutput(random, level - 1, Math.min(random.nextInt(Math.max(enchants.getInt(Enchantments.BLOCK_FORTUNE), enchants.getInt(Enchantments.MOB_LOOTING)) + 1), level), stack.getCount(), toProcess);
				result.remove(i--);
				m--;
			}
			result.addAll(toProcess);
			if(consumed > 0) {
				consumed = Mth.ceil(Math.pow((level * consumed * AlchemistsBlessing.CONSUMTION.get()), 0.6505));
				int stored = StackUtils.getInt(tool, AlchemistsBlessing.STORED_REDSTONE, 0) - consumed;
				StackUtils.setInt(tool, AlchemistsBlessing.STORED_REDSTONE, Math.max(0, stored));
				if(stored < 0)
					tool.hurtAndBreak(-stored, living, MiscUtil.get(EquipmentSlot.MAINHAND));
			}
		}
	}
}