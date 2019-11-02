package uniquee.handler;

import java.util.function.Predicate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.complex.EnchantmentPerpetualStrike;
import uniquee.enchantments.complex.EnchantmentSpartanWeapon;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;
import uniquee.enchantments.simple.EnchantmentBerserk;
import uniquee.enchantments.simple.EnchantmentSwift;
import uniquee.enchantments.simple.EnchantmentVitae;
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;

public class EntityEvents
{
	public static final EntityEvents INSTANCE = new EntityEvents();
	
	@SubscribeEvent
	public void onEntityUpdate(PlayerTickEvent event)
	{
		if(event.phase == Phase.START)
		{
			return;
		}
		EntityPlayer player = event.player;
		if(event.side == Side.SERVER)
		{
			if(player.world.getTotalWorldTime() % 200 == 0 && player.getHealth() < player.getMaxHealth())
			{
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.NATURES_GRACE, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
				if(level > 0 && player.getCombatTracker().getBestAttacker() == null && hasBlockCount(player.world, player.getPosition(), 4, EnchantmentNaturesGrace.FLOWERS))
				{
					player.heal((float)(1D * EnchantmentNaturesGrace.SCALAR));
				}
			}
			return;
		}
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.CLOUD_WALKER, stack);
		if(level > 0)
		{
			if(player.isSneaking())
			{
				int value = getInt(stack, "cloud", EnchantmentCloudwalker.TICKS * level);
				if(value <= 0)
				{
					return;
				}
				player.motionY = 0D;
				player.fall(player.fallDistance, 1F);
				player.fallDistance = 0F;
				setInt(stack, "cloud", value-1);
			}
			else
			{
				setInt(stack, "cloud", EnchantmentCloudwalker.TICKS * level);
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(LivingAttackEvent event)
	{
		EnchantmentAlchemistsGrace.applyToEntity(event.getSource().getTrueSource());
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.BERSERKER, base.getHeldItemMainhand());
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (1F + ((float)EnchantmentBerserk.SCALAR * (base.getMaxHealth() / base.getHealth()))));
			}
			level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.SWIFT_BLADE, base.getHeldItemMainhand());
			if(level > 0)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(attr.getAttributeValue() / EnchantmentSwiftBlade.SCALAR * level)));
				}
			}
			level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.PERPETUAL_STRIKE, base.getHeldItemMainhand());
			if(level > 0)
			{
				ItemStack held = base.getHeldItemMainhand();
				int count = getInt(held, "strikes", 0);
				int lastEntity = getInt(held, "hit_id", 0);
				if(lastEntity != event.getEntityLiving().getEntityId())
				{
					count = 0;
					setInt(held, "hit_id", event.getEntityLiving().getEntityId());
				}
				event.setAmount((float)(event.getAmount() * (1F + (level * count * EnchantmentPerpetualStrike.SCALAR))));
				setInt(held, "strikes", count+1);
				
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.SPARTAN_WEAPON, base.getHeldItemMainhand());
			if(level > 0 && base.getHeldItemOffhand().getItem() instanceof ItemShield)
			{
				event.setAmount(event.getAmount() + (event.getAmount()*((float)EnchantmentSpartanWeapon.SCALAR*level)));
			}
		}
		if(event.getAmount() >= event.getEntityLiving().getHealth())
		{
			DamageSource source = event.getSource();
			if(!source.isMagicDamage() && source != DamageSource.FALL)
			{
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST));
				if(level > 0)
				{
					float damage = event.getAmount();
					event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST).damageItem((int)(damage * (EnchantmentAresBlessing.SCALAR / level)), event.getEntityLiving());
					event.setCanceled(true);
				}	
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityKilled(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.WARRIORS_GRACE, base.getHeldItemMainhand());
			if(level > 0)
			{
				ItemStack stack = base.getHeldItemMainhand();
				int amount = Math.min(stack.getItemDamage(), (int)Math.sqrt(event.getEntityLiving().getMaxHealth() * level * EnchantmentWarriorsGrace.SCALAR));
				if(amount > 0)
				{
					stack.damageItem(-amount, base);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityPlayer && event.getEntityLiving() instanceof EntityAnimal)
		{
			EntityPlayer base = (EntityPlayer)entity;
			ItemStack stack = EnchantmentHelper.getEnchantedItem(UniqueEnchantments.FAST_FOOD, base);
			if(!stack.isEmpty())
			{
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.FAST_FOOD, stack) * EnchantmentFastFood.SCALAR;
				base.getFoodStats().addStats(1 * level , 0.5F * level);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		RayTraceResult result = event.getRayTraceResult();
		if(result.typeOfHit != Type.ENTITY || !(result.entityHit instanceof EntityLivingBase))
		{
			return;
		}
		EntityArrow arrow = event.getArrow();
		EnchantmentAlchemistsGrace.applyToEntity(arrow.shootingEntity);
		if(arrow.shootingEntity instanceof EntityPlayer)
		{
			EntityLivingBase hitEntity = (EntityLivingBase)result.entityHit;
			EntityPlayer player = (EntityPlayer)arrow.shootingEntity;
			ItemStack stack = EnchantmentHelper.getEnchantedItem(UniqueEnchantments.ENDERMARKSMEN, player);
			if(!stack.isEmpty())
			{
				int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.ENDERMARKSMEN, stack);
				arrow.pickupStatus = PickupStatus.DISALLOWED;
				player.addItemStackToInventory(this.getArrowStack(arrow));
				int needed = Math.min((int)(level*EnchantmentEnderMarksmen.SCALAR), stack.getItemDamage());
				if(needed > 0)
				{
					stack.damageItem(-needed, player);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributeMap();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getFrom(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getTo(), event.getSlot());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(ItemStack stack, EntityEquipmentSlot slot)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		int level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.VITAE, stack);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(EnchantmentVitae.getForSlot(slot), "Vitae Boost", level * EnchantmentVitae.SCALAR, 0));
		}
		level = EnchantmentHelper.getEnchantmentLevel(UniqueEnchantments.SWIFT, stack);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(EnchantmentSwift.SPEED_MOD, "Swift Boost", EnchantmentSwift.SCALAR * level, 2));
		}
		return mods;
	}
	
	public static int getInt(ItemStack stack, String tagName, int defaultValue)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null || !nbt.hasKey(tagName) ? defaultValue : nbt.getInteger(tagName);
	}
	
	public static void setInt(ItemStack stack, String tagName, int value)
	{
		stack.setTagInfo(tagName, new NBTTagInt(value));
	}
	
	public static ItemStack getArrowStack(EntityArrow arrow)
	{
		try
		{
			//For Every ASM user. No. Anti ASM Guy writing this. Aka no ASM coming here. Live with it.
			return (ItemStack)ReflectionHelper.findMethod(EntityArrow.class, "getArrowStack", "func_184550_j").invoke(arrow, new Object[0]);
		}
		catch(Exception e)
		{
		}
		if(arrow instanceof EntitySpectralArrow)
		{
			return new ItemStack(Items.SPECTRAL_ARROW);
		}
		else if(arrow instanceof EntityTippedArrow)
		{
			NBTTagCompound nbt = new NBTTagCompound();
			arrow.writeEntityToNBT(nbt);
			if(nbt.hasKey("CustomPotionEffects"))
			{
				ItemStack stack = new ItemStack(Items.TIPPED_ARROW);
				stack.setTagInfo("CustomPotionEffects", nbt.getTag("CustomPotionEffects"));
				return stack;
			}
			return new ItemStack(Items.ARROW);
		}
		return ItemStack.EMPTY;
	}
	
	public static boolean hasBlockCount(World world, BlockPos pos, int limit, Predicate<IBlockState> validator)
	{
		MutableBlockPos newPos = new MutableBlockPos();
		int found = 0;
		for(int y = 0;y<=1;y++)
		{
			for(int x = -4;x<=4;x++)
			{
				for(int z = -4;z<=4;z++)
				{
					newPos.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
					if(validator.test(world.getBlockState(newPos)))
					{
						found++;
						if(found >= limit)
						{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
