package uniquebase.utils;

@FunctionalInterface
public interface ObjIntFunction<T>
{
	public int apply(T t, int v);
}
