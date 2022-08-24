package uniquee.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import uniquebase.utils.MiscUtil;
import uniquee.UE;
import uniquee.enchantments.simple.TreasurersEyes;

public class EnchantmentLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> 
{
	public EnchantmentLayer(RenderLayerParent<T, M> entityRendererIn)
	{
		super(entityRendererIn);
	}
	
	public boolean canSeeEffects(LivingEntity base)
	{
		Player player = Minecraft.getInstance().player;
		if(player == base) return false;
		int level = MiscUtil.getEnchantmentLevel(UE.TREASURERS_EYES, player.getItemBySlot(EquipmentSlot.HEAD));
		if(level > 0)
		{
			double maxDistance = TreasurersEyes.RANGE.getAsDouble(level);
			maxDistance *= maxDistance;
			return player.distanceToSqr(base) <= maxDistance;
		}
		return false;
	}
	@Override
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(entity.getPersistentData().getLong("effectTimer") == entity.level.getGameTime()) return;
		long time = entity.level.getGameTime();
		entity.getPersistentData().putLong("effectTimer", time);
		if(!canSeeEffects(entity)) return;
		if(time % 14 == 0 && MiscUtil.getEnchantmentLevel(UE.ARES_BLESSING, entity.getItemBySlot(EquipmentSlot.CHEST)) > 0)
		{
			spawnParticle(ParticleTypes.ANGRY_VILLAGER, entity.level, entity.getBoundingBox(), 0F, 0.8F, 0F);
		}
		if(time % 2 == 0)
		{
			Object2IntMap<Enchantment> enchantments = MiscUtil.getEnchantments(entity.getMainHandItem());
			if(enchantments.getInt(UE.BERSERKER) > 0)
			{
				spawnParticle(new DustParticleOptions(Vector3f.XP, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UE.PERPETUAL_STRIKE) > 0)
			{
				spawnParticle(new DustParticleOptions(Vector3f.ZP, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UE.SPARTAN_WEAPON) > 0)
			{
				spawnParticle(new DustParticleOptions(Vector3f.YP, 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);				
			}
			if(enchantments.getInt(UE.ALCHEMISTS_GRACE) > 0)
			{
				int value = (int)(time % 90);
				spawnParticle(new DustParticleOptions(new Vector3f(value >= 0 && value < 30 ? 1F : 0F, value >= 30 && value < 60 ? 1F : 0F, value >= 60 && value < 90 ? 1F : 0F), 1F), entity.level, entity.getBoundingBox(), 0F, 0F, 0F);
			}
			if(enchantments.getInt(UE.PHOENIX_BLESSING) > 0)
			{
				AABB box = entity.getBoundingBox();
				float width = (float)(box.maxX - box.minX);
				float debth = (float)(box.maxZ - box.minZ);
				float xOffset = (width * entity.level.random.nextFloat()) - (width / 2);
				float zOffset = (debth * entity.level.random.nextFloat()) - (debth / 2);
				entity.level.addAlwaysVisibleParticle(ParticleTypes.FLAME, entity.getX() + xOffset, entity.getY(), entity.getZ() + zOffset, 0F, 0F, 0F);
			}
		}
	}
	
	protected void spawnParticle(ParticleOptions type, Level world, AABB box, float xOff, float yOff, float zOff)
	{
		float width = (float)(box.maxX - box.minX);
		float height = (float)(box.maxY - box.minY);
		float debth = (float)(box.maxZ - box.minZ);
		float xOffset = (width * world.random.nextFloat());
		float yOffset = (height * world.random.nextFloat());
		float zOffset = (debth * world.random.nextFloat());
		((ClientLevel)world).addAlwaysVisibleParticle(type, (float)box.minX + xOffset + xOff, (float)box.minY + yOffset + yOff, (float)box.minZ + zOffset + zOff, 0F, 0F, 0F);
	}
}
