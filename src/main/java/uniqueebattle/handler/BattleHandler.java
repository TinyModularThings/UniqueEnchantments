package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uniquee.utils.MiscUtil;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.EnchantmentAresFragment;
import uniqueebattle.enchantments.EnchantmentCelestialBlessing;
import uniqueebattle.enchantments.EnchantmentLunaticDespair;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.ARES_FRAGMENT, source.getHeldItemMainhand());
			if(level > 0 && source instanceof PlayerEntity)
			{
				PlayerEntity player = (PlayerEntity)source;
				double chance = EnchantmentAresFragment.BASE_CHANCE.get() + ((Math.log(Math.pow(player.experienceLevel, level)) * EnchantmentAresFragment.CHANCE_MULT.get()) / 100D);
				float damage = (float)Math.log(3 + (level * ((1+event.getEntityLiving().getTotalArmorValue()+event.getEntityLiving().getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue())*EnchantmentAresFragment.ARMOR_PERCENTAGE.get())));
				while(chance > 0)
				{
					if(player.world.rand.nextDouble() < chance)
					{
						event.setAmount(event.getAmount() * damage);
					}
					else if(damage != 0F)
					{
						event.setAmount(event.getAmount() / damage);
					}
					chance -= 1D;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (float)(level * EnchantmentLunaticDespair.BONUS_DAMAGE.get()));
				source.attackEntityFrom(DamageSource.MAGIC, (float)((level * EnchantmentLunaticDespair.SELF_DAMAGE.get()) + (level * 0.25F)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributes();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(ItemStack stack, EquipmentSlotType slot, World world)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(EnchantmentCelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : EnchantmentCelestialBlessing.SPEED_BONUS.getAsDouble(level), Operation.MULTIPLY_TOTAL));
		}
		return mods;
	}
}
