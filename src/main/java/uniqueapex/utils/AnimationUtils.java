package uniqueapex.utils;

import net.minecraft.util.math.MathHelper;

public class AnimationUtils
{
	public static float getRotation(int tick) {
		if(tick >= 200) return MathHelper.lerp(Math.min(Math.max(0F, tick-200F) / 20F, 1F), 16F, 28F);
		return MathHelper.lerp(Math.min(Math.max(0F, tick-40F) / 40F, 1F), 4F, 16F);
	}
	
	public static float getCloser(int tick) {
		if(tick <= 120) return 0F;
		if(tick >= 234) return (tick - 234) / 60F;
		return Math.min(2, (tick - 130) / 20F);
	}
	
	public static float getYOffset(int tick) {
		if(tick <= 40) return 0F;
		return Math.min(5.5F, cosInterpolate(0F, 5.5F, Math.min(1F, (tick-40) / 120F)));
	}
	
	public static float getMasterY(int tick) {
		if(tick <= 160) return 0F;
		if(tick >= 205) return easeOut(-3F, 40F, Math.min(1F, 1F - (tick-205) / 35F));
		return cosInterpolate(0F, 40F, Math.min(1F, (tick-160) / 40F));		
	}
	
	private static float cosInterpolate(float y1, float y2, float mu) {
		double mu2 = (1-Math.cos(mu*Math.PI))/2;
		return (float)(y1*(1-mu2)+y2*mu2);
	}
	
	private static float easeOut(float y1, float y2, float mu) {
		return MathHelper.lerp(mu >= 1F ? 1F : 1F - (float)Math.pow(2F, -10F * mu), y1, y2);
	}
}
