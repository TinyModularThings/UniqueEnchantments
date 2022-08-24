package uniquebase.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface IKeyBind
{
	public boolean test(Player player);
	public Component getName();
	public Component getKeyName();
	
	public static IKeyBind empty() {
		return EmptyKeyBind.INSTANCE;
	}
	
	static class EmptyKeyBind implements IKeyBind
	{
		static final IKeyBind INSTANCE = new EmptyKeyBind();
		
		@Override
		public boolean test(Player player) {
			return false;
		}

		@Override
		public Component getName() {
			return Component.literal("Unbound Keybinding");
		}

		@Override
		public Component getKeyName() {
			return Component.literal("Unknown");
		}
	}
}
