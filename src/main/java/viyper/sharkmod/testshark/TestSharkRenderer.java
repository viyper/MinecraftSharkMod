package viyper.sharkmod.testshark;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestSharkRenderer extends GeoEntityRenderer<TestSharkEntity> {
    public TestSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new TestSharkModel());
    }
}
