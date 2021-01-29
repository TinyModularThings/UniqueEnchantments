package uniquee.handler.ai;

import java.lang.reflect.Method;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import uniquee.UniqueEnchantments;
import uniquee.utils.MiscUtil;

public class SpecialFindPlayerAI extends NearestAttackableTargetGoal<PlayerEntity>
{
	public static final Method TELEPORT_RANDOMLY = MiscUtil.findMethod(EndermanEntity.class, new String[]{"teleportRandomly", "func_70820_n"});
	public static final Method TELEPORT_TO = MiscUtil.findMethod(EndermanEntity.class, new String[]{"teleportToEntity", "func_70816_c"}, Entity.class);
	
	private final EndermanEntity enderman;
	private PlayerEntity player;
	private int aggroTime;
	private int teleportTime;
	private final EntityPredicate field_220791_m;
	private final EntityPredicate field_220792_n = (new EntityPredicate()).setLineOfSiteRequired();
	
	public SpecialFindPlayerAI(EndermanEntity p_i45842_1_)
	{
		super(p_i45842_1_, PlayerEntity.class, false);
		enderman = p_i45842_1_;
		field_220791_m = (new EntityPredicate()).setDistance(getTargetDistance()).setCustomPredicate((p_220790_1_) -> {
			return shouldAttack(p_i45842_1_, (PlayerEntity)p_220790_1_);
		});
	}
	
	public boolean shouldAttack(EndermanEntity source, PlayerEntity target)
	{
		ItemStack itemstack = target.inventory.armorInventory.get(3);
		if(itemstack.getItem() == Blocks.CARVED_PUMPKIN.asItem() || MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_EYES, itemstack) > 0)
		{
			return false;
		}
		else
		{
			Vector3d vec3d = target.getLook(1.0F).normalize();
			Vector3d vec3d1 = new Vector3d(source.getPosX() - target.getPosX(), source.getBoundingBox().minY + source.getEyeHeight() - (target.getPosY() + target.getEyeHeight()), source.getPosZ() - target.getPosZ());
			double d0 = vec3d1.length();
			vec3d1 = vec3d1.normalize();
			double d1 = vec3d.dotProduct(vec3d1);
			return d1 > 1.0D - 0.025D / d0 ? target.canEntityBeSeen(source) : false;
		}
	}
	
	/**
	 * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
	 * method as well.
	 */
	@Override
	public boolean shouldExecute()
	{
		player = enderman.world.getClosestPlayer(field_220791_m, enderman);
		return player != null;
	}
	
	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	@Override
	public void startExecuting()
	{
		aggroTime = 5;
		teleportTime = 0;
	}
	
	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	@Override
	public void resetTask()
	{
		player = null;
		super.resetTask();
	}
	
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	@Override
	public boolean shouldContinueExecuting()
	{
		if(player != null)
		{
			if(!shouldAttack(enderman, player))
			{
				return false;
			}
			else
			{
				enderman.faceEntity(player, 10.0F, 10.0F);
				return true;
			}
		}
		else
		{
			return nearestTarget != null && field_220792_n.canTarget(enderman, nearestTarget) ? true : super.shouldContinueExecuting();
		}
	}
	
	/**
	 * Keep ticking a continuous task that has already been started
	 */
	@Override
	public void tick()
	{
		if(player != null)
		{
			if(--aggroTime <= 0)
			{
				nearestTarget = player;
				player = null;
				super.startExecuting();
			}
		}
		else
		{
			if(nearestTarget != null && !enderman.isPassenger())
			{
				if(shouldAttack(enderman, (PlayerEntity)nearestTarget))
				{
					if(nearestTarget.getDistanceSq(enderman) < 16.0D)
					{
						try
						{
							TELEPORT_RANDOMLY.invoke(enderman, new Object[0]);
						}
						catch(Exception e)
						{
						}
					}
					
					teleportTime = 0;
				}
				else if(nearestTarget.getDistanceSq(enderman) > 256.0D && teleportTime++ >= 30 && teleportTo(nearestTarget))
				{
					teleportTime = 0;
				}
			}
			
			super.tick();
		}
	}
	
	public boolean teleportTo(LivingEntity nearestTarget)
	{
		try
		{
			return (boolean)TELEPORT_TO.invoke(enderman, nearestTarget);
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
