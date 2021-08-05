package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uniquee.utils.MiscUtil;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.LunaticDespair;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.ARES_FRAGMENT, source.getHeldItemMainhand());
			if(level > 0 && source instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)source;
				double chance = AresFragment.BASE_CHANCE.get() + (AresFragment.CHANCE_MULT.get(Math.log(Math.pow(player.experienceLevel, level))) / 100D);
				float damage = (float)Math.log(3 + (level * ((1+event.getEntityLiving().getTotalArmorValue()+event.getEntityLiving().getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue())*AresFragment.ARMOR_PERCENTAGE.get())));
				int i = 0;
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
					i++;
				}
				if(i > 0)
				{
					int durability = MathHelper.ceil(Math.log(AresFragment.DURABILITY_SCALING.get(player.experienceLevel * level))/(1D+MiscUtil.getEnchantmentLevel(Enchantments.UNBREAKING, source.getHeldItemMainhand())));
					source.getHeldItemMainhand().damageItem(i*durability, source);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				event.setAmount(event.getAmount() + LunaticDespair.BONUS_DAMAGE.getFloat((float)Math.log(2.8D+level)));
				source.attackEntityFrom(DamageSource.GENERIC, LunaticDespair.SELF_DAMAGE.getFloat(level) * (float)Math.log(2.8+level));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.MAGIC, (level * 0.25F));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributeMap();
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
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(ItemStack stack, EntityEquipmentSlot slot, World world)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : CelestialBlessing.SPEED_BONUS.getAsDouble(level), 2));
		}
		return mods;
	}
}
