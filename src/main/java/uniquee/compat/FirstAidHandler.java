package uniquee.compat;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.unique.EnchantmentAresBlessing;

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
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST));
				if(level > 0)
				{
					float damage = event.getUndistributedDamage();
					event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST).damageItem((int)(damage * (EnchantmentAresBlessing.SCALAR / level)), event.getEntityLiving());
					event.setCanceled(true);
					return;
				}	
			}
			EntityPlayer living = event.getEntityPlayer();
			ItemStack stack = EnchantmentHelper.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, living);
			int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.PHOENIX_BLESSING, stack);
			if(level > 0)
			{
				living.heal(living.getMaxHealth());
				living.clearActivePotions();
				living.getFoodStats().addStats(Short.MAX_VALUE, 1F);
	            living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
	            living.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
	            living.world.setEntityState(living, (byte)35);
	            stack.shrink(1);
			}
		}
	}
}
