package fun.wich;

import net.minecraft.entity.Entity;

public interface MuddyPig {
	MuddyPigMud MuddyPig_GetMud();
	void MuddyPig_SetMud(MuddyPigMud mud);
	MuddyPigFlower MuddyPig_GetFlower();
	void MuddyPig_SetFlower(MuddyPigFlower flower);
	static void Mudden(Entity entity) { if (entity instanceof MuddyPig muddyPig) muddyPig.MuddyPig_SetMud(MuddyPigMud.MUDDY); }
	static boolean IsDried(Entity entity) { return entity instanceof MuddyPig muddyPig && muddyPig.MuddyPig_GetMud() == MuddyPigMud.DRIED; }
}
