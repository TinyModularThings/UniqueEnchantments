package uniqueeutils.handler;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import uniquee.api.crops.CropHarvestRegistry;
import uniquee.handler.EntityEvents;
import uniquee.utils.HarvestEntry;
import uniquee.utils.MiscUtil;
import uniqueeutils.UniqueEnchantmentsUtils;
import uniqueeutils.enchantments.EnchantmentClimber;
import uniqueeutils.enchantments.EnchantmentDemetersSoul;
import uniqueeutils.enchantments.EnchantmentFaminesOdium;
import uniqueeutils.enchantments.EnchantmentPhanesRegret;
import uniqueeutils.enchantments.EnchantmentRocketMan;
import uniqueeutils.enchantments.EnchantmentSleipnirsGrace;
import uniqueeutils.enchantments.EnchantmentThickPick;

@SuppressWarnings("deprecation")
public class UtilsHandler
{
	public static UtilsHandler INSTANCE = new UtilsHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.side == Side.CLIENT || event.phase == Phase.START)
		{
			return;
		}
		EntityPlayer player = event.player;
		long time = player.world.getTotalWorldTime();
		if(player.isRiding() && time % 20 == 0)
		{
			Entity entity = player.getRidingEntity();
			if(entity instanceof EntityHorse)
			{
				EntityHorse horse = (EntityHorse)entity;
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.SLEIPNIRS_GRACE, entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(1));
				if(level > 0)
				{
					NBTTagCompound nbt = entity.getEntityData();
					long lastTime = nbt.getLong(EnchantmentSleipnirsGrace.HORSE_NBT);
					if(lastTime == 0)
					{
						nbt.setLong(EnchantmentSleipnirsGrace.HORSE_NBT, time);
						lastTime = time;
					}
					double maxTime = Math.min(EnchantmentSleipnirsGrace.CAP.getAsDouble(level) * 20, time - lastTime);
					IAttributeInstance attri = horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
					attri.removeModifier(EnchantmentSleipnirsGrace.SPEED_MOD);
					attri.applyModifier(new AttributeModifier(EnchantmentSleipnirsGrace.SPEED_MOD, "Sleipnirs Grace", Math.log10(1 + ((maxTime / EnchantmentSleipnirsGrace.LIMITER) * level)), 2));
				}
			}
		}
		NBTTagCompound nbt = player.getEntityData();
		if(nbt.hasKey(EnchantmentClimber.CLIMB_POS) && time >= nbt.getLong(EnchantmentClimber.CLIMB_START) + nbt.getLong(EnchantmentClimber.CLIMB_DELAY))
		{
			nbt.removeTag(EnchantmentClimber.CLIMB_DELAY);
			nbt.removeTag(EnchantmentClimber.CLIMB_START);
			BlockPos pos = BlockPos.fromLong(nbt.getLong(EnchantmentClimber.CLIMB_POS));
			nbt.removeTag(EnchantmentClimber.CLIMB_POS);
			player.setPositionAndUpdate(pos.getX()+0.5F, pos.getY(), pos.getZ() + 0.5F);
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.FAMINES_ODIUM, player);
		if(level > 0)
		{
			int duration = (int)(EnchantmentFaminesOdium.DELAY.get() * (1 - Math.log10(level)));
			if(time % duration == 0)
			{
				Int2FloatMap.Entry entry = EnchantmentFaminesOdium.consumeRandomItem(player.inventory, EnchantmentFaminesOdium.NURISHMENT.getFloat(level));
				if(entry != null)
				{
					player.getFoodStats().addStats(MathHelper.ceil(EnchantmentFaminesOdium.NURISHMENT.get(entry.getIntKey() * Math.log(2.8D+level*0.0625D))), (float)(entry.getFloatValue() * level * Math.log(2.8D+level*0.0625D)));
		            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
				}
				else
				{
					player.attackEntityFrom(DamageSource.MAGIC, EnchantmentFaminesOdium.DAMAGE.getFloat(duration * (float)Math.log(2.8D+level*0.0625D)));
				}
			}
		}
		int delay = Math.max(1, MathHelper.ceil(EnchantmentDemetersSoul.DELAY.get() / Math.log(10+EnchantmentDemetersSoul.SCALING.get(MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.DEMETERS_SOUL, player.getHeldItem(EnumHand.MAIN_HAND))))));
		if(player.world.getTotalWorldTime() % delay == 0)
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
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.PHANES_REGRET, event.getEntityLiving());
		if(level > 0)
		{
			double chance = EnchantmentPhanesRegret.CHANCE.get(Math.log(2.8D + Math.pow(level, 3)));
			if(chance > 1D)
			{
				event.getEntityLiving().attackEntityFrom(DamageSource.STARVE, event.getAmount());
				event.setCanceled(true);
				return;
			}
			if(event.getEntity().getEntityWorld().rand.nextDouble() < chance)
			{
				event.setCanceled(true);
				return;
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getEntityPlayer().isSneaking())
		{
			IBlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock().isLadder(state, event.getWorld(), event.getPos(), event.getEntityLiving()))
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.CLIMBER, event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.LEGS));
				if(level > 0)
				{
					MutableBlockPos pos = new MutableBlockPos(event.getPos());
					List<Block> blocks = new ObjectArrayList<>();
					do 
					{
						pos.move(EnumFacing.UP);
						state = event.getWorld().getBlockState(pos);
						blocks.add(state.getBlock());
					}
					while(state.getBlock().isLadder(state, event.getWorld(), pos, event.getEntityLiving()));
					if(!event.getWorld().getBlockState(pos).isBlockNormalCube() && !event.getWorld().getBlockState(pos.up()).isBlockNormalCube())
					{
						NBTTagCompound nbt = event.getEntityPlayer().getEntityData();
						nbt.setLong(EnchantmentClimber.CLIMB_POS, pos.toLong());
						nbt.setInteger(EnchantmentClimber.CLIMB_DELAY, EnchantmentClimber.getClimbTime(level, blocks));
						nbt.setLong(EnchantmentClimber.CLIMB_START, event.getWorld().getTotalWorldTime());
						event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.climb.start.name"), true);
					}
					else
					{
						event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqueeutil.climb.fail.name"), true);
					}
				}
			}
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.DEMETERS_SOUL, event.getItemStack());
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
					if(list.tagCount() >= EnchantmentDemetersSoul.CAP.get(level))
					{
						event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqee.crops.full.name"), false);
						event.setCancellationResult(EnumActionResult.SUCCESS);
						event.setCanceled(true);
						return;
					}
					list.appendTag(entry.save());
				}
				event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tooltip.uniqee.crops."+(found ? "removed" : "added")+".name"), false);
				event.setCancellationResult(EnumActionResult.SUCCESS);
				event.setCanceled(true);
				return;
			}
		}
		else
		{
			Object2IntMap<Enchantment> ench = MiscUtil.getEnchantments(event.getItemStack());
			if(ench.getInt(UniqueEnchantmentsUtils.DETEMERS_BLESSING) > 0)
			{
				IBlockState state = event.getWorld().getBlockState(event.getPos());
				if(state.getBlock() instanceof BlockCrops)
				{
					BlockCrops crops = (BlockCrops)state.getBlock();
					if(crops.isMaxAge(state))
					{
						state.getBlock().dropBlockAsItem(event.getWorld(), event.getPos(), state, ench.getInt(Enchantments.FORTUNE));
						event.getWorld().setBlockState(event.getPos(), crops.withAge(0));
						event.getItemStack().damageItem(1, event.getEntityLiving());
					}
				}
				else if(state.getBlock() == Blocks.DIRT || state.getBlock() == Blocks.GRASS_PATH || state.getBlock() == Blocks.GRASS)
				{
					event.getItemStack().attemptDamageItem(-ench.getInt(UniqueEnchantmentsUtils.DETEMERS_BLESSING), event.getWorld().rand, null);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeftHorse(EntityMountEvent event)
	{
		if(event.getEntityBeingMounted() instanceof EntityHorse)
		{
			EntityHorse horse = (EntityHorse)event.getEntityBeingMounted();
			horse.getEntityData().removeTag(EnchantmentSleipnirsGrace.HORSE_NBT);
			horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(EnchantmentSleipnirsGrace.SPEED_MOD);
		}
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
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.THICK_PICK, held);
		if(level > 0 && event.getState().getBlockHardness(player.world, event.getPos()) >= 20)
		{
			int amount = EntityEvents.getInt(held, EnchantmentThickPick.TAG, 0);
			if(amount > 0)
			{
				event.setNewSpeed(event.getNewSpeed() * EnchantmentThickPick.MINING_SPEED.getAsFloat(level));
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		EntityPlayer player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.THICK_PICK, held);
		if(level > 0)
		{
			int amount = EntityEvents.getInt(held, EnchantmentThickPick.TAG, 0);
			if(amount > 0)
			{
				EntityEvents.setInt(held, EnchantmentThickPick.TAG, amount-1);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemAirClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FIREWORKS && event.getEntityPlayer() != null && !event.getEntityPlayer().isElytraFlying())
		{
			ItemStack armor = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, armor);
			if(armor.getItem() instanceof ItemElytra && level > 0)
			{
                if(!event.getWorld().isRemote)
                {
                    event.getWorld().spawnEntity(new EntityFireworkRocket(event.getWorld(), stack, event.getEntityPlayer()));
                }
                else
                {
                	EntityPlayer player = event.getEntityPlayer();
                	event.getEntityPlayer().setPositionAndUpdate(player.posX, player.posY + 0.5D, player.posZ);
                }   
                try
				{
                	ReflectionHelper.findMethod(Entity.class, "setFlag", "func_70052_a", int.class, boolean.class).invoke(event.getEntityPlayer(), 7, true);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}	
			}
		}
	}
	
	@SubscribeEvent
	public void onEntitySpawn(EntityJoinWorldEvent event)
	{
		if(event.getEntity() instanceof EntityFireworkRocket)
		{
			EntityFireworkRocket rocket = (EntityFireworkRocket)event.getEntity();
			try
			{
				EntityLivingBase entity = ObfuscationReflectionHelper.getPrivateValue(EntityFireworkRocket.class, rocket, "field_191513_e");
				if(entity != null)
				{
					ItemStack stack = entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
					if(stack.getItem() instanceof ItemElytra)
					{
						int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, stack);
						if(level > 0)
						{
							NBTTagCompound nbt = new NBTTagCompound();
							rocket.writeEntityToNBT(nbt);
							int time = nbt.getInteger("LifeTime");
							time += time * EnchantmentRocketMan.FLIGHT_TIME.getAsDouble(level) * Math.log(2.8 + (level/16D));
							nbt.setInteger("LifeTime", time);
							rocket.readEntityFromNBT(nbt);
						}
					}
				}
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
	
	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event)
	{
		if(event.getAmount() > 0F)
		{
			for(EntityPlayer player : event.getEntityLiving().getRecursivePassengersByType(EntityPlayer.class))
			{
				if(canBlockDamageSource(event.getSource(), player))
				{
					damageShield(event.getAmount(), player);
					event.setCanceled(true);
					return;
				}
			}
		}
	}
	
    public static void damageShield(float damage, EntityPlayer player)
    {
        if (damage >= 3.0F && player.getActiveItemStack().getItem().isShield(player.getActiveItemStack(), player))
        {
            ItemStack copyBeforeUse = player.getActiveItemStack().copy();
            int i = 1 + MathHelper.floor(damage);
            player.getActiveItemStack().damageItem(i, player);
            if (player.getActiveItemStack().isEmpty())
            {
                EnumHand enumhand = player.getActiveHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copyBeforeUse, enumhand);

                if (enumhand == EnumHand.MAIN_HAND)
                {
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                }
                else
                {
                    player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                player.resetActiveHand();
                player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }
    }
	
    public static boolean canBlockDamageSource(DamageSource damageSourceIn, EntityPlayer player)
    {
        if (!damageSourceIn.isUnblockable() && player.isActiveItemStackBlocking())
        {
        	if(MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.MOUNTING_AEGIS, player.getActiveItemStack()) <= 0) return false;
            Vec3d vec3d = damageSourceIn.getDamageLocation();

            if (vec3d != null)
            {
                Vec3d vec3d1 = player.getLook(1.0F);
                Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(player.posX, player.posY, player.posZ)).normalize();
                vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.dotProduct(vec3d1) < 0.0D)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
