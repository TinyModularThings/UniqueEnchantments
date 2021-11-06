package uniqueeutils.misc;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
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
	public void render(BufferBuilder buffer, Matrix4f matrix)
	{
		renderColorQuad(buffer, matrix, pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1F, pos.getY()+1F, pos.getZ()+1F, ((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void renderColorQuad(BufferBuilder builder, Matrix4f matrix, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a)
	{
		builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, minZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a).endVertex();
		builder.vertex(matrix, minX, minY, maxZ).color(r, g, b, a).endVertex();
	}
}
