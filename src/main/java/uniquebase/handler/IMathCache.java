package uniquebase.handler;

import java.util.function.IntToDoubleFunction;

public final class IMathCache
{
	public static final int CACHESIZE = 100000; //Roughly 10MB ram usage with all caches that we use if anyone wonders how big it is.
	public static final IMathCache SQRT = new IMathCache(CACHESIZE, Math::sqrt);
	public static final IMathCache POW2 = new IMathCache(CACHESIZE, IMathCache::pow2);
	public static final IMathCache POW3 = new IMathCache(CACHESIZE, IMathCache::pow3);
	public static final IMathCache POW5 = new IMathCache(CACHESIZE, IMathCache::pow5);
	public static final IMathCache POW_WEIRD = new IMathCache(CACHESIZE, IMathCache::powWeird);
	public static final IMathCache LOG = new IMathCache(CACHESIZE, Math::log);
	public static final IMathCache LOG_MAX = new IMathCache(CACHESIZE, IMathCache::logMaxLevel);
	public static final IMathCache LOG_ADD = new IMathCache(CACHESIZE, IMathCache::logAddLevel);
	public static final IMathCache LOG_ADD_MAX = new IMathCache(CACHESIZE, IMathCache::logAddMaxLevel);
	public static final IMathCache LOG_MUL_MAX = new IMathCache(CACHESIZE, IMathCache::logMulMaxLevel);
	public static final IMathCache LOG101 = new IMathCache(CACHESIZE, IMathCache::log101);
	public static final IMathCache LOG10 = new IMathCache(CACHESIZE, Math::log10);
	
	final double[] cache;
	final IntToDoubleFunction generator;
	
	public IMathCache(int size, IntToDoubleFunction generator)
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
	
	private static double pow2(int level) { return Math.pow(level, 2); }
	private static double pow3(int level) { return Math.pow(level, 3); }
	private static double pow5(int level) { return Math.pow(level, 5); }
	private static double powWeird(int level) { return Math.pow(1+ ((level * level) / 100), 1+(level/100)); }
	private static double logAddMaxLevel(int level) { return Math.log(2.8 + level * 0.0625D); }
	private static double logMulMaxLevel(int level) { return Math.log(2.8 * level * 0.0625D); }
	private static double logMaxLevel(int level) { return Math.log(2.8 * level); }
	private static double logAddLevel(int level) { return Math.log(2.8 + level); }
	private static double log101(int level) { return Math.log(level+1.1D); }
}