package uniqueeutils.handler;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
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
import uniquee.handler.EntityEvents;
import uniquee.utils.MiscUtil;
import uniqueeutils.UniqueEnchantmentsUtils;
import uniqueeutils.enchantments.EnchantmentClimber;
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
			int duration = (int)(EnchantmentFaminesOdium.DELAY * (1 - Math.log10(level)));
			if(time % duration == 0)
			{
				Int2FloatMap.Entry entry = EnchantmentFaminesOdium.consumeRandomItem(player.inventory, EnchantmentFaminesOdium.NURISHMENT * level);
				if(entry != null)
				{
					player.getFoodStats().addStats(entry.getIntKey(), entry.getFloatValue());
		            player.world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
				}
				else
				{
					player.attackEntityFrom(DamageSource.MAGIC, EnchantmentFaminesOdium.DAMAGE * duration);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.PHANES_REGRET, event.getEntityLiving());
		if(level > 0 && event.getEntity().getEntityWorld().rand.nextDouble() < EnchantmentPhanesRegret.CHANCE * level)
		{
			event.setCanceled(true);
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
							time += time * EnchantmentRocketMan.FLIGHT_TIME.getAsDouble(level);
							nbt.setInteger("LifeTime", time);
							rocket.readEntityFromNBT(nbt);
						}
					}
				}
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
}
