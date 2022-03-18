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
import uniquebase.utils.MiscUtil;
import uniquee.UE;
import uniquee.enchantments.simple.TreasurersEyes;

public class EnchantmentLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> 
{
	public EnchantmentLayer(IEntityRenderer<T, M> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	public boolean canSeeEffects(LivingEntity base)
	{
		PlayerEntity player = Minecraft.getInstance().player;
		if(player == base) return false;
		int level = MiscUtil.getEnchantmentLevel(UE.TREASURERS_EYES, player.getItemBySlot(EquipmentSlotType.HEAD));
		if(level > 0)
		{
			double maxDistance = TreasurersEyes.RANGE.getAsDouble(level);
			maxDistance *= maxDistance;
			return player.distanceToSqr(base) <= maxDistance;
		}
		return false;
	}
	@Override
	public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(entity.getPersistentData().getLong("effectTimer") == entity.level.getGameTime()) return;
		long time = entity.level.getGameTime();
		entity.getPersistentData().putLong("effectTimer", time);
		if(!canSeeEffects(entity)) return;
		if(time % 14 == 0 && MiscUtil.getEnchantmentLevel(UE.ARES_BLESSING, entity.getItemBySlot(EquipmentSlotType.CHEST)) > 0)
		{
			spawnParticle(ParticleTypes.ANGRY_VILLAGER, entity.level, entity.getBoundingBox(), 0F, 0.8F, 0F);
		}
		if(time % 2 == 0)
		{
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(entity.getMainHandItem());
			if(enchantments.getInt(UE.BERSERKER) > 0)
			{
				spawnParticle(new RedstoneParticleData(1F, 0F, 0F, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UE.PERPETUAL_STRIKE) > 0)
			{
				spawnParticle(new RedstoneParticleData(0F, 0F, 1F, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UE.SPARTAN_WEAPON) > 0)
			{
				spawnParticle(new RedstoneParticleData(0F, 1F, 0F, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UE.ALCHEMISTS_GRACE) > 0)
			{
				int value = (int)(time % 90);
				spawnParticle(new RedstoneParticleData(value >= 0 && value < 30 ? 1F : 0F, value >= 30 && value < 60 ? 1F : 0F, value >= 60 && value < 90 ? 1F : 0F, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UE.PHOENIX_BLESSING) > 0)
			{
				AxisAlignedBB box = entity.getBoundingBox();
				float width = (float)(box.maxX - box.minX);
				float debth = (float)(box.maxZ - box.minZ);
				float xOffset = (width * entity.level.random.nextFloat()) - (width / 2);
				float zOffset = (debth * entity.level.random.nextFloat()) - (debth / 2);
				entity.level.addAlwaysVisibleParticle(ParticleTypes.FLAME, entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset, 0F, 0F, 0F);
			}
		}
	}
	
	protected void spawnParticle(IParticleData type, World world, AxisAlignedBB box, float xOff, float yOff, float zOff)
	{
		float width = (float)(box.maxX - box.minX);
		float height = (float)(box.maxY - box.minY);
		float debth = (float)(box.maxZ - box.minZ);
		float xOffset = (width * world.random.nextFloat());
		float yOffset = (height * world.random.nextFloat());
		float zOffset = (debth * world.random.nextFloat());
		((ClientWorld)world).addAlwaysVisibleParticle(type, (float)box.minX + xOffset + xOff, (float)box.minY + yOffset + yOff, (float)box.minZ + zOffset + zOff, 0F, 0F, 0F);
	}
}
