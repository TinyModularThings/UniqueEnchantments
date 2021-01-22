package uniquee.handler;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
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
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import uniquee.UniqueEnchantments;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.enchantments.complex.EnchantmentEnderMending;
import uniquee.enchantments.complex.EnchantmentMomentum;
import uniquee.enchantments.complex.EnchantmentPerpetualStrike;
import uniquee.enchantments.complex.EnchantmentSmartAss;
import uniquee.enchantments.complex.EnchantmentSpartanWeapon;
import uniquee.enchantments.complex.EnchantmentSwiftBlade;
import uniquee.enchantments.curse.EnchantmentDeathsOdium;
import uniquee.enchantments.curse.EnchantmentPestilencesOdium;
import uniquee.enchantments.simple.EnchantmentBerserk;
import uniquee.enchantments.simple.EnchantmentBoneCrusher;
import uniquee.enchantments.simple.EnchantmentFocusImpact;
import uniquee.enchantments.simple.EnchantmentRange;
import uniquee.enchantments.simple.EnchantmentSagesBlessing;
import uniquee.enchantments.simple.EnchantmentSwift;
import uniquee.enchantments.simple.EnchantmentVitae;
import uniquee.enchantments.unique.EnchantmentAlchemistsGrace;
import uniquee.enchantments.unique.EnchantmentAresBlessing;
import uniquee.enchantments.unique.EnchantmentClimateTranquility;
import uniquee.enchantments.unique.EnchantmentCloudwalker;
import uniquee.enchantments.unique.EnchantmentDemetersSoul;
import uniquee.enchantments.unique.EnchantmentEcological;
import uniquee.enchantments.unique.EnchantmentEnderMarksmen;
import uniquee.enchantments.unique.EnchantmentFastFood;
import uniquee.enchantments.unique.EnchantmentIcarusAegis;
import uniquee.enchantments.unique.EnchantmentIfritsGrace;
import uniquee.enchantments.unique.EnchantmentMidasBlessing;
import uniquee.enchantments.unique.EnchantmentNaturesGrace;
import uniquee.enchantments.unique.EnchantmentWarriorsGrace;
import uniquee.handler.ai.AISpecialFindPlayer;
import uniquee.utils.HarvestEntry;
import uniquee.utils.MiscUtil;
import uniquee.utils.Triple;

@SuppressWarnings("deprecation")
public class EntityEvents
{
	public static final EntityEvents INSTANCE = new EntityEvents();
	List<Tuple<Enchantment, String[]>> tooltips = new ObjectArrayList<Tuple<Enchantment, String[]>>();
	List<Triple<Enchantment, ToIntFunction<ItemStack>, String>> anvilHelpers = new ObjectArrayList<Triple<Enchantment, ToIntFunction<ItemStack>, String>>();
	public static final ThreadLocal<UUID> ENDERMEN_TO_BLOCK = new ThreadLocal<>();
	
	public void registerStorageTooltip(Enchantment ench, String translation, String tag)
	{
		tooltips.add(new Tuple<Enchantment, String[]>(ench, new String[]{translation, tag}));
	}
	
