
/* Don't forget to change this line to cs540.checkers.<username> */
package cs540.checkers.sahit;
import java.util.*;

import cs540.checkers.*;
import static cs540.checkers.CheckersConsts.*;

import java.util.*;

/*
 * This is a skeleton for an alpha beta checkers player. Please copy this file
 * into your own directory, i.e. src/<username>/, and change the package 
 * declaration at the top to read
 *     package cs540.checkers.<username>;
 * , where <username> is your cs department login.
 */
/** This is a skeleton for an alpha beta checkers player. */
public class SahitPlayer extends CheckersPlayer implements GradedCheckersPlayer
{
   /** The number of pruned subtrees for the most recent deepening iteration. */
   protected int pruneCount;
   protected int pruneCountFinal;
   private int lastPrunedNodeValue;
   protected int lastPrunedNodeValueFinal;
   private int lastMinimaxVal;
   protected Evaluator sbe;
   private int dLimit = depthLimit;

   public SahitPlayer(String name, int side)
   { 
      super(name, side);
      // Use SimpleEvaluator to score terminal nodes
      if (side == RED) {
         sbe = new CustomEvaluatorRed();
      }
      else {
         sbe = new CustomEvaluatorBlack();
      }
   }

   public void calculateMove(int[] bs)
   {
      int depth = 1;
      while (depth <= dLimit) {
         pruneCount = 0;
         Move sol = maxValueStart(bs,depth);
         setMove(sol);
         lastPrunedNodeValueFinal = lastPrunedNodeValue;
         pruneCountFinal = pruneCount;
         depth++;

         if (Utils.verbose) {
            System.out.println("Best Move:"+ sol.toString() +"," + lastMinimaxVal);
            System.out.println("Prune Count:" + pruneCountFinal);
            System.out.println("Last Prune Node Value:" + lastPrunedNodeValueFinal);
         }

      }
      
      dLimit=(int)(Math.ceil(dLimit*1.1));
   }

   //minimax for root node; utilizes properties of root (no pruning, etc)
   private Move maxValueStart(int[] bs, int depth) {
      //initialize values for root node
      int minimax = Integer.MIN_VALUE;
      int a = Integer.MIN_VALUE;
      int b = Integer.MAX_VALUE;
      List<Move> moves = Utils.getAllPossibleMoves(bs, side);
      
      
      Move returnMove = null;
      //consider each child node, calculate minvals
      for (Move move: moves) {
         Stack<Integer> reverseStack = Utils.execute(bs, move);
         int childVal = minValue(bs,a, b, depth-1);
         Utils.revert(bs,reverseStack);
         if (childVal > minimax) {
            returnMove = move;
            minimax = childVal; 
         }
         a = Math.max(a, minimax);
      }
      //return best move
      return returnMove;
   }

   //general max node evaluator
   private int maxValue(int[] bs, int a, int b, int depth) {
      int minimax = Integer.MIN_VALUE;
      List<Move> moves = Utils.getAllPossibleMoves(bs, side);
      int unvisitedTrees = moves.size(); //number of unvisited node which may be pruned
      //if end of search tree or no more children, return sbe
      if (depth == 0 || unvisitedTrees==0) {
         return sbe.evaluate(bs); //convert SBE to side's value
      }

      //otherwise, check children
      for (Move move: moves) {
         unvisitedTrees--; //visiting a tree
         Stack<Integer> reverseStack = Utils.execute(bs, move); 
         minimax = Math.max(minimax,minValue(bs,a, b, depth-1));
         Utils.revert(bs,reverseStack);
         if (minimax >= b) {
            pruneCount += unvisitedTrees;
            if (unvisitedTrees != 0) {
               lastPrunedNodeValue = minimax;
               }
            return minimax;
         }
         a = Math.max(a, minimax);
      }
      return minimax;
   }

   private int minValue(int[] bs, int a, int b, int depth) {
      int minimax = Integer.MAX_VALUE;
      List<Move> moves = Utils.getAllPossibleMoves(bs, side);
      int unvisitedTrees = moves.size();
      if (depth==0 || unvisitedTrees==0) {
         return sbe.evaluate(bs);
      }
      for (Move move: moves) {
         unvisitedTrees--;
         Stack<Integer> reverseStack = Utils.execute(bs, move); 
         minimax = Math.min(minimax,maxValue(bs,a, b, depth-1));
         Utils.revert(bs,reverseStack);
         if (minimax <= a) {
            pruneCount += unvisitedTrees;
            if (unvisitedTrees != 0) {lastPrunedNodeValue = minimax;}
            return minimax;
         }
         b = Math.min(b, minimax);
      }
      return minimax;
   }

   public int getPruneCount()
   {
      return pruneCountFinal;
   }

   public int getLastPrunedNodeScore() {
      return lastPrunedNodeValueFinal;

   }
}

/*
class MoveCompare<Move> implements Comparator<Move> {
   public int compare(Move a, Move b) {
      return a.size()-b.size();
      
   }
   public boolean equals() { return false;}

}
*/


