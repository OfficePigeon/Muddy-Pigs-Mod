package fun.wich.client;

import fun.wich.MuddyPigMud;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelPair;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.client.render.entity.state.PigEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.PigVariant;

import java.util.Map;

@Environment(EnvType.CLIENT)
public class MuddyPigMudFeatureRenderer extends FeatureRenderer<PigEntityRenderState, PigEntityModel> {
	protected final Map<PigVariant.Model, BabyModelPair<PigEntityModel>> modelPairs;
	public MuddyPigMudFeatureRenderer(FeatureRendererContext<PigEntityRenderState, PigEntityModel> context, Map<PigVariant.Model, BabyModelPair<PigEntityModel>> modelPairs) {
		super(context);
		this.modelPairs = modelPairs;
	}
	@Override
	public void render(MatrixStack matrices, OrderedRenderCommandQueue queue, int light, PigEntityRenderState state, float limbAngle, float limbDistance) {
		if (state.invisible) return;
		if (!(state instanceof MuddyPigRenderState muddyPig)) return;
		MuddyPigMud mud = muddyPig.MuddyPig_GetMud();
		if (mud == MuddyPigMud.CLEAN) return;
		PigEntityModel model = this.modelPairs.get(state.variant != null ? state.variant.modelAndTexture().model() : PigVariant.Model.NORMAL).get(state.baby);
		queue.getBatchingQueue(1).submitModel(model, state, matrices, RenderLayer.getEntityCutout(mud.getTexture()), light, LivingEntityRenderer.getOverlay(state, 0), -1, null, state.outlineColor, null);
	}
}
