package uniquee.compat;

import ichttt.mods.firstaid.api.event.FirstAidLivingDamageEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.EntityEquipmentSlot;
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
				}	
			}
		}
	}
}
