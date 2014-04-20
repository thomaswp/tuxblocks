package tuxkids.tuxblocks.core.story;

/**
 * Sometimes, we want to know if some topic (usually GUI related) has been covered in another tutorial. 
 * StoryGameState has a map of keys and booleans, which allows state to be maintained.
 */
public interface StoryGameStateKeys {
	/**
     * Has the tutoring system given coaching on what dragging is, and that x needs
     * to be isolated?
     * 
     */
    final static String HCOED = "Has Coached on Extra Dragging";
    
    /**
     * Has the student placed a blocking tower and been told the in-game reason 
     * for no blocking?
     * 
     */
    final static String HPBT = "Has Placed Blocking Tower";
    
    /**
     * Has the UI taught what pushing the start round button does?
     * 
     */
    final static String TSRB = "Taught Start Round Button";

    /**
     * Has Tutorial2ExplainingStarred executed?
     */
	final static String HESP = "Has Explained Starred Problems";

}
