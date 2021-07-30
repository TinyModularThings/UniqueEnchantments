package uniquee.client;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.simple.EnchantmentTreasurersEyes;
import uniquee.utils.MiscUtil;

public class EnchantmentLayer implements LayerRenderer<EntityLivingBase>
{
	public boolean canSeeEffects(EntityLivingBase base)
	{
		EntityPlayer player = Minecraft.getMinecraft().player;
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.TREASURERS_EYES, player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
		if(level > 0)
		{
			double maxDistance = EnchantmentTreasurersEyes.RANGE.getAsDouble(level);
			maxDistance *= maxDistance;
			return player.getDistanceSq(base) <= maxDistance;
		}
		return false;
	}
	
	@Override
	public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		if(entity.getEntityData().getLong("effectTimer") == entity.world.getTotalWorldTime())
		{
			return;
		}
		long time = entity.world.getTotalWorldTime();
		entity.getEntityData().setLong("effectTimer", time);
		if(!canSeeEffects(entity))
		{
			return;
		}
		if(time % 14 == 0 && MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST)) > 0)
		{
			spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, entity.world, entity.getEntityBoundingBox(), 0F, 0F, 0F, 0F, 0.8F, 0F);
		}
		if(time % 2 == 0)
		{
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(entity.getHeldItemMainhand());
			if(enchantments.getInt(UniqueEnchantments.BERSERKER) > 0)
			{
				spawnParticle(EnumParticleTypes.REDSTONE, entity.world, entity.getEntityBoundingBox(), 1F, 0F, 0F, 0F, 0F, 0F);
			}
			if(enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE) > 0)
			{
				spawnParticle(EnumParticleTypes.REDSTONE, entity.world, entity.getEntityBoundingBox(), 0F, 0F, 1F, 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UniqueEnchantments.SPARTAN_WEAPON) > 0)
			{
				spawnParticle(EnumParticleTypes.REDSTONE, entity.world, entity.getEntityBoundingBox(), 0F, 1F, 0F, 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UniqueEnchantments.ALCHEMISTS_GRACE) > 0)
			{
				int value = (int)(time % 90);
				spawnParticle(EnumParticleTypes.REDSTONE, entity.world, entity.getEntityBoundingBox(), value >= 0 && value < 30 ? 1F : 0F, value >= 30 && value < 60 ? 1F : 0F, value >= 60 && value < 90 ? 1F : 0F, 0F, 0F, 0F);
			}
			if(enchantments.getInt(UniqueEnchantments.PHOENIX_BLESSING) > 0)
			{
				AxisAlignedBB box = entity.getEntityBoundingBox();
				float width = (float)(box.maxX - box.minX);
				float debth = (float)(box.maxZ - box.minZ);
				float xOffset = (width * entity.world.rand.nextFloat()) - (width / 2);
				float zOffset = (debth * entity.world.rand.nextFloat()) - (debth / 2);
				entity.world.spawnParticle(EnumParticleTypes.FLAME, entity.posX + xOffset, entity.posY, entity.posZ + zOffset, 0F, 0F, 0F);
			}
			
		}
	}
	
	
	protected void spawnParticles(EnumParticleTypes type, World world, AxisAlignedBB box, float r, float g, float b, float xOff, float yOff, float zOff, int count)
	{
		for(int i = 0;i<count;i++)
		{
			spawnParticle(type, world, box, r, g, b, xOff, yOff, zOff);
		}
	}
	
	protected void spawnParticle(EnumParticleTypes type, World world, AxisAlignedBB box, float r, float g, float b, float xOff, float yOff, float zOff)
	{
		float width = (float)(box.maxX - box.minX);
		float height = (float)(box.maxY - box.minY);
		float debth = (float)(box.maxZ - box.minZ);
		float xOffset = (width * world.rand.nextFloat());
		float yOffset = (height * world.rand.nextFloat());
		float zOffset = (debth * world.rand.nextFloat());
		Particle particle = Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(type.getParticleID(), (float)box.minX + xOffset + xOff, (float)box.minY + yOffset + yOff, (float)box.minZ + zOffset + zOff, 0F, 0F, 0F);
		if(particle != null)
		{
			particle.setRBGColorF(r, g, b);
		}
	}

	@Override
	public boolean shouldCombineTextures()
	{
		return false;
	}
	
}
