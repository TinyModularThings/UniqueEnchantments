package uniquee.compat;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquebase.utils.MiscUtil;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.unique.AresBlessing;
import uniquee.enchantments.unique.PhoenixBlessing;

public class FirstAidHandler
{
	public static final FirstAidHandler INSTANCE = new FirstAidHandler();
	
	@SubscribeEvent
	public void onFirstAidEvent(FirstAidLivingDamageEvent event)
	{
		if(event.getAfterDamage().isDead(event.getPlayer()))
		{
			DamageSource source = event.getSource();
			if(!source.isMagic() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isDamageableItem())
				{
					float damage = event.getUndistributedDamage();
					stack.hurtAndBreak((int)(damage * (AresBlessing.BASE_DAMAGE.get() / Math.log(1+level))), event.getEntityLiving(), MiscUtil.get(EquipmentSlotType.CHEST));
					event.setCanceled(true);
					return;
				}	
			}
			PlayerEntity living = event.getPlayer();
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, living);
			if(slot.getIntValue() > 0)
			{
				living.heal(living.getMaxHealth());
				living.removeAllEffects();
				living.getFoodData().eat(Short.MAX_VALUE, 1F);
				living.getPersistentData().putLong(DeathsOdium.CURSE_TIMER, living.getCommandSenderWorld().getGameTime() + DeathsOdium.DELAY.get());
	            living.addEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
	            living.addEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
	            living.level.broadcastEntityEvent(living, (byte)35);
	            living.getItemBySlot(slot.getKey()).shrink(1);
	            for(LivingEntity entry : living.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(living.blockPosition()).inflate(PhoenixBlessing.RANGE.getAsDouble(slot.getIntValue()))))
	            {
	            	if(entry == living) continue;
	            	entry.setSecondsOnFire(600000);
	            }
			}
		}
	}
}
