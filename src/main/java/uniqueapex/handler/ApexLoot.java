package uniqueapex.handler;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import uniqueapex.UEApex;
import uniqueapex.enchantments.simple.SagesFragment;
import uniquebase.api.ILootModifier;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;

public class ApexLoot implements ILootModifier
{
	@Override
	public void handleLoot(ObjectArrayList<ItemStack> generatedLoot, LootContext context)
	{
		if(context.hasParam(LootContextParams.ORIGIN) && (context.hasParam(LootContextParams.TOOL) || context.hasParam(LootContextParams.DAMAGE_SOURCE)))
		{
			int lvl = getLevel(context);
			if(lvl > 0)
			{
				Level level = context.getLevel();
				Vec3 vec = context.getParam(LootContextParams.ORIGIN);
				BlockPos pos = new BlockPos(vec);
				if(level.isLoaded(pos))
				{
					int sum = 0;
					for(int i = 0;i<generatedLoot.size();i++)
					{
						sum += generatedLoot.get(i).getCount();
					}
					level.addFreshEntity(new ExperienceOrb(level, vec.x(), vec.y(), vec.z(), (int)SagesFragment.SCALE.get(sum*lvl)));
				}
			}
		}
	}
	
	private int getLevel(LootContext context)
	{
		if(context.hasParam(LootContextParams.TOOL)) return MiscUtil.getEnchantmentLevel(UEApex.SAGES_FRAGMENT, context.getParam(LootContextParams.TOOL));
		if(context.hasParam(LootContextParams.DAMAGE_SOURCE))
		{
			DamageSource source = context.getParam(LootContextParams.DAMAGE_SOURCE);
			if(source.getDirectEntity() instanceof ThrownTrident trident) return MiscUtil.getEnchantmentLevel(UEApex.SAGES_FRAGMENT, StackUtils.getArrowStack(trident));
			if(source.getEntity() instanceof LivingEntity living) return MiscUtil.getEnchantedItem(UEApex.SAGES_FRAGMENT, living).getIntValue();
		}
		return 0;
	}
}
