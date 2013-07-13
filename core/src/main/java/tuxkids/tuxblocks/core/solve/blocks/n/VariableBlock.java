package tuxkids.tuxblocks.core.solve.blocks.n;

public class VariableBlock extends BaseBlock {

	private String name;
	
	public VariableBlock(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public String text() {
		return name;
	}

}
