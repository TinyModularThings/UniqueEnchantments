package uniqueebattle.handler.potion;

import net.minecraft.potion.Potion;

public class Bleed extends Potion
{
	public Bleed()
	{
		super(true, 0xFFFF0000);
		setRegistryName("bleed");
	}
}
