package uniquee.client;

import com.mojang.blaze3d.matrix.MatrixStack;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.simple.TreasurersEyesEnchantment;
import uniquee.utils.MiscUtil;

public class EnchantmentLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> 
{
	public EnchantmentLayer(IEntityRenderer<T, M> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	public boolean canSeeEffects(LivingEntity base)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		int level = MiscUtil.getEnchantmentLevel(UniqueEnchantments.TREASURERS_EYES, player.getItemStackFromSlot(EquipmentSlotType.HEAD));
		if(level > 0)
		{
			double maxDistance = TreasurersEyesEnchantment.RANGE.getAsDouble(level);
			maxDistance *= maxDistance;
			return player.getDistanceSq(base) <= maxDistance;
		}
		return true;
	}
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(entity.getPersistentData().getLong("effectTimer") == entity.world.getGameTime())
		{
			return;
		}
		long time = entity.world.getGameTime();
		entity.getPersistentData().putLong("effectTimer", time);
		if(!canSeeEffects(entity))
		{
			return;
		}
		if(time % 14 == 0 && MiscUtil.getEnchantmentLevel(UniqueEnchantments.ARES_BLESSING, entity.getItemStackFromSlot(EquipmentSlotType.CHEST)) > 0)
		{
			spawnParticle(ParticleTypes.HEART, entity.world, entity.getBoundingBox(), 0F, 0.8F, 0F);
		}
		if(time % 2 == 0)
		{
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(entity.getHeldItemMainhand());
			if(enchantments.getInt(UniqueEnchantments.BERSERKER) > 0)
			{
				spawnParticle(new RedstoneParticleData(1F, 0F, 0F, 1F), entity.world, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UniqueEnchantments.PERPETUAL_STRIKE) > 0)
			{
				spawnParticle(new RedstoneParticleData(0F, 0F, 1F, 1F), entity.world, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UniqueEnchantments.SPARTAN_WEAPON) > 0)
			{
				spawnParticle(new RedstoneParticleData(0F, 1F, 0F, 1F), entity.world, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UniqueEnchantments.ALCHEMISTS_GRACE) > 0)
			{
				int value = (int)(time % 90);
				spawnParticle(new RedstoneParticleData(value >= 0 && value < 30 ? 1F : 0F, value >= 30 && value < 60 ? 1F : 0F, value >= 60 && value < 90 ? 1F : 0F, 1F), entity.world, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UniqueEnchantments.PHOENIX_BLESSING) > 0)
			{
				AxisAlignedBB box = entity.getBoundingBox();
				float width = (float)(box.maxX - box.minX);
				float debth = (float)(box.maxZ - box.minZ);
				float xOffset = (width * entity.world.rand.nextFloat()) - (width / 2);
				float zOffset = (debth * entity.world.rand.nextFloat()) - (debth / 2);
				entity.world.addOptionalParticle(ParticleTypes.FLAME, entity.getPosX() + xOffset, entity.getPosY(), entity.getPosZ() + zOffset, 0F, 0F, 0F);
			}
		}
	}
	
	protected void spawnParticle(IParticleData type, World world, AxisAlignedBB box, float xOff, float yOff, float zOff)
	{
		float width = (float)(box.maxX - box.minX);
		float height = (float)(box.maxY - box.minY);
		float debth = (float)(box.maxZ - box.minZ);
		float xOffset = (width * world.rand.nextFloat());
		float yOffset = (height * world.rand.nextFloat());
		float zOffset = (debth * world.rand.nextFloat());
		((ClientWorld)world).addOptionalParticle(type, (float)box.minX + xOffset + xOff, (float)box.minY + yOffset + yOff, (float)box.minZ + zOffset + zOff, 0F, 0F, 0F);
	}
}
