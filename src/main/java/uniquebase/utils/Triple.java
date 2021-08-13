package uniquebase.utils;

public class Triple<K, V, T>
{
	K key;
	V value;
	T extra;
	
	public Triple(K key, V value, T extra)
	{
		this.key = key;
		this.value = value;
		this.extra = extra;
	}
	
	public T getExtra()
	{
		return extra;
	}
	
	public K getKey()
	{
		return key;
	}
	
	public V getValue()
	{
		return value;
	}
	
	public static <K, V, T> Triple<K, V, T> create(K key, V value, T extra)
	{
		return new Triple<K, V, T>(key, value, extra);
	}
}
