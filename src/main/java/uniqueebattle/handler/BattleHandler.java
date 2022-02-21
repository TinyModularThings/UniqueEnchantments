package uniqueebattle.handler;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import uniquebase.handler.MathCache;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniqueebattle.UniqueEnchantmentsBattle;
import uniqueebattle.enchantments.AresFragment;
import uniqueebattle.enchantments.AresGrace;
import uniqueebattle.enchantments.ArtemisSoul;
import uniqueebattle.enchantments.CelestialBlessing;
import uniqueebattle.enchantments.DeepWounds;
import uniqueebattle.enchantments.Fury;
import uniqueebattle.enchantments.GolemSoul;
import uniqueebattle.enchantments.GranisSoul;
import uniqueebattle.enchantments.IfritsBlessing;
import uniqueebattle.enchantments.IfritsJudgement;
import uniqueebattle.enchantments.IronBird;
import uniqueebattle.enchantments.LunaticDespair;
import uniqueebattle.enchantments.StreakersWill;
import uniqueebattle.enchantments.WarsOdium;

public class BattleHandler
{
	public static final BattleHandler INSTANCE = new BattleHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START || event.side.isClient()) return;
		EntityPlayer player = event.player;
		if(player.onGround && player.world.getTotalWorldTime() % 40 == 0)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.IRON_BIRD, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
			if(level > 0)
			{
				player.addPotionEffect(new PotionEffect(UniqueEnchantmentsBattle.TOUGHEND, 80, Math.max(MathHelper.floor(Math.sqrt(level)-1), 0), true, true));
			}
		}
		if(player.world.getTotalWorldTime() % 4800 == 0)
		{
			Object2IntMap.Entry<EntityEquipmentSlot> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, player);
			if(entry.getIntValue() > 0)
			{
				ItemStack stack = player.getItemStackFromSlot(entry.getKey());
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
		Entity entity = event.player.getLowestRidingEntity();
		if(entity instanceof EntityHorse)
		{
			EntityHorse horse = (EntityHorse)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsBattle.GRANIS_SOUL, horse.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(1));
			if(level > 0)
			{
				NBTTagCompound nbt = horse.getEntityData();
				if(nbt.getLong(GranisSoul.NEXT_DASH) < player.world.getTotalWorldTime())
				{
					IAttributeInstance instance = horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
					if(instance.getModifier(GranisSoul.DASH_ID) != null)
					{
						instance.removeModifier(GranisSoul.DASH_ID);	
					}
					if(UniqueEnchantmentsBattle.GRANIS_SOUL_DASH.test(player))
					{
						int duration = (MathCache.LOG10.getInt(GranisSoul.DASH_DURATION.get()+player.experienceLevel)-1)*20;
						nbt.setLong(GranisSoul.NEXT_DASH, player.world.getTotalWorldTime() + duration+20);
						nbt.setInteger(GranisSoul.DASH_TIME, duration);
						instance.applyModifier(new AttributeModifier(GranisSoul.DASH_ID, "Granis Dash", Math.sqrt(GranisSoul.DASH_SPEED.get()+level), 2));
						int bleed = GranisSoul.BLEED_DURATION.get(level);
						for(EntityLivingBase base : player.world.getEntitiesWithinAABB(EntityLivingBase.class, horse.getEntityBoundingBox().grow(GranisSoul.BLEED_RANGE.get(level)), T -> T != horse && T != player && EntitySelectors.NOT_SPECTATING.test(T)))
						{
							base.addPotionEffect(new PotionEffect(UniqueEnchantmentsBattle.BLEED, bleed, level-1));
						}
					}
				}
				else
				{
					int timeLeft = nbt.getInteger(GranisSoul.DASH_TIME);
					if(timeLeft >= 0)
					{
						timeLeft--;
						nbt.setInteger(GranisSoul.DASH_TIME, timeLeft);
						if(timeLeft <= 0)
						{
							horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(GranisSoul.DASH_ID);
						}
					}
				}
			}
			else
			{
				IAttributeInstance instance = horse.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
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
		if(event.getWorld() instanceof WorldServer)
		{
			int count = event.getEntityPlayer().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT);
			if(count > 0)
			{
				TileEntity tile = event.getWorld().getTileEntity(event.getPos());
				if(tile != null && tile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace()))
				{
					IItemHandler handler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, event.getFace());
					if(handler.getSlots() < 9) return;
					LootTable table = ((WorldServer)event.getWorld()).getLootTableManager().getLootTableFromLocation(IfritsJudgement.JUDGEMENT_LOOT);
					for(ItemStack stack : table.generateLootForPools(event.getWorld().rand, new LootContext.Builder((WorldServer)event.getWorld()).withPlayer(event.getEntityPlayer()).withLuck(event.getEntityPlayer().getLuck()).build()))
					{
						ItemHandlerHelper.insertItem(handler, stack, false);
					}
					MiscUtil.getPersistentData(event.getEntityPlayer()).setInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT, count-1);
					if(event.getEntityPlayer().isSneaking()) event.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onCritEvent(CriticalHitEvent event)
	{
		EntityLivingBase source = event.getEntityPlayer();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
		if(event.isVanillaCritical())
		{
			dropPlayerHand(event.getTarget(), ench.getInt(UniqueEnchantmentsBattle.FURY));
			return;
		}
		int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
		if(level > 0 && source instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)source;
			int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
			int posRolls = Math.max(source.world.rand.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
			int negRolls = maxRolls - posRolls;
			if(negRolls >= posRolls) return;
			event.setResult(Result.ALLOW);
			event.setDamageModifier(1.5F);
			dropPlayerHand(event.getTarget(), ench.getInt(UniqueEnchantmentsBattle.FURY));
		}
	}
	
	protected void dropPlayerHand(Entity target, int level)
	{
		if(level > 0 && target.world.rand.nextDouble() < Fury.DROP_CHANCE.getDevided(level))
		{
			if(target instanceof EntityPlayer)
			{
				InventoryPlayer player = ((EntityPlayer)target).inventory;
				int firstEmpty = player.getFirstEmptyStack();
				if(firstEmpty == -1)
				{
					player.player.dropItem(player.getCurrentItem(), false);
					player.removeStackFromSlot(player.currentItem);
				}
				else
				{
					ItemStack stack = player.getCurrentItem().copy();
					player.removeStackFromSlot(player.currentItem);
					player.add(firstEmpty, stack);
				}
			}
			else if(target instanceof EntityLivingBase)
			{
				EntityLivingBase other = (EntityLivingBase)target;
				if(!other.getHeldItemMainhand().isEmpty())
				{
					if(other.getHeldItemOffhand().isEmpty())
					{
						other.setHeldItem(EnumHand.OFF_HAND, other.getHeldItem(EnumHand.MAIN_HAND));
						other.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					}
					else
					{
						other.entityDropItem(other.getHeldItemMainhand(), 0F);
						other.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityLoot(LootingLevelEvent event)
	{
		Entity entity = event.getDamageSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			Object2IntMap.Entry<EntityEquipmentSlot> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, base);
			if(entry.getIntValue() > 0)
			{
				event.setLootingLevel(event.getLootingLevel() + MathCache.LOG10.getInt(1+StackUtils.getInt(base.getItemStackFromSlot(entry.getKey()), ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)*entry.getIntValue()));
			}
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		Object2IntMap.Entry<EntityEquipmentSlot> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, event.getAttackingPlayer());
		if(entry.getIntValue() > 0)
		{
			ItemStack stack = event.getAttackingPlayer().getItemStackFromSlot(entry.getKey());
			double temp = ArtemisSoul.TEMP_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.TEMPORARY_SOUL_COUNT, 0));
			event.setDroppedExperience(event.getDroppedExperience()*MathCache.LOG10.getInt(100+MathHelper.ceil(entry.getIntValue()*Math.sqrt((ArtemisSoul.PERM_SOUL_SCALE.get(StackUtils.getInt(stack, ArtemisSoul.PERSISTEN_SOUL_COUNT, 0)))+(temp*temp))))-1);
		}
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			EntityLivingBase target = event.getEntityLiving();

			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(source.getHeldItemMainhand());
			int level = ench.getInt(UniqueEnchantmentsBattle.ARES_FRAGMENT);
			if(level > 0 && source instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)source;
				int maxRolls = MathHelper.floor(Math.sqrt(level*player.experienceLevel)*AresFragment.BASE_ROLL_MULTIPLIER.get()) + AresFragment.BASE_ROLL.get();
				int posRolls = Math.max(source.world.rand.nextInt(Math.max(1, maxRolls)), MathHelper.floor(Math.sqrt(level)));
				int negRolls = maxRolls - posRolls;
				double speed = MiscUtil.getAttribute(source, SharedMonsterAttributes.ATTACK_SPEED);
				float damageFactor = (float)(Math.log(1+Math.sqrt(player.experienceLevel*level*level)*(1+MiscUtil.getArmorProtection(target)*AresFragment.ARMOR_PERCENTAGE.get())) / (100F*speed));
				event.setAmount(event.getAmount() * ((1F+(damageFactor*posRolls)) / (1F+(damageFactor*negRolls))));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil(Math.log(Math.abs(posRolls-Math.sqrt(negRolls)))*(((posRolls-negRolls) != 0 ? posRolls-negRolls : 1)/speed)), source);
			}
			level = ench.getInt(UniqueEnchantmentsBattle.DEEP_WOUNDS);
			if(level > 0 && target.getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty())
			{
				PotionEffect effect = target.getActivePotionEffect(UniqueEnchantmentsBattle.BLEED);
				if(effect != null)
				{
					event.setAmount(event.getAmount() * MathCache.LOG.getFloat(10+((int)Math.pow(DeepWounds.BLEED_SCALE.get(MiscUtil.getPlayerLevel(source, 70)),2))/100));
				}
				event.setAmount(event.getAmount() * 1+(float)(Math.pow(DeepWounds.SCALE.get(MiscUtil.getPlayerLevel(source, 200)), 2) * 0.01D));
				target.addPotionEffect(new PotionEffect(UniqueEnchantmentsBattle.BLEED, (int)Math.pow(DeepWounds.DURATION.get(level), 0.4D)*20, 0));
			}
			if(target.isBurning())
			{
				level = event.getSource().isProjectile() ? MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source).getIntValue() : ench.getInt(UniqueEnchantmentsBattle.IFRITS_BLESSING);
				if(level > 0)
				{
					event.setAmount(event.getAmount() * ((1 + IfritsBlessing.BONUS_DAMAGE.getLogValue(2.8D, level)) * (source.isBurning() ? 2 : 1)));
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.STREAKERS_WILL, source).getIntValue();
			if(level > 0 && source.world.rand.nextDouble() < StreakersWill.CHANCE.getAsDouble(level))
			{
				EntityLivingBase enemy = target;
				for(EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.HEAD})
				{
					ItemStack stack = enemy.getItemStackFromSlot(slot);
					if(stack.isEmpty()) continue;
					stack.damageItem((int)StreakersWill.LOSS_PER_LEVEL.get(level), enemy);
					break;
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARES_GRACE, source).getIntValue();
			if(level > 0)
			{
				event.setAmount(event.getAmount() + (float)Math.log(1+Math.sqrt(MiscUtil.getArmorProtection(target)*target.getHealth())*MiscUtil.getPlayerLevel(source, 0)*level*AresGrace.DAMAGE.get()));
				source.getHeldItemMainhand().damageItem(MathHelper.ceil(AresGrace.DURABILITY.get(Math.log(1+level*source.getHealth()))), source);
			}
			EntityEquipmentSlot slot = null;
			if(event.getSource().isProjectile())
			{
				Object2IntMap.Entry<EntityEquipmentSlot> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_BLESSING, source);
				slot = found.getKey();
				level = found.getIntValue();
			}
			else
			{
				level = ench.getInt(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT);
				slot = level > 0 ? EntityEquipmentSlot.MAINHAND : null;
			}
			if(level > 0)
			{
				NBTTagCompound entityNBT = MiscUtil.getPersistentData(target);
				NBTTagList list = entityNBT.getTagList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				boolean found = false;
				String id = source.getItemStackFromSlot(slot).getItem().getRegistryName().toString();
				for(int i = 0,m=list.tagCount();i<m;i++)
				{
					NBTTagCompound data = list.getCompoundTagAt(i);
					if(data.getString(IfritsJudgement.FLAG_JUDGEMENT_ID).equalsIgnoreCase(id))
					{
						found = true;
						data.setInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT, data.getInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT)+1);
						break;
					}
				}
				if(!found)
				{
					NBTTagCompound data = new NBTTagCompound();
					data.setString(IfritsJudgement.FLAG_JUDGEMENT_ID, id);
					data.setInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT, 1);
					list.appendTag(data);
					entityNBT.setTag(IfritsJudgement.FLAG_JUDGEMENT_ID, list);
				}
			}
		}
		PotionEffect effect = event.getEntityLiving().getActivePotionEffect(UniqueEnchantmentsBattle.TOUGHEND);
		if(effect != null)
		{
			event.setAmount((float)(event.getAmount() * Math.pow(0.9, effect.getAmplifier()+1)));
		}
	}
	
	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase source = (EntityLivingBase)entity;
			Object2IntMap.Entry<EntityEquipmentSlot> found = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
			if(found.getIntValue() > 0)
			{
				NBTTagList list = MiscUtil.getPersistentData(event.getEntityLiving()).getTagList(IfritsJudgement.FLAG_JUDGEMENT_ID, 10);
				int max = 0;
				for(int i = 0,m=list.tagCount();i<m;i++)
				{
					max = Math.max(max, list.getCompoundTagAt(i).getInteger(IfritsJudgement.FLAG_JUDGEMENT_COUNT));
				}
				if(max > IfritsJudgement.LAVA_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.LAVA, IfritsJudgement.LAVA_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));
				}
				else if(max > IfritsJudgement.FIRE_HITS.get())
				{
					int combined = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.IFRITS_JUDGEMENT, source);
					source.attackEntityFrom(DamageSource.IN_FIRE, IfritsJudgement.FIRE_DAMAGE.getAsFloat(found.getIntValue() * MathCache.LOG_MUL_MAX.getFloat(combined)));
					entity.setFire(Math.max(1, IfritsJudgement.DURATION.get(found.getIntValue()) / 20));					
				}
				else if(max > 0)
				{
					NBTTagCompound compound = MiscUtil.getPersistentData(source);
					compound.setInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT, compound.getInteger(IfritsJudgement.FLAG_JUDGEMENT_LOOT)+1);
				}
			}
			int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.WARS_ODIUM, source);
			if(level > 0 && !(event.getEntity() instanceof EntityPlayer))
			{
				NBTTagCompound nbt = MiscUtil.getPersistentData(source);
				double chance = WarsOdium.SPAWN_CHANCE.getAsDouble(nbt.getInteger(WarsOdium.HIT_COUNTER)) * MathCache.LOG_ADD_MAX.get(level);
				nbt.removeTag(WarsOdium.HIT_COUNTER);
				if(chance >= source.world.rand.nextDouble())
				{
					double spawnMod = Math.log(54.6+WarsOdium.MULTIPLIER.get(level))-3;
					int value = (int)spawnMod;
					if(value > 0)
					{
						double extraHealth = WarsOdium.HEALTH_BUFF.getAsDouble(spawnMod);
						IEntityLivingData data = null;//IDEA implement group data support so the curse gets really mean
						ResourceLocation location = EntityList.getKey(event.getEntity());
						Vec3d pos = event.getEntity().getPositionVector();
						if(location != null)
						{
							for(int i = 0;i<value;i++)
							{
								Entity toSpawn = EntityList.createEntityByIDFromName(location, event.getEntity().world);
								if(toSpawn instanceof EntityLiving)
								{
									EntityLiving base = (EntityLiving)toSpawn;
									base.setLocationAndAngles(pos.x, pos.y, pos.z, MathHelper.wrapDegrees(event.getEntityLiving().world.rand.nextFloat() * 360.0F), 0.0F);
									base.rotationYawHead = base.rotationYaw;
				                    base.renderYawOffset = base.rotationYaw;
									data = base.onInitialSpawn(event.getEntity().world.getDifficultyForLocation(new BlockPos(toSpawn)), data);
									base.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(WarsOdium.HEALTH_MOD, "wars_spawn_buff", extraHealth, 2));
									base.setHealth(base.getMaxHealth());
									event.getEntity().world.spawnEntity(toSpawn);
				                    base.playLivingSound();
								}
							}
						}
					}
				}
			}
			Object2IntMap.Entry<EntityEquipmentSlot> entry = MiscUtil.getEnchantedItem(UniqueEnchantmentsBattle.ARTEMIS_SOUL, source);
			level = entry.getIntValue();
			if(level > 0 && !(event.getEntityLiving() instanceof EntityAgeable))
			{
				ItemStack stack = source.getItemStackFromSlot(entry.getKey());
				String key = ArtemisSoul.isValidSpecialMob(event.getEntity()) ? ArtemisSoul.PERSISTEN_SOUL_COUNT : ArtemisSoul.TEMPORARY_SOUL_COUNT;
				int playerLevel = MiscUtil.getPlayerLevel(source, 70);
				int max = (int)Math.max(Math.sqrt(playerLevel*ArtemisSoul.CAP_SCALE.get())*ArtemisSoul.CAP_FACTOR.get(), ArtemisSoul.CAP_BASE.get());
				int gain = MathCache.LOG10.getInt((int)(10+(entity.world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, stack)+1)+1)*level*ArtemisSoul.REAP_SCALE.get()*playerLevel));
				int preLog = (int)(10+(entity.world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, stack)+1)+1)*level*ArtemisSoul.REAP_SCALE.get()*playerLevel);
				FMLLog.log.info("Gain: "+gain+", PreLog: "+preLog+", Looting: "+(entity.world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, stack)+1)+1)+", Level: "+level+", PlayerLevel: "+playerLevel);
				StackUtils.setInt(stack, key, Math.min(StackUtils.getInt(stack, key, 0)+gain, max));
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
				event.setAmount(event.getAmount() * (1F + LunaticDespair.BONUS_DAMAGE.getFloat(MathCache.LOG_ADD.getFloat(level))));
				source.hurtResistantTime = 0;
				source.attackEntityFrom(DamageSource.MAGIC, (float)Math.pow(event.getAmount()*level, 0.25)-1);
			}
			level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsBattle.WARS_ODIUM, source);
			if(level > 0)
			{
				NBTTagCompound nbt = MiscUtil.getPersistentData(source);
				nbt.setInteger(WarsOdium.HIT_COUNTER, nbt.getInteger(WarsOdium.HIT_COUNTER)+1);
			}
		}
	}
	
	@SubscribeEvent
	public void onEquippementSwapped(LivingEquipmentChangeEvent event)
	{
		AbstractAttributeMap attribute = event.getEntityLiving().getAttributeMap();
		Multimap<String, AttributeModifier> mods = createModifiersFromStack(event.getEntityLiving(), event.getFrom(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.removeAttributeModifiers(mods);
		}
		mods = createModifiersFromStack(event.getEntityLiving(), event.getTo(), event.getSlot(), event.getEntityLiving().getEntityWorld());
		if(!mods.isEmpty())
		{
			attribute.applyAttributeModifiers(mods);
		}
	}
	
	private Multimap<String, AttributeModifier> createModifiersFromStack(EntityLivingBase base, ItemStack stack, EntityEquipmentSlot slot, World world)
	{
		Multimap<String, AttributeModifier> mods = HashMultimap.create();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantmentsBattle.CELESTIAL_BLESSING);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(CelestialBlessing.SPEED_MOD, "speed_boost", world.isDaytime() ? 0F : CelestialBlessing.SPEED_BONUS.getAsDouble(level), 2));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.IRON_BIRD);
		if(level > 0)
		{
			double armor = IronBird.ARMOR.getAsDouble(level);
			mods.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(IronBird.DAMAGE_MOD, "damage_mod", armor, 0));
			mods.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(IronBird.TOUGHNESS_MOD, "toughness_mod", Math.sqrt(IronBird.TOUGHNESS.get(armor)), 0));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.GOLEM_SOUL);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(GolemSoul.SPEED_MOD, "speed_loss", (Math.pow(1-GolemSoul.SPEED.get(), level)-1), 2));
			mods.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(), new AttributeModifier(GolemSoul.KNOCKBACK_MOD, "knockback_boost", GolemSoul.KNOCKBACK.get(level), 0));
		}
		level = enchantments.getInt(UniqueEnchantmentsBattle.FURY);
		if(level > 0)
		{
			mods.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(Fury.SPEED_MOD, "fury_faster_speed", Math.pow(Fury.ATTACK_SPEED_SCALE.get(1.43D * level), 0.125D), 0));
		}
		return mods;
	}
}