	public void registerAnvilHelper(Enchantment ench, ToIntFunction<ItemStack> helper, String tag)
	{
		anvilHelpers.add(Triple.create(ench, helper, tag));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onToolTipEvent(ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();
		Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
		for(int i = 0,m=tooltips.size();i<m;i++)
		{
			Tuple<Enchantment, String[]> entry = tooltips.get(i);
			if(enchantments.getInt(entry.getFirst()) > 0)
			{
				String[] names = entry.getSecond();
				event.getToolTip().add(I18n.format(names[0], getInt(stack, names[1], 0)));
			}
		}
	}
	
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
	}
	
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
			if(player.getHealth() < player.getMaxHealth())
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.NATURES_GRACE, player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
				if(level > 0 && player.world.getTotalWorldTime() % Math.max(MathHelper.floor(Math.sqrt(EnchantmentNaturesGrace.DELAY / level)), 1) == 0)
				{
					if(player.getCombatTracker().getBestAttacker() == null && hasBlockCount(player.world, player.getPosition(), 4, EnchantmentNaturesGrace.FLOWERS))
					{
						player.heal(EnchantmentNaturesGrace.HEALING.getAsFloat(level));
					}
				}
			}
			if(player.world.getTotalWorldTime() % 100 == 0)
			{
				EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
				for(int i = 0;i<slots.length;i++)
				{
					ItemStack stack = player.getItemStackFromSlot(slots[i]);
					int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
					if(level > 0 && stack.isItemDamaged())
					{
						int stored = getInt(stack, EnchantmentEnderMending.ENDER_TAG, 0);
						if(stored > 0)
						{
							int toRemove = Math.min(stack.getItemDamage(), stored);
							stack.setItemDamage(stack.getItemDamage() - toRemove);
							setInt(stack, EnchantmentEnderMending.ENDER_TAG, stored - toRemove);
						}
					}
				}
			}
			if(player.world.getTotalWorldTime() % 30 == 0)
			{
				EnchantmentClimateTranquility.onClimate(player);
			}
			if(player.world.getTotalWorldTime() % 10 == 0)
			{
				ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
				if(level > 0)
				{
					stack.getTagCompound().setBoolean(EnchantmentIcarusAegis.FLYING_TAG, player.isElytraFlying());
				}
			}
			if(player.world.getTotalWorldTime() % 40 == 0)
			{
				int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantments.PESTILENCES_ODIUM, player);
				if(level > 0)
				{
					List<EntityAgeable> living = player.world.getEntitiesWithinAABB(EntityAgeable.class, new AxisAlignedBB(player.getPosition()).grow(EnchantmentPestilencesOdium.RADIUS));
					for(int i = 0,m=living.size();i<m;i++)
					{
						living.get(i).addPotionEffect(new PotionEffect(UniqueEnchantments.PESTILENCES_ODIUM_POTION, 200, level));
					}
				}
			}
			if(player.world.getTotalWorldTime() % 20 == 0)
			{
				HarvestEntry entry = EnchantmentDemetersSoul.getNextIndex(player);
				if(entry != null)
				{
					EnumActionResult result = entry.harvest(player.world, player);
					if(result == EnumActionResult.FAIL)
					{
						NBTTagList list = EnchantmentDemetersSoul.getCrops(player);
						for(int i = 0,m=list.tagCount();i<m;i++)
						{
							if(entry.matches(list.getCompoundTagAt(i)))
							{
								list.removeTag(i--);
								break;
							}
						}
					}
					else if(result == EnumActionResult.SUCCESS)
					{
						player.addExhaustion(0.06F);
					}
				}
				Object2IntMap.Entry<EntityEquipmentSlot> level = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, player);
				if(level.getIntValue() > 0)
				{
					player.addExhaustion(0.01F * level.getIntValue());
				}
			}
		}
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.CLOUD_WALKER, stack);
		if(level > 0)
		{
			NBTTagCompound nbt = player.getEntityData();
			if(player.isSneaking() && !nbt.getBoolean(EnchantmentCloudwalker.TRIGGER) && (!player.onGround || nbt.getBoolean(EnchantmentCloudwalker.ENABLED)))
			{
				nbt.setBoolean(EnchantmentCloudwalker.ENABLED, nbt.getBoolean(EnchantmentCloudwalker.ENABLED));
				nbt.setBoolean(EnchantmentCloudwalker.TRIGGER, true);
			}
			else if(!player.isSneaking())
			{
				nbt.setBoolean(EnchantmentCloudwalker.TRIGGER, false);
			}
			if(nbt.getBoolean(EnchantmentCloudwalker.ENABLED))
			{
				int value = getInt(stack, EnchantmentCloudwalker.TIMER, EnchantmentCloudwalker.TICKS.get(level));
				if(value <= 0)
				{
					nbt.setBoolean(EnchantmentCloudwalker.ENABLED, false);
					return;
				}
				player.motionY = player.capabilities.isFlying ? 0.15D : 0D;
				player.fall(player.fallDistance, 1F);
				player.fallDistance = 0F;
				if(!player.isCreative())
				{
					setInt(stack, EnchantmentCloudwalker.TIMER, value-1);
					if(player.world.getTotalWorldTime() % 20 == 0)
					{
						stack.damageItem(1, player);
					}
				}
			}
			else
			{
				setInt(stack, EnchantmentCloudwalker.TIMER, EnchantmentCloudwalker.TICKS.get(level));
			}
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SWIFT, player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
		if(level > 0 && player.isOnLadder() && player.moveForward != 0F && player.motionY > 0 && player.motionY <= 0.2 && !player.isSneaking())
		{
			player.motionY += EnchantmentSwift.SPEED_BONUS.getAsDouble(level) * 3D;
		}
		Boolean cache = null;
		//Reflection is slower then direct call. But Twice the Iteration & Double IsEmpty Check is slower then Reflection.
		EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ECOLOGICAL);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack equipStack = player.getItemStackFromSlot(slots[i]); 
			level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ECOLOGICAL, equipStack);
			if(level > 0 && equipStack.isItemDamaged() && player.world.getTotalWorldTime() % Math.max(1, (int)(EnchantmentEcological.SPEED / Math.sqrt(level / EnchantmentEcological.SCALE))) == 0)
			{
				if((cache == null ? cache = hasBlockCount(player.world, player.getPosition(), 1, EnchantmentEcological.STATES) : cache.booleanValue()))
				{
					equipStack.damageItem(-1, player);
				}
			}
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
		int maxLevel = 0;
		Object2BooleanMap<ItemStack> values = new Object2BooleanLinkedOpenHashMap<ItemStack>();
		int foundItems = 0;
		EntityEquipmentSlot[] slots = MiscUtil.getEquipmentSlotsFor(UniqueEnchantments.ENDER_MENDING);
		for(int i = 0;i<slots.length;i++)
		{
			ItemStack stack = player.getItemStackFromSlot(slots[i]);
			if(stack.isEmpty())
			{
				continue;
			}
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_MENDING, stack);
			if(level > 0)
			{
				values.put(stack, true);
				foundItems++;
				maxLevel = Math.max(maxLevel, level);
			}
			else if(stack.isItemDamaged() && MiscUtil.getEnchantmentLevel(Enchantments.MENDING, stack) > 0)
			{
				values.put(stack, false);
			}
		}
		if(values.isEmpty() || foundItems <= 0)
		{
			return;
		}
		int xp = event.getOrb().xpValue;
		int totalXP = (int)((xp * 1F - EnchantmentEnderMending.ABSORBTION_RATIO.getAsFloat(maxLevel)) * 2);
		xp -= (totalXP / 2);
		int usedXP = 0;
		for(Object2BooleanMap.Entry<ItemStack> entry : values.object2BooleanEntrySet())
		{
			ItemStack stack = entry.getKey();
			if(stack.isItemDamaged())
			{
				int used = Math.min(totalXP - usedXP, stack.getItemDamage());
                stack.setItemDamage(stack.getItemDamage() - used);
                usedXP += used;
                if(usedXP >= totalXP)
                {
                	break;
                }
			}
		}
		if(totalXP <= usedXP)
		{
			player.onItemPickup(event.getOrb(), 1);
			event.getOrb().setDead();
			event.setCanceled(true);
			player.addExperience(xp);
			return;
		}
		totalXP -= usedXP;
		usedXP = 0;
		int perItem = Math.max(1, totalXP / foundItems);
		for(Object2BooleanMap.Entry<ItemStack> entry : values.object2BooleanEntrySet())
		{
			if(entry.getBooleanValue())
			{
				ItemStack stack = entry.getKey();
				int stored = getInt(stack, EnchantmentEnderMending.ENDER_TAG, 0);
				int left = Math.min(Math.min(totalXP - usedXP, perItem), EnchantmentEnderMending.LIMIT - stored);
				if(left <= 0)
				{
					continue;
				}
				usedXP+=left;
				setInt(stack, EnchantmentEnderMending.ENDER_TAG, stored + left);
			}
		}
		perItem = totalXP - usedXP;
		if(perItem > 0)
		{
			player.addExperience(perItem / 2);
		}
		player.onItemPickup(event.getOrb(), 1);
		event.getOrb().setDead();
		event.setCanceled(true);
		player.addExperience(xp);
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
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MOMENTUM, held);
		if(level > 0 && isMining(player))
		{
			NBTTagCompound nbt = player.getEntityData();
			long worldTime = player.world.getTotalWorldTime();
			long time = nbt.getLong(EnchantmentMomentum.LAST_MINE);
			int count = nbt.getInteger(EnchantmentMomentum.COUNT);
			if(worldTime - time > EnchantmentMomentum.MAX_DELAY || worldTime < time)
			{
				count = 0;
				nbt.setInteger(EnchantmentMomentum.COUNT, 0);
			}
			event.setNewSpeed((float)(event.getNewSpeed() + (event.getNewSpeed() * (level * (count / EnchantmentMomentum.SCALAR) / event.getOriginalSpeed()))));
			nbt.setLong(EnchantmentMomentum.LAST_MINE, worldTime);
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.RANGE, held);
		if(level > 0)
		{
			double value = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getBaseValue();
			if(value * value < player.getDistanceSqToCenter(event.getPos()))
			{
				event.setNewSpeed(event.getNewSpeed() * (1F - EnchantmentRange.REDUCTION.getDevided(level)));
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
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SMART_ASS, held);
		if(level > 0)
		{
			if(EnchantmentSmartAss.VALID_STATES.test(event.getState()))
			{
				Block block = event.getState().getBlock();
				int limit = EnchantmentSmartAss.STATS.get(level);
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
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SAGES_BLESSING, held);
		if(level > 0)
		{
			event.setExpToDrop((int)(event.getExpToDrop() + event.getExpToDrop() * (level * EnchantmentSagesBlessing.XP_BOOST)));
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MOMENTUM, held);
		if(level > 0)
		{
			int max = EnchantmentMomentum.CAP + level;
			NBTTagCompound nbt = event.getPlayer().getEntityData();
			nbt.setInteger(EnchantmentMomentum.COUNT, Math.min(max, nbt.getInteger(EnchantmentMomentum.COUNT) + 1));
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
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.MIDAS_BLESSING, stack);
		if(level > 0)
		{
			int gold = getInt(stack, EnchantmentMidasBlessing.GOLD_COUNTER, 0);
			if(gold > 0 && isGem(event.getState()))
			{
				gold -= (int)(Math.ceil((double)EnchantmentMidasBlessing.LEVEL_SCALAR / (double)level) + EnchantmentMidasBlessing.BASE_COST);
				setInt(stack, EnchantmentMidasBlessing.GOLD_COUNTER, Math.max(0, gold));
				int multiplier = 1 + level;
				List<ItemStack> newDrops = new ObjectArrayList<ItemStack>();
				for(ItemStack drop : event.getDrops())
				{
					growStack(drop, drop.getCount() * multiplier, newDrops);
				}
				event.getDrops().clear();
				event.getDrops().addAll(newDrops);
			}
		}
		level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.IFRIDS_GRACE, stack);
		if(level > 0)
		{
			int stored = getInt(stack, EnchantmentIfritsGrace.LAVA_COUNT, 0);
			if(stored > 0)
			{
				boolean ore = isOre(event.getState());
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
					stored -= MathHelper.ceil(smelted * (ore ? 5 : 1) * EnchantmentIfritsGrace.SCALAR / level);
					setInt(stack, EnchantmentIfritsGrace.LAVA_COUNT, Math.max(0, stored));
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
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntityPlayer().isSneaking())
		{
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock() instanceof BlockAnvil)
			{
				ItemStack stack = event.getItemStack();
				Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(stack);
				for(int i = 0,m=anvilHelpers.size();i<m;i++)
				{
					Triple<Enchantment, ToIntFunction<ItemStack>, String> entry = anvilHelpers.get(i);
					if(enchantments.getInt(entry.getKey()) > 0)
					{
						int found = consumeItems(event.getEntityPlayer(), entry.getValue(), Integer.MAX_VALUE);
						if(found > 0)
						{
							setInt(stack, entry.getExtra(), found + getInt(stack, entry.getExtra(), 0));
							event.setCancellationResult(EnumActionResult.SUCCESS);
							event.setCanceled(true);
						}
					}
				}
			}
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.DEMETERS_SOUL, event.getItemStack());
			if(level > 0 && CropHarvestRegistry.INSTANCE.isValid(state.getBlock()) && !event.getWorld().isRemote)
			{
				HarvestEntry entry = new HarvestEntry(event.getWorld().provider.getDimension(), event.getPos().toLong());
				NBTTagList list = EnchantmentDemetersSoul.getCrops(event.getEntityPlayer());
				boolean found = false;
				for(int i = 0,m=list.tagCount();i<m;i++)
				{
					if(entry.matches(list.getCompoundTagAt(i)))
					{
						found = true;
						list.removeTag(i--);
						break;
					}
				}
				if(!found)
				{
					list.appendTag(entry.save());
				}
				event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqee.crops."+(found ? "removed" : "added")+".name"), false);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityHit(LivingAttackEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		EnchantmentAlchemistsGrace.applyToEntity(entity);
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingHurtEvent event)
	{
		Entity entity = event.getSource().getTrueSource();
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(base.getHeldItemMainhand());
			if(enchantments.getInt(UniqueEnchantments.BERSERKER) > 0)
			{
				event.setAmount(event.getAmount() * (1F + ((float)EnchantmentBerserk.SCALAR * (base.getMaxHealth() / Math.max(base.getHealth(), 1F)))));
			}
			int level = enchantments.getInt(UniqueEnchantments.SWIFT_BLADE);
			if(level > 0)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(attr.getAttributeValue() / EnchantmentSwiftBlade.SCALAR * level)));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.FOCUS_IMPACT);
			if(level > 0)
			{
				IAttributeInstance attr = base.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_SPEED);
				if(attr != null)
				{
					event.setAmount(event.getAmount() * (1F + (float)Math.log10(EnchantmentFocusImpact.SCALAR * level / (attr.getAttributeValue() * attr.getAttributeValue()))));
				}
			}
			level = enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE);
			if(level > 0)
			{
				ItemStack held = base.getHeldItemMainhand();
				int count = getInt(held, EnchantmentPerpetualStrike.HIT_COUNT, 0);
				int lastEntity = getInt(held, EnchantmentPerpetualStrike.HIT_ID, 0);
				if(lastEntity != event.getEntityLiving().getEntityId())
				{
					count = 0;
					setInt(held, EnchantmentPerpetualStrike.HIT_ID, event.getEntityLiving().getEntityId());
				}
				event.setAmount((float)(event.getAmount() * (1F + (level * count * EnchantmentPerpetualStrike.SCALAR))));
				setInt(held, EnchantmentPerpetualStrike.HIT_COUNT, count+1);
			}
			level = MiscUtil.getEnchantedItem(UniqueEnchantments.CLIMATE_TRANQUILITY, base).getIntValue();
			if(level > 0)
			{
				Set<BiomeDictionary.Type> effects = BiomeDictionary.getTypes(base.world.getBiome(base.getPosition()));
				boolean hasHot = effects.contains(BiomeDictionary.Type.HOT) || effects.contains(BiomeDictionary.Type.NETHER);
				boolean hasCold = effects.contains(BiomeDictionary.Type.COLD);
				if(hasHot && !hasCold)
				{
					event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, EnchantmentClimateTranquility.SLOW_TIME * level, level));
				}
				else if(hasCold && !hasHot)
				{
					event.getEntityLiving().setFire(level * EnchantmentClimateTranquility.BURN_TIME);
				}
			}
			if(event.getEntityLiving() instanceof AbstractSkeleton)
			{
				level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, base.getHeldItemMainhand());
				if(level > 0 && EnchantmentBoneCrusher.isNotArmored((AbstractSkeleton)event.getEntityLiving()))
				{
					event.setAmount((float)(event.getAmount() * (1F + (EnchantmentBoneCrusher.SCALAR * level))));
				}
			}
		}
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;
			ItemStack stack = player.getHeldItemMainhand();
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.RANGE, stack);
			if(level > 0)
			{
				double value = player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getBaseValue();
				if(value * value < player.getDistanceSq(event.getEntity()))
				{
					event.setAmount(event.getAmount() * (1F - EnchantmentRange.REDUCTION.getDevided(level)));
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
			EntityLivingBase base = (EntityLivingBase)entity;
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.SPARTAN_WEAPON, base.getHeldItemMainhand());
			if(level > 0 && base.getHeldItemOffhand().getItem() instanceof ItemShield)
			{
				event.setAmount(event.getAmount() + (event.getAmount()*((float)EnchantmentSpartanWeapon.SCALAR*level)));
			}
		}
		if(event.getSource() == DamageSource.FLY_INTO_WALL)
		{
			ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
			if(level > 0)
			{
				int feathers = getInt(stack, EnchantmentIcarusAegis.FEATHER_TAG, 0);
				int consume = (int)Math.ceil((double)EnchantmentIcarusAegis.SCALAR / (double)level);
				if(feathers >= consume)
				{
					feathers -= consume;
					setInt(stack, EnchantmentIcarusAegis.FEATHER_TAG, feathers);
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
					stack.damageItem((int)(damage * (EnchantmentAresBlessing.SCALAR / level)), event.getEntityLiving());
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
                living.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 600, 2));
                living.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
                living.world.setEntityState(living, (byte)35);
                event.getEntityLiving().getItemStackFromSlot(slot.getKey()).shrink(1);
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingFall(LivingFallEvent event)
	{
		ItemStack stack = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.ICARUS_AEGIS, stack);
		if(level > 0 && stack.getTagCompound().getBoolean(EnchantmentIcarusAegis.FLYING_TAG))
		{
			int feathers = getInt(stack, EnchantmentIcarusAegis.FEATHER_TAG, 0);
			int consume = (int)Math.max(Math.ceil((double)EnchantmentIcarusAegis.SCALAR / (double)level), 4D);
			if(feathers >= consume)
			{
				feathers -= consume;
				setInt(stack, EnchantmentIcarusAegis.FEATHER_TAG, feathers);
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
				int amount = Math.min(stack.getItemDamage(), MathHelper.ceil(Math.sqrt(event.getEntityLiving().getMaxHealth() * level) * EnchantmentWarriorsGrace.DURABILITY_GAIN));
				if(amount > 0)
				{
					stack.damageItem(-amount, base);
				}
			}
		}
		Object2IntMap.Entry<EntityEquipmentSlot> ench = MiscUtil.getEnchantedItem(UniqueEnchantments.DEATHS_ODIUM, event.getEntityLiving());
		if(ench.getIntValue() > 0)
		{
			ItemStack stack = event.getEntityLiving().getItemStackFromSlot(ench.getKey());
			setInt(stack, EnchantmentDeathsOdium.CURSE_STORAGE, getInt(stack, EnchantmentDeathsOdium.CURSE_STORAGE, 0) + 1);
			IAttributeInstance instance = event.getEntityLiving().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
			AttributeModifier mod = instance.getModifier(EnchantmentDeathsOdium.REMOVE_UUID);
			float toRemove = 0F;
			if(mod != null)
			{
				toRemove += mod.getAmount();
				instance.removeModifier(mod);
			}
			NBTTagCompound nbt = event.getEntityLiving().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
			event.getEntityLiving().getEntityData().setTag(EntityPlayer.PERSISTED_NBT_TAG, nbt);
			nbt.setFloat(EnchantmentDeathsOdium.CURSE_STORAGE, toRemove - 1F);
		}
	}
	
	@SubscribeEvent
	public void onRespawn(PlayerEvent.Clone event)
	{
		float f = event.getEntityPlayer().getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getFloat(EnchantmentDeathsOdium.CURSE_STORAGE);
		if(f != 0)
		{
			event.getEntityLiving().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(EnchantmentDeathsOdium.REMOVE_UUID, "odiums_curse", f, 0));
		}
	}
	
	@SubscribeEvent
	public void onXPDrop(LivingExperienceDropEvent event)
	{
		if(event.getAttackingPlayer() == null)
		{
			return;
		}
		int level = MiscUtil.getEnchantedItem(UniqueEnchantments.SAGES_BLESSING, event.getAttackingPlayer()).getIntValue();
		if(level > 0)
		{
			event.setDroppedExperience((int)(event.getDroppedExperience() + event.getDroppedExperience() * (level * EnchantmentSagesBlessing.XP_BOOST)));
		}
	}
	
	@SubscribeEvent
	public void onLootingLevel(LootingLevelEvent event)
	{
		Entity entity = event.getDamageSource().getTrueSource();
		if(entity instanceof EntityLivingBase && event.getEntityLiving() instanceof AbstractSkeleton)
		{
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.BONE_CRUSH, ((EntityLivingBase)entity).getHeldItemMainhand());
			if(level > 0 && EnchantmentBoneCrusher.isNotArmored((AbstractSkeleton)event.getEntityLiving()))
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
			int level = MiscUtil.getEnchantedItem(UniqueEnchantments.FAST_FOOD, base).getIntValue();
			if(level > 0)
			{
				base.getFoodStats().addStats(EnchantmentFastFood.NURISHMENT.get(level), (float)(EnchantmentFastFood.SATURATION * level));
				event.setCanceled(true);
			}
		}
		if(event.getEntityLiving().isPotionActive(UniqueEnchantments.PESTILENCES_ODIUM_POTION))
		{
			event.setCanceled(true);
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
		EnchantmentAlchemistsGrace.applyToEntity(arrow.shootingEntity);
		if(arrow.shootingEntity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)arrow.shootingEntity;
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ENDERMARKSMEN, player);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				ItemStack stack = player.getItemStackFromSlot(slot.getKey());
				arrow.pickupStatus = PickupStatus.DISALLOWED;
				player.addItemStackToInventory(getArrowStack(arrow));
				int needed = Math.min((int)(level*EnchantmentEnderMarksmen.SCALAR), stack.getItemDamage());
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
			mods.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(EnchantmentVitae.getForSlot(slot), "Vitae Boost", level * EnchantmentVitae.HEALTH_BOOST, 0));
		}
		level = enchantments.getInt(UniqueEnchantments.SWIFT);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.SWIFT).contains(slot))
		{
			mods.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(EnchantmentSwift.SPEED_MOD, "Swift Boost", EnchantmentSwift.SPEED_BONUS.getAsDouble(level), 2));
		}
		level = enchantments.getInt(UniqueEnchantments.RANGE);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.RANGE).contains(slot))
		{
			mods.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(EnchantmentRange.RANGE_MOD, "Range Boost", EnchantmentRange.RANGE.getAsFloat(level), 0));
		}
		level = enchantments.getInt(UniqueEnchantments.DEATHS_ODIUM);
		if(level > 0 && MiscUtil.getSlotsFor(UniqueEnchantments.DEATHS_ODIUM).contains(slot))
		{
			int value = getInt(stack, EnchantmentDeathsOdium.CURSE_STORAGE, 0);
			if(value > 0)
			{
				mods.put(SharedMonsterAttributes.MAX_HEALTH.getName(), new AttributeModifier(EnchantmentDeathsOdium.getForSlot(slot), "Death Odiums Restore", value, 0));
			}
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
	
	public static long getLong(ItemStack stack, String tagName, long defaultValue)
	{
		NBTTagCompound nbt = stack.getTagCompound();
		return nbt == null || !nbt.hasKey(tagName) ? defaultValue : nbt.getLong(tagName);
	}
	
	public static void setLong(ItemStack stack, String tagName, long value)
	{
		stack.setTagInfo(tagName, new NBTTagLong(value));
	}
	
	public static void growStack(ItemStack source, int size, List<ItemStack> output)
	{
		while(size > 0)
		{
			ItemStack stack = source.copy();
			stack.setCount(Math.min(size, stack.getMaxStackSize()));
			output.add(stack);
			size -= stack.getCount();
		}
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
	
	public static boolean isOre(IBlockState state)
	{
		Item item = Item.getItemFromBlock(state.getBlock());
		if(item == Items.AIR)
		{
			return false;
		}
		//Correct way to extract meta-data out of a BlockState. This is accurate from 1.12 backwards to most versions I know. (1.4.7 I think)
		for(int id : OreDictionary.getOreIDs(new ItemStack(item, 1, item.getMetadata(state.getBlock().getMetaFromState(state)))))
		{
			if(OreDictionary.getOreName(id).startsWith("ore"))
			{
				return true;
			}
		}
		return false;
	}
	
	public static boolean isGem(IBlockState state)
	{
		Item item = Item.getItemFromBlock(state.getBlock());
		if(item == Items.AIR)
		{
			return false;
		}
		//Correct way to extract meta-data out of a BlockState. This is accurate from 1.12 backwards to most versions I know. (1.4.7 I think)
		for(int id : OreDictionary.getOreIDs(new ItemStack(item, 1, item.getMetadata(state.getBlock().getMetaFromState(state)))))
		{
			String gem = OreDictionary.getOreName(id);
			if(gem.startsWith("ore") && OreDictionary.doesOreNameExist("gem"+gem.substring(3)))
			{
				return true;
			}
		}
		return false;
	}
	
	public static int consumeItems(EntityPlayer player, ToIntFunction<ItemStack> validator, int limit)
	{
		int found = 0;
		NonNullList<ItemStack> inv = player.inventory.mainInventory;
		for(int i = 0,m=inv.size();i<m;i++)
		{
			ItemStack stack = inv.get(i);
			int value = validator.applyAsInt(stack);
			if(value <= 0)
			{
				continue;
			}
			int left = limit - found;
			if(left >= stack.getCount() * value)
			{
				found+=stack.getCount() * value;
				if(stack.getItem().hasContainerItem(stack))
				{
					inv.set(i, stack.getItem().getContainerItem(stack));
					continue;
				}
				inv.set(i, ItemStack.EMPTY);
			}
			else if(left / value > 0)
			{
				stack.shrink(left / value);
				found += left;
			}
			if(found >= limit)
			{
				break;
			}
		}
		return found;
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
	
	public static int getXP(EntityPlayer player)
	{
		return getXPForLvl(player.experienceLevel) + (int)(player.experience * player.xpBarCap());
	}
	
	public static int getXPForLvl(int level) 
	{
		if (level < 0) return Integer.MAX_VALUE;
		if (level <= 15)return level * level + 6 * level;
		if (level <= 30)return (int) (((level * level) * 2.5D) - (40.5D * level) + 360.0D);
		return (int) (((level * level) * 4.5D) - (162.5D * level) + 2220.0D);
	}

	public static int getLvlForXP(int totalXP)
	{
		int result = 0;
		while (getXPForLvl(result) <= totalXP) 
		{
			result++;
		}
		return --result;
	}
}
