package uniquee.compat;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uniquebase.utils.MiscUtil;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.unique.AresBlessing;
import uniquee.enchantments.unique.PhoenixBlessing;

public class FirstAidHandler
{
	public static FirstAidHandler INSTANCE = new FirstAidHandler();
	
	@Method(modid = "firstaid")
	@SubscribeEvent
	public void onFirstAidEvent(FirstAidLivingDamageEvent event)
	{
		if(event.getAfterDamage().isDead(event.getEntityPlayer()))
		{
			DamageSource source = event.getSource();
			if(!source.isMagicDamage() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isItemStackDamageable())
				{
					float damage = event.getUndistributedDamage();
					stack.damageItem((int)(damage * AresBlessing.BASE_DAMAGE.get() / Math.log(level+1)), event.getEntityLiving());
					event.setCanceled(true);
					return;
				}	
			}
			EntityPlayer living = event.getEntityPlayer();
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, living);
			if(slot.getIntValue() > 0)
			{
				living.heal(living.getMaxHealth());
				living.clearActivePotions();
				living.getFoodStats().addStats(Short.MAX_VALUE, 1F);
				living.getEntityData().setLong(DeathsOdium.CRUSE_TIMER, living.getEntityWorld().getTotalWorldTime() + DeathsOdium.DELAY.get());
	            living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 2));
	            living.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
	            living.world.setEntityState(living, (byte)35);
	            living.getItemStackFromSlot(slot.getKey()).shrink(1);
	            for(EntityLivingBase entry : living.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(living.getPosition()).grow(PhoenixBlessing.RANGE.getAsDouble(slot.getIntValue()))))
	            {
	            	if(entry == living) continue;
	            	entry.setFire(600000);
	            }
			}
		}
	}
}
