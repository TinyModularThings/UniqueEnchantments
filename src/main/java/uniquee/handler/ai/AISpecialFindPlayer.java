package uniquee.handler.ai;


import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import uniquee.UniqueEnchantments;
import uniquee.utils.MiscUtil;

@SuppressWarnings("deprecation")
public class AISpecialFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer>
{
	EntityEnderman enderman;
    private EntityPlayer player;
    private int aggroTime;
    private int teleportTime;
    
	public AISpecialFindPlayer(EntityEnderman enderman)
	{
		super(enderman, EntityPlayer.class, false);
		this.enderman = enderman;
	}
	
    private boolean shouldAttackPlayer(EntityPlayer player)
    {
        ItemStack itemstack = player.inventory.armorInventory.get(3);

        if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) || MiscUtil.getEnchantmentLevel(UniqueEnchantments.ENDER_EYES, itemstack) > 0)
        {
            return false;
        }
        else
        {
            Vec3d vec3d1 = new Vec3d(enderman.posX - player.posX, enderman.getEntityBoundingBox().minY + enderman.getEyeHeight() - (player.posY + player.getEyeHeight()), enderman.posZ - player.posZ);
            double d0 = vec3d1.lengthVector();
            double d1 = player.getLook(1.0F).normalize().dotProduct(vec3d1.normalize());
            return d1 > 1.0D - 0.025D / d0 ? player.canEntityBeSeen(enderman) : false;
        }
    }
	
    @Override
	public boolean shouldExecute()
    {
        double d0 = this.getTargetDistance();
        this.player = this.enderman.world.getNearestAttackablePlayer(this.enderman.posX, this.enderman.posY, this.enderman.posZ, d0, d0, null, new Predicate<EntityPlayer>()
        {
            @Override
			public boolean apply(@Nullable EntityPlayer p_apply_1_)
            {
                return p_apply_1_ != null && shouldAttackPlayer(p_apply_1_);
            }
        });
        return this.player != null;
    }
    
    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
	public void startExecuting()
    {
        this.aggroTime = 5;
        this.teleportTime = 0;
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
	public void resetTask()
    {
        this.player = null;
        super.resetTask();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
	public boolean shouldContinueExecuting()
    {
        if (this.player != null)
        {
            if (!this.shouldAttackPlayer(this.player))
            {
                return false;
            }
            else
            {
                this.enderman.faceEntity(this.player, 10.0F, 10.0F);
                return true;
            }
        }
        else
        {
            return this.targetEntity != null && this.targetEntity.isEntityAlive() ? true : super.shouldContinueExecuting();
        }
    }

    @Override
	public void updateTask()
    {
        if (this.player != null)
        {
            if (--this.aggroTime <= 0)
            {
                this.targetEntity = this.player;
                this.player = null;
                super.startExecuting();
            }
        }
        else
        {
            if (this.targetEntity != null)
            {
                if (this.shouldAttackPlayer(this.targetEntity))
                {
                    if (this.targetEntity.getDistanceSq(this.enderman) < 16.0D)
                    {
                    	try
						{
                        	ReflectionHelper.findMethod(EntityEnderman.class, "teleportRandomly", "func_70820_n").invoke(enderman);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
                    }

                    this.teleportTime = 0;
                }
                else if (this.targetEntity.getDistanceSq(this.enderman) > 256.0D && this.teleportTime++ >= 30 && teleportToTarget())
                {
                    this.teleportTime = 0;
                }
            }

            super.updateTask();
        }
    }
    
    protected boolean teleportToTarget()
    {
    	try
		{
			return (boolean)ReflectionHelper.findMethod(EntityEnderman.class, "teleportToEntity", "func_70816_c", Entity.class).invoke(enderman, targetEntity);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
    }
}
