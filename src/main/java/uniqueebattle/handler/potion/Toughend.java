package uniqueebattle.handler.potion;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public class Toughend extends Effect
{
	public Toughend()
	{
		super(EffectType.BENEFICIAL, 0xFF99453A);
		setRegistryName("uniquebattle", "toughend");
	}
}