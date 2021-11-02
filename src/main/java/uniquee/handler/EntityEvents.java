package uniquee.handler;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import uniquebase.handler.IMathCache;
import uniquebase.utils.EnchantmentContainer;
import uniquebase.utils.MiscUtil;
import uniquebase.utils.StackUtils;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.complex.EnderMending;
import uniquee.enchantments.complex.Momentum;
import uniquee.enchantments.complex.PerpetualStrike;
import uniquee.enchantments.complex.SmartAss;
import uniquee.enchantments.complex.SpartanWeapon;
import uniquee.enchantments.complex.SwiftBlade;
import uniquee.enchantments.curse.DeathsOdium;
import uniquee.enchantments.curse.PestilencesOdium;
import uniquee.enchantments.simple.Berserk;
import uniquee.enchantments.simple.BoneCrusher;
import uniquee.enchantments.simple.EnderEyes;
import uniquee.enchantments.simple.FocusImpact;
import uniquee.enchantments.simple.Range;
import uniquee.enchantments.simple.SagesBlessing;
import uniquee.enchantments.simple.Swift;
import uniquee.enchantments.simple.Vitae;
import uniquee.enchantments.unique.AlchemistsGrace;
import uniquee.enchantments.unique.AresBlessing;
import uniquee.enchantments.unique.ClimateTranquility;
import uniquee.enchantments.unique.Cloudwalker;
import uniquee.enchantments.unique.Ecological;
import uniquee.enchantments.unique.EnderMarksmen;
import uniquee.enchantments.unique.EndestReap;
import uniquee.enchantments.unique.FastFood;
import uniquee.enchantments.unique.Grimoire;
import uniquee.enchantments.unique.IcarusAegis;
import uniquee.enchantments.unique.IfritsGrace;
import uniquee.enchantments.unique.MidasBlessing;
import uniquee.enchantments.unique.NaturesGrace;
import uniquee.enchantments.unique.PhoenixBlessing;
import uniquee.enchantments.unique.WarriorsGrace;
import uniquee.handler.ai.AISpecialFindPlayer;

