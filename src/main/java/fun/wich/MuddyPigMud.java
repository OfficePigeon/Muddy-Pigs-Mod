package fun.wich;

import net.minecraft.util.Identifier;

public enum MuddyPigMud {
	CLEAN(null),
	DRIED(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/overlay_dried_mud.png")),
	MUDDY(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/overlay_mud.png"));
	private final Identifier texture;
	public Identifier getTexture() { return this.texture; }
	MuddyPigMud(Identifier texture) { this.texture = texture; }
	public static MuddyPigMud get(int index) {
		MuddyPigMud[] values = MuddyPigMud.values();
		return index < 0 || index >= values.length ? CLEAN : values[index];
	}
}
