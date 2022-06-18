package uniquebase.handler;

import java.util.function.IntToDoubleFunction;

public final class MathCache
{
	public static final int CACHESIZE = 100000; //Roughly 10MB ram usage with all caches that we use if anyone wonders how big it is.
	public static final MathCache POW3 = new MathCache(CACHESIZE, MathCache::pow3);
	public static final MathCache POW5 = new MathCache(CACHESIZE, MathCache::pow5);
	public static final MathCache POW_WEIRD = new MathCache(CACHESIZE, MathCache::powWeird);
	public static final MathCache LOG = new MathCache(CACHESIZE, Math::log);
	public static final MathCache LOG_MAX = new MathCache(CACHESIZE, MathCache::logMaxLevel);
	public static final MathCache LOG_ADD = new MathCache(CACHESIZE, MathCache::logAddLevel);
	public static final MathCache LOG_ADD_MAX = new MathCache(CACHESIZE, MathCache::logAddMaxLevel);
	public static final MathCache LOG_MUL_MAX = new MathCache(CACHESIZE, MathCache::logMulMaxLevel);
	public static final MathCache LOG101 = new MathCache(CACHESIZE, MathCache::log101);
	public static final MathCache LOG10 = new MathCache(CACHESIZE, Math::log10);
	public static final MathCache SQRT_SPECIAL = new MathCache(CACHESIZE, MathCache::sqrtSpecial);
	public static final MathCache SQRT_EXTRA_SPECIAL = new MathCache(CACHESIZE, MathCache::sqrtExtraSpecial);
	
	final double[] cache;
	final IntToDoubleFunction generator;
	
	public MathCache(int size, IntToDoubleFunction generator)
	{
		this.generator = generator;
		cache = new double[size];
		for(int i = 0;i<size;i++) cache[i] = generator.applyAsDouble(i);
	}
	
	public double get(int level)
	{
		return level >= cache.length ? generator.applyAsDouble(level) : cache[level];
	}
	
	public float getFloat(int level)
	{
		return (float)get(level);
	}
	
	public int getInt(int level)
	{
		return (int)get(level);
	}
	
	private static double pow3(int level) { return Math.pow(level, 3); }
	private static double pow5(int level) { return Math.pow(level, 5); }
	private static double powWeird(int level) { return Math.pow(1+ ((level * level) / 100), 1+(level/100)); }
	private static double logAddMaxLevel(int level) { return Math.log(2.8 + level * 0.0625D); }
	private static double logMulMaxLevel(int level) { return Math.log(2.8 * level * 0.0625D); }
	private static double logMaxLevel(int level) { return Math.log(2.8 * level); }
	private static double logAddLevel(int level) { return Math.log(2.8 + level); }
	private static double log101(int level) { return Math.log(level+1.1D); }
	private static double sqrtSpecial(int level) { return (-0.5D + Math.sqrt(0.25D + level))*0.01D;}
	private static double sqrtExtraSpecial(int level) { return Math.sqrt(-0.5D + Math.sqrt(0.25D + level));}
	
	public static double dynamicLog(int input, int level)
	{
		return LOG10.get(input) / LOG10.get(level);
	}
}