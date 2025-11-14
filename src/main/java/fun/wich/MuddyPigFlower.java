package fun.wich;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public enum MuddyPigFlower {
	ORANGE_TULIP(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/flower/orange_tulip.png"), ()-> Items.ORANGE_TULIP),
	PINK_TULIP(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/flower/pink_tulip.png"), ()-> Items.PINK_TULIP),
	RED_TULIP(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/flower/red_tulip.png"), ()-> Items.RED_TULIP),
	WHITE_TULIP(Identifier.of(MuddyPigsMod.MOD_ID, "textures/entity/pig/flower/white_tulip.png"), ()-> Items.WHITE_TULIP);
	private final Identifier texture;
	public Identifier getTexture() { return this.texture; }
	private final Supplier<Item> item;
	public Item getItem() { return this.item.get(); }
	MuddyPigFlower(Identifier texture, Supplier<Item> item) {
		this.texture = texture;
		this.item = item;
	}
	public static MuddyPigFlower get(int index) {
		MuddyPigFlower[] values = MuddyPigFlower.values();
		return index < 0 || index >= values.length ? null : values[index];
	}
	public static MuddyPigFlower getRandom(PigEntity pig) {
		MuddyPigFlower[] values = MuddyPigFlower.values();
		return values[pig.getRandom().nextInt(values.length)];
	}
}