@SuppressWarnings("deprecation")
public class EntityEvents
{
	public static final EntityEvents INSTANCE = new EntityEvents();
	public static final ThreadLocal<UUID> ENDERMEN_TO_BLOCK = new ThreadLocal<>();
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		Entity entity = event.getEntity();
		if(entity instanceof EntityEnderman)
		{
			EntityAITasks tasks = ((EntityEnderman)entity).targetTasks;
			for(EntityAITaskEntry task : new ObjectArrayList<EntityAITaskEntry>(tasks.taskEntries))
			{
				if(task.priority == 1 && task.action instanceof EntityAINearestAttackableTarget)
				{
					tasks.removeTask(task.action);
					tasks.addTask(1, new AISpecialFindPlayer((EntityEnderman)entity));
				}
			}
		}
		else if(entity instanceof EntityItem)
		{
			if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.GRIMOIRE, ((EntityItem)entity).getItem()) > 0)
			{
				entity.setEntityInvulnerable(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onAnvilRepair(AnvilUpdateEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.GRIMOIRE, event.getLeft()) > 0)
		{
			event.setCost(Integer.MAX_VALUE);
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.phase == Phase.START) return;
		EntityPlayer player = event.player;
		EnchantmentContainer container = new EnchantmentContainer(player);
		if(event.side == Side.SERVER)
		{
			if(player.getHealth() < player.getMaxHealth())
			{
				int level = container.getEnchantment(UniqueEnchantments.NATURES_GRACE, EntityEquipmentSlot.CHEST);
				if(level > 0 && player.world.getTotalWorldTime() % Math.max((int)(NaturesGrace.DELAY.get() / IMathCache.LOG101.get(level)), 1) == 0)
				{
					if(player.getCombatTracker().getBestAttacker() == null && StackUtils.hasBlockCount(player.world, player.getPosition(), 4, NaturesGrace.FLOWERS))
					{
						player.heal(NaturesGrace.HEALING.getAsFloat(level));
					}
				}
			}
			if(player.world.getTotalWorldTime() % 1200 == 0)
			{
				EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.GRIMOIRE);
				for(int i = 0;i<slots.length;i++)
				{
					int level = container.getEnchantment(UniqueEnchantments.GRIMOIRE, slots[i]);
					if(level > 0)
					{
						Grimoire.applyGrimore(player.getItemStackFromSlot(slots[i]), level, player);
					}
				}
			}
			if(player.world.getTotalWorldTime() % 400 == 0)
			{
				EnderMending.shareXP(player, container);				
			}
			if(player.world.getTotalWorldTime() % 100 == 0)
			{
				for(Int2ObjectMap.Entry<ItemStack> entry : container.getEnchantedItems(UniqueEnchantments.ENDER_MENDING))
				{
					ItemStack stack = entry.getValue();
					if(stack.isItemDamaged())
					{
						int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
						if(stored > 0)
						{
							int toRemove = Math.min(stack.getItemDamage(), stored);
							stack.setItemDamage(stack.getItemDamage() - toRemove);
							StackUtils.setInt(stack, EnderMending.ENDER_TAG, stored - toRemove);
						}
					}
				}
				int level = container.getEnchantment(UniqueEnchantments.ENDEST_REAP, EntityEquipmentSlot.MAINHAND);
				if(level > 0)
				{
					StackUtils.setInt(player.getHeldItemMainhand(), EndestReap.REAP_STORAGE, player.getEntityData().getInteger(EndestReap.REAP_STORAGE));
				}
			}
			if(player.world.getTotalWorldTime() % 30 == 0)
			{
				ClimateTranquility.onClimate(player, container);
			}
			if(player.world.getTotalWorldTime() % 10 == 0)
			{
				int level = container.getEnchantment(UniqueEnchantments.ICARUS_AEGIS, EntityEquipmentSlot.CHEST);
				if(level > 0)
				{
					player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getTagCompound().setBoolean(IcarusAegis.FLYING_TAG, player.isElytraFlying());
				}
			}
			if(player.world.getTotalWorldTime() % 40 == 0)
			{
				int level = container.getCombinedEnchantment(UniqueEnchantments.PESTILENCES_ODIUM);
				if(level > 0)
				{
					List<EntityAgeable> living = player.world.getEntitiesWithinAABB(EntityAgeable.class, new AxisAlignedBB(player.getPosition()).grow(PestilencesOdium.RADIUS.get(IMathCache.LOG_ADD_MAX.get(level))));
					for(int i = 0,m=living.size();i<m;i++)
					{
						living.get(i).addPotionEffect(new PotionEffect(UniqueEnchantments.PESTILENCES_ODIUM_POTION, 200, level));
					}
				}
			}
			if(player.world.getTotalWorldTime() % 20 == 0)
			{
				Object2IntMap.Entry<EntityEquipmentSlot> level = container.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING);
				if(level.getIntValue() > 0)
				{
					player.addExhaustion(0.01F * level.getIntValue());
				}
			}
			NBTTagCompound data = event.player.getEntityData();
			if(data.hasKey(DeathsOdium.CURSE_DAMAGE) && data.getLong(DeathsOdium.CURSE_TIMER) < event.player.world.getTotalWorldTime())
			{
				int total = MathHelper.floor(data.getFloat(DeathsOdium.CURSE_DAMAGE) / DeathsOdium.DAMAGE_FACTOR.get());
				if(total > 0)
				{
					IAttributeInstance instance = event.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
					AttributeModifier mod = instance.getModifier(DeathsOdium.REMOVE_UUID);
					if(mod != null)
					{
						double newValue = Math.max(0D, mod.getAmount() - total);
						instance.removeModifier(mod);
						if(newValue > 0)
						{
							instance.applyModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", newValue, 0));
						}
					}
				}
				data.removeTag(DeathsOdium.CURSE_DAMAGE);
			}
		}
		int level = container.getEnchantment(UniqueEnchantments.CLOUD_WALKER, EntityEquipmentSlot.FEET);
		if(level > 0)
		{
			NBTTagCompound nbt = player.getEntityData();
			if(player.isSneaking() && !nbt.getBoolean(Cloudwalker.TRIGGER) && (!player.onGround || nbt.getBoolean(Cloudwalker.ENABLED)))
			{
				nbt.setBoolean(Cloudwalker.ENABLED, !nbt.getBoolean(Cloudwalker.ENABLED));
				nbt.setBoolean(Cloudwalker.TRIGGER, true);
			}
			else if(!player.isSneaking())
			{
				nbt.setBoolean(Cloudwalker.TRIGGER, false);
			}
			ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
			if(nbt.getBoolean(Cloudwalker.ENABLED))
			{
				int value = StackUtils.getInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level));
				if(value <= 0)
				{
					nbt.setBoolean(Cloudwalker.ENABLED, false);
					return;
				}
				player.motionY = player.capabilities.isFlying ? 0.15D : 0D;
				player.fall(player.fallDistance, 1F);
				player.fallDistance = 0F;
				if(!player.isCreative())
				{
					StackUtils.setInt(stack, Cloudwalker.TIMER, value-1);
					if(player.world.getTotalWorldTime() % Math.min(1, (int)(20 * IMathCache.SQRT.get(level))) == 0)
					{
						stack.damageItem(1, player);
					}
				}
			}
			else
			{
				StackUtils.setInt(stack, Cloudwalker.TIMER, Cloudwalker.TICKS.get(level));
			}
		}
		Boolean cache = null;
		//Reflection is slower then direct call. But Twice the Iteration & Double IsEmpty Check is slower then Reflection.
		EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ECOLOGICAL);
		for(int i = 0;i<slots.length;i++)
		{
			level = container.getEnchantment(UniqueEnchantments.ECOLOGICAL, slots[i]);
			ItemStack equipStack = player.getItemStackFromSlot(slots[i]); 
			if(level > 0 && equipStack.isItemDamaged() && player.world.getTotalWorldTime() % Math.max(1, (int)(Ecological.SPEED.get() / Math.log10(10.0D + (player.experienceLevel*level) / Ecological.SPEED_SCALE.get()))) == 0)
			{
				if((cache == null ? cache = StackUtils.hasBlockCount(player.world, player.getPosition(), 1, Ecological.STATES) : cache.booleanValue()))
				{
					equipStack.damageItem(-1, player);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onItemDespawn(ItemExpireEvent event)
	{
		if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.GRIMOIRE, event.getEntityItem().getItem()) > 0)
		{
			event.setExtraLife(10000);
		}
	}
	
	@SubscribeEvent
	public void onXPPickup(PlayerPickupXpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(player == null)
		{
			return;
		}
		List<ItemStack> all = new ObjectArrayList<>();
		List<ItemStack> ender = new ObjectArrayList<>();
		EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack stack = player.getItemStackFromSlot(slots[i]);
			if(stack.isEmpty()) continue;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
			if(level > 0)
			{
				all.add(stack);
				ender.add(stack);
			}
			else if(stack.isItemDamaged() && MiscUtil.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
			{
				all.add(stack);
			}
		}
		if(ender.size() <= 0) return;
		EntityXPOrb orb = event.getOrb();
		int xp = orb.xpValue;
		int totalXP = xp * 2;
		int usedXP = 0;
		usedXP += StackUtils.evenDistribute(totalXP, orb.world.rand, all, (stack, i) -> {
			int used = Math.min(i, stack.getItemDamage());
            stack.setItemDamage(stack.getItemDamage() - used);
            return used;
		});
		if(usedXP >= totalXP)
		{
			orb.xpValue = 0;
			player.onItemPickup(orb, 1);
			orb.setDead();
			event.setCanceled(true);
			return;
		}
		usedXP += StackUtils.evenDistribute(totalXP - usedXP, orb.world.rand, ender, (stack, i) -> {
			int max = MathHelper.ceil(EnderMending.LIMIT.get() * Math.pow(EnderMending.LIMIT_MULTIPLIER.get(), Math.sqrt(MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack))));
			int stored = StackUtils.getInt(stack, EnderMending.ENDER_TAG, 0);
			int left = Math.min(i, max - stored);
			StackUtils.setInt(stack, EnderMending.ENDER_TAG, stored + left);
			return left;
		});
		int left = (totalXP - usedXP) / 2;
		player.onItemPickup(event.getOrb(), 1);
		event.getOrb().setDead();
		event.setCanceled(true);
		if(left > 0) player.addExperience(left);
	}
	

	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getEntityPlayer() == null)
		{
			return;
		}
		EntityPlayer player = event.getEntityPlayer();
		ItemStack held = player.getHeldItemMainhand();
		Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(held);
		int level = ench.getInt(UniqueEnchantments.MOMENTUM);
		if(level > 0 && isMining(player))
		{
			NBTTagCompound nbt = player.getEntityData();
			long worldTime = player.world.getTotalWorldTime();
			long time = nbt.getLong(Momentum.LAST_MINE);
			double count = nbt.getDouble(Momentum.COUNT);
			if(worldTime - time > Momentum.MAX_DELAY.get() || worldTime < time)
			{
				count = 0;
				nbt.setDouble(Momentum.COUNT, 0);
			}
			double flat = Momentum.SPEED.get(count)/Math.pow((1+event.getNewSpeed()), 0.25D);
			double percent = 1 + (Math.sqrt(Momentum.SPEED_MULTIPLIER.get(count))/level);
			event.setNewSpeed((float)((event.getNewSpeed() + flat) * percent));
			nbt.setLong(Momentum.LAST_MINE, worldTime);
		}
		level = ench.getInt(UniqueEnchantments.RANGE);
		if(level > 0)
		{
			double value = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getBaseValue();
			if(value * value < player.getDistanceSqToCenter(event.getPos()))
			{
				event.setNewSpeed(event.getNewSpeed() * Range.REDUCTION.getLogDevided(level));
			}
		}
	}
	
	public boolean isMining(EntityPlayer player)
	{
		if(player instanceof EntityPlayerMP)
		{
			return ReflectionHelper.getPrivateValue(PlayerInteractionManager.class, ((EntityPlayerMP)player).interactionManager, "isDestroyingBlock", "field_73088_d");
		}
		return true;
	}
	
	@SubscribeEvent
	public void onBlockBreak(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		ItemStack held = event.getPlayer().getHeldItemMainhand();
		Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(held);
		int level = enchs.getInt(UniqueEnchantments.ALCHEMISTS_GRACE);
		if(level > 0)
		{
			AlchemistsGrace.applyToEntity(event.getPlayer(), true, event.getState().getBlockHardness(event.getWorld(), event.getPos()));
		}
		level = enchs.getInt(UniqueEnchantments.SMART_ASS);
		if(level > 0)
		{
			if(SmartAss.VALID_STATES.test(event.getState()))
			{
				Block block = event.getState().getBlock();
				int limit = SmartAss.RANGE.get(level);
				World world = event.getWorld();
				IBlockState lastState = null;
				BlockPos lastPos = null;
				for(int i = 1;i<limit;i++)
				{
					BlockPos pos = event.getPos().up(i);
					IBlockState state = world.getBlockState(pos);
					if(state.getBlock() != block)
					{
						continue;
					}
					lastState = state;
					lastPos = pos;
				}
				if(lastState != null && MiscUtil.harvestBlock(event, lastState, lastPos))
				{
					event.setCanceled(true);
					return;
				}
			}
		}
		level = enchs.getInt(UniqueEnchantments.SAGES_BLESSING);
		if(level > 0)
		{
			level += event.getWorld().rand.nextInt(enchs.getInt(Enchantments.FORTUNE)+1);
			event.setExpToDrop((int)(event.getExpToDrop() + event.getExpToDrop() * SagesBlessing.XP_BOOST.get(level)));
		}
		level = enchs.getInt(UniqueEnchantments.MOMENTUM);
		if(level > 0)
		{
			double cap = Momentum.CAP.get() * Math.pow(Momentum.CAP_MULTIPLIER.get(level), 2);
			double extra = Math.min(1000, event.getState().getBlockHardness(event.getWorld(), event.getPos())) * Math.pow(1 + ((level * level) / 100), 1+(level/100));
			NBTTagCompound nbt = event.getPlayer().getEntityData();
			nbt.setDouble(Momentum.COUNT, Math.min(nbt.getDouble(Momentum.COUNT) + extra, cap));
		}
	}
	
	@SubscribeEvent
	public void onBlockLoot(HarvestDropsEvent event)
	{
		if(event.getHarvester() == null)
		{
			return;
		}
		ItemStack stack = event.getHarvester().getHeldItemMainhand();
		Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(stack);
		int midas = enchs.getInt(UniqueEnchantments.MIDAS_BLESSING);
		if(midas > 0)
		{
			int gold = StackUtils.getInt(stack, MidasBlessing.GOLD_COUNTER, 0);
			if(gold > 0 && StackUtils.isGem(event.getState()))
			{
				gold -= (int)(Math.pow(MidasBlessing.GOLD_COST.getAsDouble(midas), 2)/midas);
				StackUtils.setInt(stack, MidasBlessing.GOLD_COUNTER, Math.max(0, gold));
				int multiplier = 1 + midas;
				List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
				for(ItemStack drop : event.getDrops())
				{
					StackUtils.growStack(drop, drop.getCount() * multiplier, newDrops);
				}
				event.getDrops().clear();
				event.getDrops().addAll(newDrops);
			}
		}
		int level = enchs.getInt(UniqueEnchantments.IFRIDS_GRACE);
		if(level > 0)
		{
			int stored = StackUtils.getInt(stack, IfritsGrace.LAVA_COUNT, 0);
			if(stored > 0)
			{
				double extra = (IMathCache.POW2.get(level)/level)+1D;
				boolean ore = StackUtils.isOre(event.getState());
				int smelted = 0;
				List<ItemStack> stacks = event.getDrops();
				for(int i = 0,m=stacks.size();i<m;i++)
				{
					ItemStack toBurn = stacks.get(i).copy();
					toBurn.setCount(1);
					ItemStack burned = FurnaceRecipes.instance().getSmeltingResult(toBurn).copy();
					if(burned.isEmpty())
					{
						continue;
					}
					burned.setCount(burned.getCount() * stacks.get(i).getCount());
					stacks.set(i, burned);
					smelted++;
				}
				if(smelted > 0)
				{
					stored -= MathHelper.ceil((smelted * (ore ? 5 : 1) * IfritsGrace.BASE_CONSUMTION.get() * extra) / level);
					StackUtils.setInt(stack, IfritsGrace.LAVA_COUNT, Math.max(0, stored));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onItemClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(!event.getWorld().isRemote && stack.getItem() instanceof ItemMap && MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_LIBRARIAN, stack) > 0)
		{
			ItemMap map = (ItemMap)stack.getItem();
			MapData data = map.getMapData(stack, event.getWorld());
			if(data == null || data.dimension != event.getWorld().provider.getDimension())
			{
				return;
			}
			int x = data.xCenter;
			int z = data.zCenter;
			BlockPos position = null;
			NBTTagCompound nbt = stack.getTagCompound();
	        if (nbt != null)
	        {
	        	//Have to do it that way because Mine-craft decorations are rotated and its annoying to math that out properly.
	            NBTTagList list = nbt.getTagList("Decorations", 10);
	            for(int i = 0,m=list.tagCount();i<m;i++)
	            {
	                NBTTagCompound nbtData = list.getCompoundTagAt(i);
	                if(nbtData.getString("id").equalsIgnoreCase("+"))
	                {
	                	position = new BlockPos(nbtData.getInteger("x") - 20, 255, nbtData.getInteger("z") - 20);
	                }
	            }
	        }
	        if(position != null)
	        {
				BlockPos pos = event.getWorld().getTopSolidOrLiquidBlock(position);
				event.getEntityPlayer().setPositionAndUpdate(pos.getX() + 0.5F, Math.max(event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        else
	        {
		        int limit = 64 * (1 << data.scale) * 2;
		        int xOffset = (int)((event.getWorld().rand.nextDouble() - 0.5D) * limit);
		        int zOffset = (int)((event.getWorld().rand.nextDouble() - 0.5D) * limit);
				BlockPos pos = event.getWorld().getTopSolidOrLiquidBlock(new BlockPos(x + xOffset, 255, z + zOffset));
				event.getEntityPlayer().setPositionAndUpdate(pos.getX() + 0.5F, Math.max(event.getWorld().getSeaLevel(), pos.getY() + 1), pos.getZ() + 0.5F);
	        }
	        stack.shrink(1);
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(LivingAttackEvent event)
	{
		AlchemistsGrace.applyToEntity(event.getSource().getTrueSource(), false, 1.5F);
	}
	
	@SubscribeEvent
	public void onArmorDamage(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(base.getHeldItemMainhand());
			int level = enchantments.getInt(UniqueEnchantments.SWIFT_BLADE);
			if(level > 0)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (float)Math.log10(10D + (1.6D + Math.log(Math.max(0.25D, attr.getAttributeValue())) / SwiftBlade.BASE_SPEED.get()) * IMathCache.LOG.get(level*level)));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.FOCUS_IMPACT);
			if(level > 0)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(Math.pow(FocusImpact.BASE_SPEED.get() / attr.getAttributeValue(), 2D)*IMathCache.LOG.get(6+level))));
				}
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, base).getIntValue();
			if(level > 0)
			{
				Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(base.world.getBiome(base.getPosition()));
				boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
				boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
				if(hasHot && !hasCold)
				{
					event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, ClimateTranquility.SLOW_TIME.get() * level, level));
				}
				else if(hasCold && !hasHot)
				{
					event.getEntityLiving().setFire(level * ClimateTranquility.BURN_TIME.get());
				}
			}
			if(event.getEntityLiving() instanceof AbstractSkeleton)
			{
				level = enchantments.getInt(UniqueEnchantments.BONE_CRUSH);
				if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeleton)event.getEntityLiving()))
				{
					event.setAmount((float)(event.getAmount() * (1F + Math.log10(1F+BoneCrusher.BONUS_DAMAGE.getFloat(level)))));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.BERSERKER);
			if(level > 0)
			{
				event.setAmount(event.getAmount() * (float)(1D + ((1-(Berserk.MIN_HEALTH.getMax(base.getHealth(), 1D)/base.getMaxHealth())) * Berserk.PERCENTUAL_DAMAGE.get() * IMathCache.LOG10.get(level+1))));
			}
			level = enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE);
			if(level > 0)
			{
				ItemStack held = base.getHeldItemMainhand();
				int count = StackUtils.getInt(held, PerpetualStrike.HIT_COUNT, 0);
				int lastEntity = StackUtils.getInt(held, PerpetualStrike.HIT_ID, 0);
				if(lastEntity != event.getEntityLiving().getEntityId())
				{
					count = 0;
					StackUtils.setInt(held, PerpetualStrike.HIT_ID, event.getEntityLiving().getEntityId());
				}
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				float amount = event.getAmount();
				double damage = (1F + Math.pow(PerpetualStrike.PER_HIT.get(count)/Math.log(2.8D+(attr == null ? 1D : attr.getAttributeValue())), 1.4D)-1F)*level*PerpetualStrike.PER_HIT_LEVEL.get();
				double multiplier = Math.log10(10+(damage/Math.log10(1+event.getAmount())) * PerpetualStrike.MULTIPLIER.get());
				amount += damage;
				amount *= multiplier;
				event.setAmount(amount);
				StackUtils.setInt(held, PerpetualStrike.HIT_COUNT, count+1);
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
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SPARTAN_WEAPON, base.getHeldItemMainhand());
			if(level > 0 && base.getHeldItemOffhand().getItem() instanceof ItemShield)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount((float)(event.getAmount() * (1D + SpartanWeapon.EXTRA_DAMAGE.getFloat()*Math.log((event.getAmount()*event.getAmount())/attr.getAttributeValue())*level)));
				}
			}
			level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDEST_REAP, base.getHeldItemMainhand());
			if(level > 0)
			{
				 event.setAmount(event.getAmount() + (EndestReap.BONUS_DAMAGE_LEVEL.getFloat(level) + EndestReap.REAP_MULTIPLIER.getFloat(level * base.getEntityData().getInteger(EndestReap.REAP_STORAGE))));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
			if(level > 0)
			{
				int feathers = StackUtils.getInt(stack, IcarusAegis.FEATHER_TAG, 0);
				int consume = (int)Math.ceil((double)IcarusAegis.BASE_CONSUMPTION.get() / (double)level);
				if(feathers >= consume)
				{
					feathers -= consume;
					StackUtils.setInt(stack, IcarusAegis.FEATHER_TAG, feathers);
					event.setCanceled(true);
					return;
				}
			}
		}
		if(event.getAmount() >= event.getEntityLiving().getHealth())
		{
			DamageSource source = event.getSource();
			if(!source.isMagicDamage() && source != DamageSource.FALL)
			{
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, stack);
				if(level > 0 && stack.isItemStackDamageable())
				{
					float damage = event.getAmount();
					stack.damageItem((int)(damage * AresBlessing.BASE_DAMAGE.get() / IMathCache.LOG.get(level+1)), event.getEntityLiving());
					event.setCanceled(true);
					return;
				}	
			}
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.PHOENIX_BLESSING, event.getEntityLiving());
			if(slot.getIntValue() > 0)
			{
				EntityLivingBase living = event.getEntityLiving();
				living.heal(living.getMaxHealth());
				living.clearActivePotions();
				if(living instanceof EntityPlayer)
				{
					((EntityPlayer)living).getFoodStats().addStats(Short.MAX_VALUE, 1F);
				}
				living.getEntityData().setLong(DeathsOdium.CURSE_TIMER, living.getEntityWorld().getTotalWorldTime() + DeathsOdium.DELAY.get());
                living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 2));
                living.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
                living.world.setEntityState(living, (byte)35);
                event.getEntityLiving().getItemStackFromSlot(slot.getKey()).shrink(1);
				event.setCanceled(true);
	            for(EntityLivingBase entry : living.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(living.getPosition()).grow(PhoenixBlessing.RANGE.getAsDouble(slot.getIntValue()))))
	            {
	            	if(entry == living) continue;
	            	entry.setFire(600000);
	            }
			}
		}
		if(entity instanceof EntityPlayer)
		{
			NBTTagCompound compound = entity.getEntityData();
			if(compound.getLong(DeathsOdium.CURSE_TIMER) >= entity.world.getTotalWorldTime())
			{
				compound.setFloat(DeathsOdium.CURSE_DAMAGE, compound.getFloat(DeathsOdium.CURSE_DAMAGE)+event.getAmount());
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
		if(level > 0 && stack.getTagCompound().getBoolean(IcarusAegis.FLYING_TAG) && event.getDistance() > 3F)
		{
			int feathers = StackUtils.getInt(stack, IcarusAegis.FEATHER_TAG, 0);
			int consume = (int)(IcarusAegis.BASE_CONSUMPTION.get() / IMathCache.LOG.get(2 + level));
			if(feathers >= consume)
			{
				feathers -= consume;
				StackUtils.setInt(stack, IcarusAegis.FEATHER_TAG, feathers);
				event.setCanceled(true);
				return;
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
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.WARRIORS_GRACE, base.getHeldItemMainhand());
			if(level > 0)
			{
				ItemStack stack = base.getHeldItemMainhand();
				int amount = Math.min(stack.getItemDamage(), MathHelper.ceil(Math.sqrt(event.getEntityLiving().getMaxHealth() * level) * WarriorsGrace.DURABILITY_GAIN.get()));
				if(amount > 0)
				{
					stack.damageItem(-amount, base);
				}
			}
			Entity killed = event.getEntity();
			if(killed != null && base instanceof EntityPlayer)
			{
				int amount = EndestReap.isValid(killed);
				if(amount > 0)
				{
					level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDEST_REAP, base.getHeldItemMainhand());
					if(level > 0)
					{
						NBTTagCompound nbt = entity.getEntityData();
						int result = Math.min(nbt.getInteger(EndestReap.REAP_STORAGE)+amount, ((EntityPlayer)base).experienceLevel);
						nbt.setInteger(EndestReap.REAP_STORAGE, result);
						StackUtils.setInt(base.getHeldItemMainhand(), EndestReap.REAP_STORAGE, result);
					}
				}
			}
		}
		int maxLevel = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, event.getEntityLiving());
		if(maxLevel > 0)
		{
			for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
			{
				ItemStack stack = event.getEntityLiving().getItemStackFromSlot(slot);
				if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, stack) > 0)
				{
					StackUtils.setInt(stack, DeathsOdium.CURSE_STORAGE, Math.min(StackUtils.getInt(stack, DeathsOdium.CURSE_STORAGE, 0) + 1, DeathsOdium.MAX_STORAGE.get()));
					break;
				}
			}
			IAttributeInstance instance = event.getEntityLiving().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
			AttributeModifier mod = instance.getModifier(DeathsOdium.REMOVE_UUID);
			float toRemove = 0F;
			if(mod != null)
			{
				toRemove += mod.getAmount();
				instance.removeModifier(mod);
			}
			NBTTagCompound nbt = event.getEntityLiving().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			if(nbt.getBoolean(DeathsOdium.CURSE_RESET))
			{
				nbt.removeTag(DeathsOdium.CURSE_RESET);
				nbt.removeTag(DeathsOdium.CURSE_STORAGE);
				for(EntityEquipmentSlot slot : EntityEquipmentSlot.values())
				{
					ItemStack stack = event.getEntityLiving().getItemStackFromSlot(slot);
					if(MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, stack) > 0)
					{
						stack.getTagCompound().removeTag(DeathsOdium.CURSE_STORAGE);
					}
				}
				return;
			}
			event.getEntityLiving().getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, nbt);
			nbt.setFloat(DeathsOdium.CURSE_STORAGE, toRemove - (float)Math.ceil(Math.sqrt(DeathsOdium.BASE_LOSS.get(maxLevel))));
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event)
	{
		float f = event.getEntityPlayer().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getFloat(DeathsOdium.CURSE_STORAGE);
		if(f != 0F)
		{
			event.getEntityLiving().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(DeathsOdium.REMOVE_UUID, "odiums_curse", f, 0));
		}
	}
	
	@SubscribeEvent
	public void onEaten(LivingEntityUseItemEvent.Finish event)
	{
		if(event.getItem().getItem() == Items.COOKIE && MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEATHS_ODIUM, event.getItem()) > 0)
		{
			event.getEntityLiving().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).setBoolean(DeathsOdium.CURSE_RESET, true);
			event.getEntityLiving().onKillCommand();
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null)
		{
			return;
		}
		Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, event.getAttackingPlayer());
		int level = slot.getIntValue();
		if(level > 0)
		{
			level += (event.getAttackingPlayer().world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, event.getAttackingPlayer().getItemStackFromSlot(slot.getKey()))+1));
			event.setDroppedExperience((int)(event.getDroppedExperience() + event.getDroppedExperience() * (SagesBlessing.XP_BOOST.get(level))));
		}
	}
	
	@SubscribeEvent
	public void onLootingLevel(LootingLevelEvent event)
	{
		if(event.getDamageSource() == null) return;
		Entity entity = event.getDamageSource().getTrueSource();
		if(entity instanceof EntityLivingBase && event.getEntityLiving() instanceof AbstractSkeleton)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, ((EntityLivingBase)entity).getHeldItemMainhand());
			if(level > 0 && BoneCrusher.isNotArmored((AbstractSkeleton)event.getEntityLiving()))
			{
				event.setLootingLevel((event.getLootingLevel() + 1) + level);
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
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.FAST_FOOD, base);
			int level = slot.getIntValue();
			if(level > 0)
			{
				int looting = base.world.rand.nextInt(1+MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, base.getItemStackFromSlot(slot.getKey())));
				int burning = event.getEntityLiving().isBurning() ? 2 : 1;
				level *= (base.world.rand.nextInt(MiscUtil.getEnchantmentLevel(Enchantments.LOOTING, base.getItemStackFromSlot(slot.getKey()))+1)+1);
				base.getFoodStats().addStats(FastFood.NURISHMENT.get(level+looting) * burning, FastFood.SATURATION.getFloat(level+looting) * burning);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		RayTraceResult result = event.getRayTraceResult();
		if(result.typeOfHit != Type.ENTITY || !(result.entityHit instanceof EntityLivingBase) || event.getEntity().world.isRemote)
		{
			return;
		}
		EntityArrow arrow = event.getArrow();
		AlchemistsGrace.applyToEntity(arrow.shootingEntity, false, 1.5F);
		if(arrow.shootingEntity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)arrow.shootingEntity;
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				ItemStack stack = player.getItemStackFromSlot(slot.getKey());
				arrow.pickupStatus = PickupStatus.DISALLOWED;
				player.addItemStackToInventory(StackUtils.getArrowStack(arrow));
				int needed = Math.min(MathHelper.floor(IMathCache.LOG_ADD.get(level)*EnderMarksmen.EXTRA_DURABILITY.get()), stack.getItemDamage());
				if(needed > 0)
				{
					stack.damageItem(-needed, player);
				}
				if(result.entityHit instanceof EntityEnderman)
				{
					ENDERMEN_TO_BLOCK.set(result.entityHit.getUniqueID());
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEndermenTeleport(EnderTeleportEvent event)
	{
		UUID id = ENDERMEN_TO_BLOCK.get();
		if(event.getEntity().getUniqueID().equals(id))
		{
			ENDERMEN_TO_BLOCK.set(null);
			event.setCanceled(true);
			return;
		}
		EntityLivingBase living = event.getEntityLiving();
        IAttributeInstance iattributeinstance = living.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        double distance = iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
        if(living.getEntityWorld().getNearestAttackablePlayer(living.posX, living.posY, living.posZ, distance, distance, null, EnderEyes.getPlayerFilter(living)) != null)
        {
        	event.setCanceled(true);
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
		//Optimization. After 3 Enchantment's its sure that on average you have more then 1 full iteration. So now we fully iterate once over it since hash-code would be a faster check.
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		int level = enchantments.getInt(UniqueEnchantments.VITAE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.VITAE).contains(slot))
		{
			mods.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(Vitae.getForSlot(slot), "Vitae Boost", level * Vitae.HEALTH_BOOST.get(), 0));
		}
		level = enchantments.getInt(UniqueEnchantments.SWIFT);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.SWIFT).contains(slot))
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(Swift.SPEED_MOD, "Swift Boost", Swift.SPEED_BONUS.getAsDouble(level), 2));
		}
		level = enchantments.getInt(UniqueEnchantments.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.RANGE).contains(slot))
		{
			mods.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(Range.RANGE_MOD, "Range Boost", Range.RANGE.getAsFloat(level), 0));
		}
		level = enchantments.getInt(UniqueEnchantments.DEATHS_ODIUM);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.DEATHS_ODIUM).contains(slot))
		{
			int value = StackUtils.getInt(stack, DeathsOdium.CURSE_STORAGE, 0);
			if(value > 0)
			{
				mods.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(DeathsOdium.getForSlot(slot), "Death Odiums Restore", DeathsOdium.BASE_LOSS.get(value), 0));
			}
		}
		return mods;
	}
}
