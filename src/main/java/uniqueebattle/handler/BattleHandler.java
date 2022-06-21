package uniqueebattle.handler;

import java.util.Random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.EmptyHandler;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquebase.utils.events.FishingLuckEvent;
import uniquebase.utils.mixin.common.entity.DragonManagerMixin;
import uniqueebattle.UEBattle;
import uniqueebattle.enchantments.complex.AresFragment;
import uniqueebattle.enchantments.complex.ArtemisSoul;
import uniqueebattle.enchantments.complex.DeepWounds;
import uniqueebattle.enchantments.complex.GranisSoul;
import uniqueebattle.enchantments.complex.IfritsBlessing;
import uniqueebattle.enchantments.curse.IfritsJudgement;
import uniqueebattle.enchantments.curse.LunaticDespair;
import uniqueebattle.enchantments.curse.WarsOdium;
import uniqueebattle.enchantments.simple.AresGrace;
import uniqueebattle.enchantments.simple.CelestialBlessing;
import uniqueebattle.enchantments.simple.Fury;
import uniqueebattle.enchantments.simple.GolemSoul;
import uniqueebattle.enchantments.simple.IronBird;
import uniqueebattle.enchantments.simple.Snare;
import uniqueebattle.enchantments.simple.StreakersWill;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		PlayerEntity player = event.player;
		
		if(player.level.getGameTime() % 100 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.CELESTIAL_BLESSING, player.getMainHandItem());
			if(level > 0)
			{
				CompoundNBT data = player.getPersistentData();
				boolean celestial = data.getBoolean(CelestialBlessing.CELESTIAL_DAY);
				if(player.level.isDay() == celestial)
				{
					player.level.playSound(null, player.blockPosition(), celestial ? UEBattle.CELESTIAL_BLESSING_END_SOUND : UEBattle.CELESTIAL_BLESSING_START_SOUND, SoundCategory.AMBIENT, 1F, 1F);					
					data.putBoolean(CelestialBlessing.CELESTIAL_DAY, !celestial);
				}
			}
			
		}
		if(player.isOnGround() && player.level.getGameTime() % 40 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.IRON_BIRD, player.getItemBySlot(EquipmentSlotType.CHEST));
			if(level > 0)
			{
				player.addEffect(new EffectInstance(UEBattle.TOUGHEND, 80, Math.max(MathHelper.floor(Math.sqrt(level)-1), 0), true, true));
			}
		}
		if(player.level.getGameTime() % 4800 == 0)
		{
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, player);
			if(entry.getIntValue() > 0)
			{
				ItemStack stack = player.getItemBySlot(entry.getKey());
				int enderPoints = StackUtils.getInt(stack, ArtemisSoul.ENDER_STORAGE, 0);
				if(enderPoints > 0)
				{
					StackUtils.setInt(stack, ArtemisSoul.ENDER_STORAGE, Math.max(0, enderPoints-MathHelper.ceil(Math.sqrt(entry.getIntValue()))));
				}
				else
				{
					StackUtils.setInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, (int)(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0) * ((-1D/(entry.getIntValue()+1D))+1D)));
				}
			}
		}
		Entity entity = event.player.getRootVehicle();
		if(entity instanceof HorseEntity)
		{
			HorseEntity horse = (HorseEntity)entity;
			int level = MiscUtil.getEnchantmentLevel(UEBattle.GRANIS_SOUL, horse.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(EmptyHandler.INSTANCE).getStackInSlot(1));
			if(level > 0)
			{
				CompoundNBT nbt = horse.getPersistentData();
				if(nbt.getLong(GranisSoul.NEXT_DASH) < player.level.getGameTime())
				{
					ModifiableAttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
					if(instance.getModifier(GranisSoul.DASH_ID) != null)
					{
						instance.removeModifier(GranisSoul.DASH_ID);	
					}
					if(UEBattle.GRANIS_SOUL_DASH.test(player))
					{
						int duration = (MathCache.LOG10.getInt(GranisSoul.DASH_DURATION.get()+player.experienceLevel)-1)*20;
						nbt.putLong(GranisSoul.NEXT_DASH, player.level.getGameTime() + duration+20);
						nbt.putInt(GranisSoul.DASH_TIME, duration);
						instance.addPermanentModifier(new AttributeModifier(GranisSoul.DASH_ID, "Granis Dash", Math.sqrt(GranisSoul.DASH_SPEED.get()+level), Operation.MULTIPLY_TOTAL));
						int bleed = GranisSoul.BLEED_DURATION.get(level);
						for(LivingEntity base : player.level.getEntitiesOfClass(LivingEntity.class, horse.getBoundingBox().inflate(GranisSoul.BLEED_RANGE.get(level)), T -> T != horse && T != player && EntityPredicates.NO_SPECTATORS.test(T)))
						{
							EffectInstance present = base.getEffect(UEBattle.BLEED);
							if(present == null) base.addEffect(new EffectInstance(UEBattle.BLEED, bleed, level-1));
							else base.addEffect(new EffectInstance(UEBattle.BLEED, bleed, present.getAmplifier()+1));
						}
					}
				}
				else
				{
					int timeLeft = nbt.getInt(GranisSoul.DASH_TIME);
					if(timeLeft >= 0)
					{
						timeLeft--;
						nbt.putInt(GranisSoul.DASH_TIME, timeLeft);
						if(timeLeft <= 0)
						{
							horse.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(GranisSoul.DASH_ID);
						}
					}
				}
			}
			else
			{
				ModifiableAttributeInstance instance = horse.getAttribute(Attributes.MOVEMENT_SPEED);
				if(instance.getModifier(GranisSoul.DASH_ID) != null)
				{
					instance.removeModifier(GranisSoul.DASH_ID);	
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getWorld() instanceof ServerWorld)
		{
			int count = event.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG).getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT);
			if(count > 0)
			{
				TileEntity tile = event.getWorld().getBlockEntity(event.getPos());
				if(tile == null) return;
				IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()).orElse(EmptyHandler.INSTANCE);
				if(handler.getSlots() < 9) return;
				LootTable table = ((ServerWorld)event.getWorld()).getServer().getLootTables().get(IfritsJudgement.JUDGEMENT_LOOT);
				LootContext.Builder builder = (new LootContext.Builder((ServerWorld)event.getWorld())).withRandom(event.getWorld().getRandom()).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(event.getPos())).withLuck(event.getPlayer().getLuck()).withParameter(LootParameters.THIS_ENTITY, event.getPlayer());
				table.getRandomItemsRaw(builder.create(LootParameterSets.CHEST), T -> ItemHandlerHelper.insertItem(handler, T, false));
				MiscUtil.getPersistentData(event.getPlayer()).putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, count-1);
				if(event.getPlayer().isShiftKeyDown()) event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onCritEvent(CriticalHitEvent event)
	{
		LivingEntity source = event.getPlayer();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getMainHandItem());
		if(event.isVanillaCritical())
		{
			int points = UEBattle.ARES_UPGRADE.getPoints(source.getMainHandItem());
			if(points > 0) event.setDamageModifier(event.getDamageModifier() + MathCache.SQRT_SPECIAL.getFloat(points));
			dropPlayerHand(event.getTarget(), ench.getInt(UEBattle.FURY));
			return;
		}
		int level = ench.getInt(UEBattle.ARES_FRAGMENT);
		if(level > 0 && source instanceof PlayerEntity)
		{
			PlayerEntity player = (PlayerEntity)source;
			int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
			int posRolls = Math.max(source.level.random.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
			int negRolls = maxRolls - posRolls;
			if(negRolls >= posRolls) return;
			event.setResult(Result.ALLOW);
			event.setDamageModifier(1.5F);
			if(MiscUtil.isTranscendent(source, player.getMainHandItem(), UEBattle.ARES_FRAGMENT))
			{
				event.setDamageModifier(event.getDamageModifier() + AresFragment.TRANSCENDED_CRIT_MULTIPLIER.getFloat()*(event.isVanillaCritical() ? 1.0F : 0.0F));
			}
			int points = UEBattle.ARES_UPGRADE.getPoints(source.getMainHandItem());
			if(points > 0) event.setDamageModifier(event.getDamageModifier() + MathCache.SQRT_SPECIAL.getFloat(points));
			dropPlayerHand(event.getTarget(), ench.getInt(UEBattle.FURY));
		}
	}
	
	@SubscribeEvent
	public void onItemUseTick(LivingEntityUseItemEvent.Tick event)
	{
		int level = MiscUtil.getEnchantmentLevel(UEBattle.CELESTIAL_BLESSING, event.getItem());
		LivingEntity entity = event.getEntityLiving();
		if(level > 0)
		{
			float boost = MiscUtil.isTranscendent(entity.getEntity(), event.getItem(), UEBattle.CELESTIAL_BLESSING) ? CelestialBlessing.SPEED_BONUS.getAsFloat(level) * CelestialBlessing.SPEED_BONUS.getAsFloat(level) : CelestialBlessing.SPEED_BONUS.getAsFloat(level);
			double num = (1 + (event.getEntityLiving().level.isNight() ? boost : 0));
			event.setDuration((int) Math.max(10,event.getDuration() / num));
		}
		level = UEBattle.LUNATIC_UPGRADE.getCombinedPoints(entity);
		if(level > 0)
		{
			event.setDuration(Math.max(1, event.getDuration() - MathCache.LOG10.getInt(level+1)));
		}
	}
	
	protected void dropPlayerHand(Entity target, int level)
	{
		if(level > 0 && target.level.random.nextDouble() < Fury.DROP_CHANCE.getDevided(level))
		{
			if(target instanceof PlayerEntity)
			{
				target.level.playSound(null, target.blockPosition(), UEBattle.FURY_DROP_SOUND, SoundCategory.AMBIENT, 10F, 1F);
				PlayerInventory player = ((PlayerEntity)target).inventory;
				int firstEmpty = player.getFreeSlot();
				if(firstEmpty == -1)
				{
					player.player.drop(player.getSelected(), false);
					player.removeItemNoUpdate(player.selected);
				}
				else
				{
					ItemStack stack = player.getSelected().copy();
					player.removeItemNoUpdate(player.selected);
					player.add(firstEmpty, stack);
				}
			}
			else if(target instanceof LivingEntity)
			{
				LivingEntity other = (LivingEntity)target;
				if(!other.getMainHandItem().isEmpty())
				{
					if(other.getOffhandItem().isEmpty())
					{
						other.setItemInHand(Hand.OFF_HAND, other.getItemInHand(Hand.MAIN_HAND));
						other.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
					else
					{
						if(!other.level.isClientSide)
						{
							ItemEntity entity = other.spawnAtLocation(other.getMainHandItem(), 0F);
							if(entity != null) entity.setPickUpDelay(30);
						}
						other.setItemInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityLoot(LootingLevelEvent event)
	{
		Entity entity = event.getDamageSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, base);
			if(entry.getIntValue() > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + MathCache.LOG10.getInt(1+StackUtils.getInt(base.getItemBySlot(entry.getKey()), ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)*entry.getIntValue()));
			}
			int level = UEBattle.IFRITS_UPGRADE.getCombinedPoints(base);
			if(level > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + (int)(MathCache.dynamicLog(level+1, 4)));
			}
		}
	}
	
	@SubscribeEvent
	public void onFishingLuck(FishingLuckEvent event)
	{
		int level = UEBattle.IFRITS_UPGRADE.getPoints(event.getStack());
		if(level > 0)
		{
			event.setLevel(event.getLevel() + (int)(MathCache.dynamicLog(level+1, 4)));
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null) return;
		Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, event.getAttackingPlayer());
		if(entry.getIntValue() > 0)
		{
			ItemStack stack = event.getAttackingPlayer().getItemBySlot(entry.getKey());
			double temp = ArtemisSoul.TEMP_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0));
			event.setDroppedExperience(event.getDroppedExperience()*MathCache.LOG10.getInt(100+MathHelper.ceil(entry.getIntValue()*Math.sqrt((ArtemisSoul.PERM_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)))+(temp*temp))))-1);
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			LivingEntity target = event.getEntityLiving();
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getMainHandItem());
			int level = ench.getInt(UEBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof PlayerEntity)
			{
				PlayerEntity player = (PlayerEntity)source;
				int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
				int posRolls = Math.max(source.level.random.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
				int negRolls = maxRolls - posRolls;
				double speed = MiscUtil.getAttackSpeed(player);
				float damageFactor = (float)(Math.log(1+Math.sqrt(player.experienceLevel*level*level)*(1+(MiscUtil.getArmorProtection(target))*AresFragment.ARMOR_PERCENTAGE.get())) / (100F*speed));
				event.setAmount(event.getAmount() * (1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls)));
				source.getMainHandItem().hurtAndBreak(MathHelper.ceil(Math.log(Math.abs(posRolls-Math.sqrt(negRolls)))*(((posRolls-negRolls) != 0 ? posRolls-negRolls : 1)/speed)), source, MiscUtil.get(EquipmentSlotType.MAINHAND));
			}
			if(event.getEntityLiving().isOnFire())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UEBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UEBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isOnFire() ? 2 : 1)));
				}
			}
			level = ench.getInt(UEBattle.DEEP_WOUNDS);
			if(level > 0 && target.getItemBySlot(EquipmentSlotType.CHEST).isEmpty())
			{
				EffectInstance effect = target.getEffect(UEBattle.BLEED);
				if(effect != null)
				{
					event.setAmount(event.getAmount() * MathCache.LOG.getFloat(10+((int) DeepWounds.BLEED_SCALE.get(MiscUtil.getPlayerLevel(source, 70))/100)));
				}
				target.addEffect(new EffectInstance(UEBattle.BLEED, (int)Math.pow(DeepWounds.DURATION.get(level), 0.4D)*20, effect == null ? 0 : effect.getAmplifier()+1));
			}
			level = MiscUtil.getEnchantedItem(UEBattle.STREAKERS_WILL, source).getIntValue();
			if(level > 0 && source.level.random.nextDouble() < StreakersWill.CHANCE.getAsDouble(level))
			{
				LivingEntity enemy = target;
				for(EquipmentSlotType targetSlot : new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.HEAD})
				{
					ItemStack stack = enemy.getItemBySlot(targetSlot);
					if(stack.isEmpty()) continue;
					stack.hurtAndBreak((int)Math.ceil(StreakersWill.LOSS_PER_LEVEL.get(level)/MiscUtil.getAttackSpeed(source)), enemy, MiscUtil.get(targetSlot));
					break;
				}
			}
			level = MiscUtil.getEnchantedItem(UEBattle.ARES_GRACE, source).getIntValue();
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (float)Math.log(1+Math.sqrt(MiscUtil.getArmorProtection(target)*target.getHealth()*MiscUtil.getPlayerLevel(source, 0)*level)*AresGrace.DAMAGE.get()));
				source.getMainHandItem().hurtAndBreak(MathHelper.ceil(AresGrace.DURABILITY.get(Math.log(1+level*source.getHealth()))), source, MiscUtil.get(EquipmentSlotType.MAINHAND));
			}
			EquipmentSlotType slot = null;
			if(event.getSource().isProjectile())
			{
				Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UEBattle.IFRITS_BLESSING, source);
				slot = found.getKey();
				level = found.getIntValue();
			}
			else
			{
				level = ench.getInt(UEBattle.IFRITS_JUDGEMENT);
				slot = level > 0 ? EquipmentSlotType.MAINHAND : null;
			}
			if(level > 0)
			{
				CompoundNBT entityNBT = MiscUtil.getPersistentData(event.getEntityLiving());
				ListNBT list = entityNBT.getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = source.getItemBySlot(slot).getItem().getRegistryName().toString();
				for(int i = 0,m=list.size();i<m;i++)
				{
					CompoundNBT data = list.getCompound(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					CompoundNBT data = new CompoundNBT();
					data.putString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.putInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.add(data);
					entityNBT.put(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}
			EffectInstance effect = event.getEntityLiving().getEffect(UEBattle.TOUGHEND);
			if(effect != null)
			{
				event.setAmount((float)(event.getAmount() * Math.pow(0.9, effect.getAmplifier()+1)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDamage(LivingDamageEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			int level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.LUNATIC_DESPAIR, source);
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat(MathCache.LOG_ADD.getFloat(level))));
				source.invulnerableTime = 0;
				source.hurt(DamageSource.MAGIC, (float)Math.pow(event.getAmount()*level, 0.25)-1);
			}
			level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.WARS_ODIUM, source);
			if(level > 0)
			{
				CompoundNBT nbt = MiscUtil.getPersistentData(source);
				nbt.putInt(WarsOdium.HIT_COUNTER, nbt.getInt(WarsOdium.HIT_COUNTER)+1);
			}
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		RayTraceResult result = event.getRayTraceResult();
		if(!(result instanceof EntityRayTraceResult))
		{
			return;
		}
		Entity entity = ((EntityRayTraceResult)result).getEntity();
		if(entity instanceof LivingEntity)
		{
			int level = MiscUtil.getEnchantmentLevel(UEBattle.SNARE, StackUtils.getArrowStack(event.getArrow()));
			if(level > 0)
			{
				((LivingEntity)entity).addEffect(new EffectInstance(UEBattle.LOCK_DOWN, Snare.DURATION.get(level)));
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getEntity();
		if(entity instanceof LivingEntity)
		{
			LivingEntity source = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> found = MiscUtil.getEnchantedItem(UEBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				ListNBT list = MiscUtil.getPersistentData(event.getEntityLiving()).getList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.size();i<m;i++)
				{
					max = Math.max(max, list.getCompound(i).getInt(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UEBattle.IFRITS_JUDGEMENT, source);
					source.hurt(DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setSecondsOnFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
				}
				else if(max > IfritsJudgement.FIRE_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UEBattle.IFRITS_JUDGEMENT, source);
					source.hurt(DamageSource.IN_FIRE, IfritsJudgement.FIRE_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setSecondsOnFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));					
				}
				else if(max > 0)
				{
					CompoundNBT compound = MiscUtil.getPersistentData(source);
					compound.putInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT, compound.getInt(IfritsJudgement.FLAG_JUDGEMENT_LOOT)+1);
				}
			}
			int level = MiscUtil.getCombinedEnchantmentLevel(UEBattle.WARS_ODIUM, source);
			if(level > 0 && !(event.getEntity() instanceof PlayerEntity) && !WarsOdium.BLACKLIST.contains(event.getEntity().getType().getRegistryName()))
			{
				CompoundNBT nbt = MiscUtil.getPersistentData(source);
				double chance = WarsOdium.SPAWN_CHANCE.getAsDouble(nbt.getInt(WarsOdium.HIT_COUNTER)) * MathCache.LOG_ADD_MAX.get(level);
				nbt.remove(WarsOdium.HIT_COUNTER);
				double random = source.level.random.nextDouble();
				if(chance >= random)
				{
					double spawnMod = Math.log(54.6+WarsOdium.MULTIPLIER.get(level))-3;
					int value = (int)spawnMod;
					if(value > 0)
					{
						double extraHealth = WarsOdium.HEALTH_BUFF.getAsDouble(spawnMod);
						ILivingEntityData data = null;//IDEA implement group data support so the curse gets really mean
						EntityType<?> location = event.getEntity().getType();
						Vector3d pos = event.getEntity().position();
						if(location != null)
						{
							for(int i = 0;i<value;i++)
							{
								Entity toSpawn = location.create(event.getEntity().level);
								if(toSpawn instanceof MobEntity)
								{
									MobEntity base = (MobEntity)toSpawn;
									base.moveTo(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(event.getEntityLiving().level.random.nextFloat() * 360.0F), 0.0F);
									base.yRotO = base.yRot;
									base.xRotO = base.xRot;
									data = base.finalizeSpawn((IServerWorld)event.getEntity().level, event.getEntity().level.getCurrentDifficultyAt(toSpawn.blockPosition()), SpawnReason.COMMAND, data, null);
									base.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(WarsOdium.HEALTH_MOD, "wars_spawn_buff", extraHealth, Operation.MULTIPLY_TOTAL));
									base.setHealth(base.getMaxHealth());
									event.getEntity().level.addFreshEntity(toSpawn);
									toSpawn.level.playSound(null, toSpawn.blockPosition(), UEBattle.WARS_ODIUM_REVIVE_SOUND, SoundCategory.AMBIENT, 1F, 1F);
									base.playAmbientSound();
									World world = event.getEntity().level;
									if(base instanceof EnderDragonEntity && world instanceof ServerWorld)
									{
										DragonFightManager manager = ((ServerWorld)world).dragonFight();
										if(manager != null) ((DragonManagerMixin)manager).setNewDragon(base.getUUID());
									}
								}
							}
						}
					}
				}
			}
			Object2IntMap.Entry<EquipmentSlotType> entry = MiscUtil.getEnchantedItem(UEBattle.ARTEMIS_SOUL, source);
			level = entry.getIntValue();
			if(level > 0 && !(event.getEntityLiving() instanceof AgeableEntity))
			{
				ItemStack stack = source.getItemBySlot(entry.getKey());
				String key = ArtemisSoul.isValidSpecialMob(event.getEntity()) ? ArtemisSoul.PERSISTEN_SOUL_COUNT : ArtemisSoul.TEMPORARY_SOUL_COUNT;
				int playerLevel = MiscUtil.getPlayerLevel(source, 70);
				int max = (int)Math.max(Math.sqrt(playerLevel*ArtemisSoul.CAP_SCALE.get())*ArtemisSoul.CAP_FACTOR.get(), ArtemisSoul.CAP_BASE.get());
				int gain = MathCache.LOG10.getInt((int)(10+(entity.level.random.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.MOB_LOOTING, stack)+1)+1)*level*ArtemisSoul.REAP_SCALE.get()*playerLevel));
				gain = MiscUtil.isTranscendent(entity, stack, UEBattle.ARTEMIS_SOUL) ? (int) (gain * ArtemisSoul.TRANSCENDED_REAP_MULTIPLIER.getFloat()) : gain;
				StackUtils.setInt(stack, key, Math.min(StackUtils.getInt(stack, key, 0)+gain, max));
			}
			int points = UEBattle.WARS_UPGRADE.getPoints(source.getMainHandItem());
			if(points > 0) {
				Random rand = event.getEntityLiving().level.getRandom();
				System.out.println(MathCache.LOG10.getFloat(points)*rand.nextFloat());
				source.heal(MathCache.SQRT_EXTRA_SPECIAL.getFloat(points));
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AttributeModifierManager attribute = event.getEntityLiving().getAttributes();
		Multimap<Attribute, AttributeModifier> mods = createModifiersFromStack(event.getEntityLiving(), event.getFrom(), event.getSlot(), event.getEntityLiving().getCommandSenderWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getEntityLiving(), event.getTo(), event.getSlot(), event.getEntityLiving().getCommandSenderWorld());
		if(!mods.isEmpty())
		{
			attribute.addTransientAttributeModifiers(mods);
		}
	}
	
	@SubscribeEvent
	public void onFall(LivingFallEvent event) {
		LivingEntity entity = event.getEntityLiving();
		float distance = event.getDistance();
		if(distance >= 4F)
		{
			ItemStack stack = entity.getItemBySlot(EquipmentSlotType.CHEST);
			if(MiscUtil.isTranscendent(entity, stack, UEBattle.GOLEM_SOUL))
			{
				int level = MiscUtil.getEnchantmentLevel(UEBattle.GOLEM_SOUL, stack);
				for(Entity ent : entity.level.getEntities(entity, new AxisAlignedBB(entity.blockPosition()).inflate(Math.min(distance, 16)))) 
				{
					if(ent instanceof LivingEntity)
					{
						((LivingEntity)ent).addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, GolemSoul.TRANSCENDED_SLOW_TIME.get(level), Math.min(level-1,5)));
					}
				}
			}
		}
	}
	
	private Multimap<Attribute, AttributeModifier> createModifiersFromStack(LivingEntity entity, ItemStack stack, EquipmentSlotType slot, World world)
	{
		Multimap<Attribute, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);

		int level = enchantments.getInt(UEBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			double value = world.isDay() ? (MiscUtil.isTranscendent(entity, stack, UEBattle.CELESTIAL_BLESSING) ? Math.pow(1+CelestialBlessing.SPEED_BONUS.getAsDouble(level),2)-1 : CelestialBlessing.SPEED_BONUS.getAsDouble(level)) : 0;
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", value, Operation.MULTIPLY_TOTAL));
		}
		level = enchantments.getInt(UEBattle.IRON_BIRD);
		if(level > 0)
		{
			double armor = IronBird.ARMOR.getAsDouble(level);
			mods.put(Attributes.ARMOR, new AttributeModifier(IronBird.DAMAGE_MOD, "damage_mod", armor, Operation.ADDITION));
			mods.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(IronBird.TOUGHNESS_MOD, "toughness_mod", Math.sqrt(IronBird.TOUGHNESS.get(armor)), Operation.ADDITION));
		}
		level = enchantments.getInt(UEBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), Operation.MULTIPLY_TOTAL));
			mods.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), Operation.ADDITION));
		}
		level = enchantments.getInt(UEBattle.FURY);
		if(level > 0)
		{
			mods.put(Attributes.ATTACK_SPEED, new AttributeModifier(Fury.SPEED_MOD, "fury_faster_speed", Math.pow(Fury.ATTACK_SPEED_SCALE.get(1.43D * level), 0.125D), Operation.ADDITION));
		}
		return mods;
	}
}
