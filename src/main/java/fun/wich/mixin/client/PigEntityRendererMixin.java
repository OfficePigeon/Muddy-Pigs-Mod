package fun.wich.mixin.client;

import com.google.common.collect.Maps;
import fun.wich.MuddyPig;
import fun.wich.client.ModClient;
import fun.wich.client.MuddyPigFlowerFeatureRenderer;
import fun.wich.client.MuddyPigMudFeatureRenderer;
import fun.wich.client.MuddyPigRenderState;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.client.render.entity.model.ColdPigEntityModel;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PigVariant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(PigEntityRenderer.class)
public abstract class PigEntityRendererMixin extends MobEntityRenderer<PigEntity, PigEntityRenderState, PigEntityModel> {
	public PigEntityRendererMixin(EntityRendererFactory.Context context, PigEntityModel entityModel, float f) { super(context, entityModel, f); }
	@Inject(method="<init>", at=@At("TAIL"))
	private void MuddyPig_InjectModelFeatures(EntityRendererFactory.Context context, CallbackInfo ci) {
		this.addFeature(new MuddyPigMudFeatureRenderer(this, MuddyPig_CreateModelPairs(context)));
		this.addFeature(new MuddyPigFlowerFeatureRenderer(this, context.getEntityModels()));
	}
	@Inject(method="updateRenderState(Lnet/minecraft/entity/passive/PigEntity;Lnet/minecraft/client/render/entity/state/PigEntityRenderState;F)V", at=@At("TAIL"))
	private void MuddyPig_UpdateRenderState(PigEntity pigEntity, PigEntityRenderState pigEntityRenderState, float f, CallbackInfo ci) {
		if (pigEntity instanceof MuddyPig muddyPig) {
			if (pigEntityRenderState instanceof MuddyPigRenderState muddyPigRenderState) {
				muddyPigRenderState.MuddyPig_SetMud(muddyPig.MuddyPig_GetMud());
				muddyPigRenderState.MuddyPig_SetFlower(muddyPig.MuddyPig_GetFlower());
			}
		}
	}
	@Unique
	private static Map<PigVariant.Model, BabyModelPair<PigEntityModel>> MuddyPig_CreateModelPairs(EntityRendererFactory.Context context) {
		return Maps.newEnumMap(Map.of(
				PigVariant.Model.NORMAL,
				new BabyModelPair<>(new PigEntityModel(context.getPart(ModClient.MUDDY_PIG_MUD)), new PigEntityModel(context.getPart(ModClient.MUDDY_PIG_MUD_BABY))),
				PigVariant.Model.COLD,
				new BabyModelPair<>(new ColdPigEntityModel(context.getPart(ModClient.MUDDY_PIG_MUD_COLD)), new ColdPigEntityModel(context.getPart(ModClient.MUDDY_PIG_MUD_COLD_BABY)))
		));
	}
}
