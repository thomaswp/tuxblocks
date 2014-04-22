
package tuxkids.tuxblocks.core.tutorial.gen;



/**
 * Teaches how to do basic isolation of x (x+8 = 5)
 * 
 */
public interface StarredTutorial1_Base {

    /**
     * The file name of this tutorial
     * 
     */
    final static String filename = "StarredTutorial1.json";
    /**
     * We'll want to drag both the 5 and the 8 to the right side, leaving x by itself.
     * 
     */
    final static String id_undoDrag = "id_undoDrag";
    /**
     * To make X by itself, we are going to have to drag one of the blocks to its opposite side.  Do you see which one?
     * 
     */
    final static String id_letsDrag = "id_letsDrag";
    /**
     * As you've seen before, you can drag blocks to try to get X by itself.  Do you see which block we should drag?
     * 
     */
    final static String id_letsDrag_alreadyTrained = "id_letsDrag_alreadyTrained";
    /**
     * Exactly! When we dragged the +8 from the left side to the right side,this was the same as subtracting 8 from both sides.
     * 
     */
    final static String id_dragged8 = "id_dragged8";
    /**
     * Not quite.  Similar to before, this dragging the 5 actually makes the equation more complicated, not less.
     * 
     */
    final static String id_dragged5_trainedEarlier = "id_dragged5_trainedEarlier";
    /**
     * Yes.  Now drag the other one.
     * 
     */
    final static String id_goodCorrectionDrag = "id_goodCorrectionDrag";
    /**
     * Not quite.  Dragging the 5 like that subtracts 5 from both sides, but, it leaves our X term more congested than earlier.
     * 
     */
    final static String id_dragged5_notTrainedEarlier = "id_dragged5_notTrainedEarlier";
    /**
     * Hmm...  We want both the 5 and the 8 on the right side.
     * 
     */
    final static String id_badCorrectionDrag = "id_badCorrectionDrag";
    /**
     * Now, this should like a problem you know how to solve.  Take it away!
     * 
     */
    final static String id_takeItFromHere = "id_takeItFromHere";
    /**
     * So, this clue isn't as straight-forward as the ones we've seen before.  The unknown number X isn't by itself yet.
     * 
     */
=======
>>>>>>> 6afdafd045913a19b7df8f3ccf2f1c336f62afbe
    final static String id_intro = "id_intro";

}
