package tuxkids.tuxblocks.core.solve.blocks.n;

import playn.core.Image;
import playn.core.PlayN;
import playn.core.Pointer.Event;
import playn.core.Pointer.Listener;
import tripleplay.game.ScreenStack;
import tuxkids.tuxblocks.core.GameState;
import tuxkids.tuxblocks.core.layers.NinepatchLayer;
import tuxkids.tuxblocks.core.screen.GameScreen;

public class SolveScene extends GameScreen {

	public SolveScene(ScreenStack screens, GameState state) {
		super(screens, state);
	}
	
	@Override
	public void wasAdded() {
//		BaseBlock block = new VariableBlock("x");
//		block.addModifier(new PlusBlock(5));
//		block.addModifier(new MinusBlock(2));
//		block.addModifier(new OverBlock(3));
//		block.addModifier(new PlusBlock(3));
//		
//		System.out.println(block.toMathString());
		
		Image image = PlayN.assets().getImage("dialogborder9.png");
		final NinepatchLayer layer = new NinepatchLayer(image);
		
		this.layer.add(layer.layerAddable());
		layer.layerAddable().setTranslation(200, 200);
		layer.setSize(100, 100);
		
		PlayN.pointer().setListener(new Listener() {
			
			@Override
			public void onPointerStart(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerEnd(Event event) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPointerDrag(Event event) {
				float dx = event.x() - layer.layerAddable().tx();
				float dy = event.y() - layer.layerAddable().ty();
				if (dx > 0 && dy > 0) {
					layer.setSize(dx, dy);
				}
			}
			
			@Override
			public void onPointerCancel(Event event) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
