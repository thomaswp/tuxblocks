package tuxkids.tuxblocks.core;

import static playn.core.PlayN.*;

import java.util.List;

import playn.core.CanvasImage;
import playn.core.Color;
import playn.core.Game;
import playn.core.GroupLayer;
import playn.core.Image;
import playn.core.ImageLayer;
import tuxkids.tuxblocks.core.blocks.Block;
import tuxkids.tuxblocks.core.blocks.VariableBlock;
import tuxkids.tuxblocks.core.eqn.Addition;
import tuxkids.tuxblocks.core.eqn.Expression;
import tuxkids.tuxblocks.core.eqn.Multiplication;
import tuxkids.tuxblocks.core.eqn.Number;
import tuxkids.tuxblocks.core.eqn.Plus;
import tuxkids.tuxblocks.core.eqn.Variable;

public class TuxBlocksGame extends Game.Default {

  public TuxBlocksGame() {
    super(33); // call update every 33ms (30 times per second)
  }

  @Override
  public void init() {
    // create and add background image layer
    CanvasImage background = graphics().createImage(graphics().width(), graphics().height());
    background.canvas().setFillColor(Color.rgb(255, 255, 255));
    background.canvas().fillRect(0, 0, graphics().width() / 2, graphics().height());
    background.canvas().setFillColor(Color.rgb(100, 100, 100));
    background.canvas().fillRect(graphics().width() / 2, 0, graphics().width() / 2, graphics().height());
    graphics().rootLayer().add(graphics().createImageLayer(background));
    
    Expression e = new Variable("x").plus(5).times(3).times(4).plus(2).over(2);
    System.out.println(e.toMathString());

    Block block = Block.createBlock(e);
    graphics().rootLayer().add(block.getSprite());
  }

  @Override
  public void update(int delta) {
  }

  @Override
  public void paint(float alpha) {
    // the background automatically paints itself, so no need to do anything here!
  }
}
