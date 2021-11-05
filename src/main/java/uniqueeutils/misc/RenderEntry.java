package uniqueeutils.misc;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RenderEntry
{
	BlockPos pos;
	int color;
	int timeToLive;
	
	public RenderEntry(BlockPos pos, int color, int timeToLive)
	{
		this.pos = pos;
		this.color = color;
		this.timeToLive = timeToLive;
	}
	
	public boolean update()
	{
		timeToLive--;
		return timeToLive <= 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void render(ClippingHelper helper, BufferBuilder buffer)
	{
		renderColorQuad(buffer, pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1F, pos.getY()+1F, pos.getZ()+1F, ((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void renderColorQuad(BufferBuilder builder, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a)
	{
		builder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.pos(minX, minY, maxZ).color(r, g, b, a).endVertex();
	}
}
