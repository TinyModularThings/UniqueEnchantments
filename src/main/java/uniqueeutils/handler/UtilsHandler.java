package uniqueeutils.handler;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.FireworkRocketEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.items.CapabilityItemHandler;
import uniquee.handler.EntityEvents;
import uniquee.utils.MiscUtil;
import uniqueeutils.UniqueEnchantmentsUtils;
import uniqueeutils.enchantments.ClimberEnchantment;
import uniqueeutils.enchantments.FaminesOdiumEnchantment;
import uniqueeutils.enchantments.PhanesRegretEnchantment;
import uniqueeutils.enchantments.PoseidonsSoulEnchantment;
import uniqueeutils.enchantments.RocketManEnchantment;
import uniqueeutils.enchantments.SleipnirsGraceEnchantment;
import uniqueeutils.enchantments.ThickPickEnchantment;

public class UtilsHandler
{
	public static UtilsHandler INSTANCE = new UtilsHandler();
	
	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event)
	{
		if(event.side.isClient() || event.phase == Phase.START)
		{
			return;
		}
		PlayerEntity player = event.player;
		long time = player.world.getGameTime();
		if(player.isPassenger() && time % 20 == 0)
		{
			Entity entity = player.getRidingEntity();
			if(entity instanceof HorseEntity)
			{
				HorseEntity horse = (HorseEntity)entity;
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.SLEIPNIRS_GRACE, entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null).getStackInSlot(1));
				if(level > 0)
				{
					CompoundNBT nbt = entity.getPersistentData();
					long lastTime = nbt.getLong(SleipnirsGraceEnchantment.HORSE_NBT);
					if(lastTime == 0)
					{
						nbt.putLong(SleipnirsGraceEnchantment.HORSE_NBT, time);
						lastTime = time;
					}
					double maxTime = Math.min(SleipnirsGraceEnchantment.CAP.getAsDouble(level) * 20, time - lastTime);
					IAttributeInstance attri = horse.getAttributes().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
					attri.removeModifier(SleipnirsGraceEnchantment.SPEED_MOD);
					attri.applyModifier(new AttributeModifier(SleipnirsGraceEnchantment.SPEED_MOD, "Sleipnirs Grace", Math.log10(1 + ((maxTime / SleipnirsGraceEnchantment.LIMITER.get()) * level)), Operation.MULTIPLY_TOTAL));
				}
			}
		}
		CompoundNBT nbt = player.getPersistentData();
		if(nbt.contains(ClimberEnchantment.CLIMB_POS) && time >= nbt.getLong(ClimberEnchantment.CLIMB_START) + nbt.getLong(ClimberEnchantment.CLIMB_DELAY))
		{
			nbt.remove(ClimberEnchantment.CLIMB_DELAY);
			nbt.remove(ClimberEnchantment.CLIMB_START);
			BlockPos pos = BlockPos.fromLong(nbt.getLong(ClimberEnchantment.CLIMB_POS));
			nbt.remove(ClimberEnchantment.CLIMB_POS);
			player.setPositionAndUpdate(pos.getX()+0.5F, pos.getY(), pos.getZ() + 0.5F);
		}
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.FAMINES_ODIUM, player);
		if(level > 0)
		{
			int duration = (int)(FaminesOdiumEnchantment.DELAY.get() * (1 - Math.log10(level)));
			if(time % duration == 0)
			{
				Int2FloatMap.Entry entry = FaminesOdiumEnchantment.consumeRandomItem(player.inventory, FaminesOdiumEnchantment.NURISHMENT.getFloat() * level);
				if(entry != null)
				{
					player.getFoodStats().addStats(entry.getIntKey(), entry.getFloatValue());
		            player.world.playSound((PlayerEntity)null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, player.world.rand.nextFloat() * 0.1F + 0.9F);
				}
				else
				{
					player.attackEntityFrom(DamageSource.MAGIC, FaminesOdiumEnchantment.DAMAGE.getFloat() * duration);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onHeal(LivingHealEvent event)
	{
		int level = MiscUtil.getCombinedEnchantmentLevel(UniqueEnchantmentsUtils.PHANES_REGRET, event.getEntityLiving());
		if(level > 0 && event.getEntity().getEntityWorld().rand.nextDouble() < PhanesRegretEnchantment.CHANCE.get() * level)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onArrowHit(ProjectileImpactEvent.Arrow event)
	{
		if(!(event.getArrow() instanceof TridentEntity) || !(event.getArrow().getShooter() instanceof PlayerEntity)) return;
		RayTraceResult result = event.getRayTraceResult();
		if(!(result instanceof BlockRayTraceResult)) return;
		BlockRayTraceResult ray = (BlockRayTraceResult)result;
		World world = event.getEntity().getEntityWorld();
		BlockState state = world.getBlockState(ray.getPos());
		if(PoseidonsSoulEnchantment.isValid(state.getBlock()))
		{
			ItemStack arrowStack = EntityEvents.getArrowStack(event.getArrow());
			if(arrowStack.getItem() != Items.TRIDENT) return;
			Object2IntMap<Enchantment> enchs = MiscUtil.getEnchantments(arrowStack);
			int level = enchs.getInt(UniqueEnchantmentsUtils.POSEIDONS_SOUL);
			if(level <= 0) return;
			ItemStack stack = new ItemStack(Items.DIAMOND_PICKAXE);
			stack.addEnchantment(Enchantments.SILK_TOUCH, 1);
			Block.spawnAsEntity(world, ray.getPos(), new ItemStack(state.getBlock(), level * (world.rand.nextInt(enchs.getInt(Enchantments.FORTUNE)+1)+1)));
			MiscUtil.drainExperience((PlayerEntity)event.getArrow().getShooter(), (int)Math.log10(Math.pow(PoseidonsSoulEnchantment.BASE_CONSUMTION.get(), level)));
		}
	}
	
	@SubscribeEvent
	public void onBlockClick(RightClickBlock event)
	{
		if(event.getPlayer().isSneaking())
		{
			BlockState state = event.getWorld().getBlockState(event.getPos());
			if(state.getBlock().isLadder(state, event.getWorld(), event.getPos(), event.getEntityLiving()))
			{
				int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.CLIMBER, event.getPlayer().getItemStackFromSlot(EquipmentSlotType.LEGS));
				if(level > 0)
				{
					MutableBlockPos pos = new MutableBlockPos(event.getPos());
					List<Block> blocks = new ObjectArrayList<>();
					do 
					{
						pos.move(Direction.UP);
						state = event.getWorld().getBlockState(pos);
						blocks.add(state.getBlock());
					}
					while(state.getBlock().isLadder(state, event.getWorld(), pos, event.getEntityLiving()));
					if(!event.getWorld().getBlockState(pos).func_224756_o(event.getWorld(), pos) && !event.getWorld().getBlockState(pos.up()).func_224756_o(event.getWorld(), pos.up()))
					{
						CompoundNBT nbt = event.getPlayer().getPersistentData();
						nbt.putLong(ClimberEnchantment.CLIMB_POS, pos.toLong());
						nbt.putInt(ClimberEnchantment.CLIMB_DELAY, ClimberEnchantment.getClimbTime(level, blocks));
						nbt.putLong(ClimberEnchantment.CLIMB_START, event.getWorld().getGameTime());
						event.getPlayer().sendStatusMessage(new TranslationTextComponent("tooltip.uniqueutil.climb.start.name"), true);
					}
					else
					{
						event.getPlayer().sendStatusMessage(new TranslationTextComponent("tooltip.uniqueutil.climb.fail.name"), true);
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLeftHorse(EntityMountEvent event)
	{
		if(event.getEntityBeingMounted() instanceof HorseEntity)
		{
			HorseEntity horse = (HorseEntity)event.getEntityBeingMounted();
			horse.getPersistentData().remove(SleipnirsGraceEnchantment.HORSE_NBT);
			horse.getAttributes().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED).removeModifier(SleipnirsGraceEnchantment.SPEED_MOD);
		}
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event)
	{
		if(event.getPlayer() == null)
		{
			return;
		}
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.THICK_PICK, held);
		if(level > 0 && event.getState().getBlockHardness(player.world, event.getPos()) >= 20)
		{
			int amount = EntityEvents.getInt(held, ThickPickEnchantment.TAG, 0);
			if(amount > 0)
			{
				event.setNewSpeed(event.getNewSpeed() * ThickPickEnchantment.MINING_SPEED.getAsFloat(level));
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
		PlayerEntity player = event.getPlayer();
		ItemStack held = player.getHeldItemMainhand();
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.THICK_PICK, held);
		if(level > 0)
		{
			int amount = EntityEvents.getInt(held, ThickPickEnchantment.TAG, 0);
			if(amount > 0)
			{
				EntityEvents.setInt(held, ThickPickEnchantment.TAG, amount-1);
			}
		}
	}
	
	@SubscribeEvent
	public void onItemAirClick(RightClickItem event)
	{
		ItemStack stack = event.getItemStack();
		if(stack.getItem() == Items.FIREWORK_ROCKET && event.getPlayer() != null && !event.getPlayer().isElytraFlying())
		{
			ItemStack armor = event.getPlayer().getItemStackFromSlot(EquipmentSlotType.CHEST);
			int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, armor);
			if(armor.getItem() instanceof ElytraItem && level > 0)
			{
                if(!event.getWorld().isRemote)
                {
                    event.getWorld().addEntity(new FireworkRocketEntity(event.getWorld(), stack, event.getPlayer()));
                }
                else
                {
                	PlayerEntity player = event.getPlayer();
                	event.getPlayer().setPositionAndUpdate(player.posX, player.posY + 0.5D, player.posZ);
                }
                try
				{
                	MiscUtil.findMethod(Entity.class, new String[]{"setFlag", "func_70052_a"}, int.class, boolean.class).invoke(event.getPlayer(), 7, true);
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
		if(event.getEntity() instanceof FireworkRocketEntity)
		{
			FireworkRocketEntity rocket = (FireworkRocketEntity)event.getEntity();
			try
			{
				LivingEntity entity = ObfuscationReflectionHelper.getPrivateValue(FireworkRocketEntity.class, rocket, "field_191513_e");
				if(entity != null)
				{
					ItemStack stack = entity.getItemStackFromSlot(EquipmentSlotType.CHEST);
					if(stack.getItem() instanceof ElytraItem)
					{
						int level = MiscUtil.getEnchantmentLevel(UniqueEnchantmentsUtils.ROCKET_MAN, stack);
						if(level > 0)
						{
							CompoundNBT nbt = new CompoundNBT();
							rocket.writeAdditional(nbt);
							int time = nbt.getInt("LifeTime");
							time += time * RocketManEnchantment.FLIGHT_TIME.getAsDouble(level);
							nbt.putInt("LifeTime", time);
							rocket.readAdditional(nbt);
						}
					}
				}
			}
			catch(Exception e){e.printStackTrace();}
		}
	}
}
