package uniquee.compat;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.curse.DeathsOdiumEnchantment;
import uniquee.enchantments.unique.AresBlessingEnchantment;
import uniquee.utils.MiscUtil;

public class FirstAidHandler
{
	public static final FirstAidHandler INSTANCE = new FirstAidHandler();
	
	@SubscribeEvent
	public void onFirstAidEvent(FirstAidLivingDamageEvent event)
	{
		if(event.getAfterDamage().isDead(event.getPlayer()))
		{
			DamageSource source = event.getSource();
			if(!source.isMagicDamage() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EquipmentSlotType.CHEST);
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isDamageable())
				{
					float damage = event.getUndistributedDamage();
					stack.damageItem((int)(damage * (AresBlessingEnchantment.SCALAR.get() / level)), event.getEntityLiving(), MiscUtil.get(EquipmentSlotType.CHEST));
					event.setCanceled(true);
					return;
				}	
			}
			PlayerEntity living = event.getPlayer();
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, living);
			if(slot.getIntValue() > 0)
			{
				living.heal(living.getMaxHealth());
				living.clearActivePotions();
				living.getFoodStats().addStats(Short.MAX_VALUE, 1F);
				living.getPersistentData().putLong(DeathsOdiumEnchantment.CRUSE_TIMER, living.getEntityWorld().getGameTime() + DeathsOdiumEnchantment.DELAY.get());
	            living.addPotionEffect(new EffectInstance(Effects.REGENERATION, 600, 1));
	            living.addPotionEffect(new EffectInstance(Effects.ABSORPTION, 100, 1));
	            living.world.setEntityState(living, (byte)35);
	            living.getItemStackFromSlot(slot.getKey()).shrink(1);
			}
		}
	}
}
