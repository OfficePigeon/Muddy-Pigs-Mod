package fun.wich.mixin.client;

import fun.wich.MuddyPigFlower;
import fun.wich.MuddyPigMud;
import fun.wich.client.MuddyPigRenderState;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PigEntityRenderState.class)
public class MuddyPigs_PigEntityRenderStateMixin implements MuddyPigRenderState {
	@Unique	private MuddyPigMud mud = MuddyPigMud.CLEAN;
	@Unique	private MuddyPigFlower flower = null;
	@Override public MuddyPigMud MuddyPig_GetMud() { return this.mud; }
	@Override public void MuddyPig_SetMud(MuddyPigMud mud) { this.mud = mud; }
	@Override public MuddyPigFlower MuddyPig_GetFlower() { return this.flower; }
	@Override public void MuddyPig_SetFlower(MuddyPigFlower flower) { this.flower = flower; }
}
