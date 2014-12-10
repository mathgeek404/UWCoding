
/* Don't forget to change this line to cs540.checkers.<username> */
package cs540.checkers.sahit;

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
public class AlphaBetaPlayer extends CheckersPlayer implements GradedCheckersPlayer
{
   /** The number of pruned subtrees for the most recent deepening iteration. */
   protected int pruneCount;
   protected int pruneCountFinal;
   private int lastPrunedNodeValue;
   protected int lastPrunedNodeValueFinal;
   private int lastMinimaxVal;
   protected Evaluator sbe;
   protected int sideFac;  //converts SBE value for given side

   public AlphaBetaPlayer(String name, int side)
   { 
      super(name, side);
      // Use SimpleEvaluator to score terminal nodes
      sbe = new SimpleEvaluator();
      sideFac=1-side*2;
   }

   public void calculateMove(int[] bs)
   {
      BoardState state = new BoardState(bs,side);
      int depth = 1;
      while (depth <= depthLimit) {
         pruneCount = 0;
         Move sol = maxValueStart(state,depth);
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
   }

   //minimax for root node; utilizes properties of root (no pruning, etc)
   //Slightly changes to return best move
   private Move maxValueStart(BoardState state, int depth) {
      //initialize values for root node
      int minimax = Integer.MIN_VALUE;
      int a = Integer.MIN_VALUE;
      int b = Integer.MAX_VALUE;
      List<Move> moves = state.getAllPossibleMoves();
      Move returnMove = null;
      //consider each child node, calculate minvals
      for (Move move: moves) {
         state.execute(bs, move);
         int childVal = minValue(state,a, b, depth-1);
         state.revert(bs,reverseStack);
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
   private int maxValue(BoardState state, int a, int b, int depth) {
      int minimax = Integer.MIN_VALUE;
      List<Move> moves = state.getAllPossibleMoves();
      int unvisitedTrees = moves.size(); //number of unvisited node which may be pruned

      //if end of search tree or no more children, return sbe
      if (depth == 0 || unvisitedTrees==0) {
         return sideFac*sbe.evaluate(state.D); //convert SBE to side's value
      }

      //otherwise, check children
      for (Move move: moves) {
         unvisitedTrees--; //visiting a tree
         //Execute, Revert used to make child configuration
         state.execute(move); 
         minimax = Math.max(minimax,minValue(bs,a, b, depth-1));
         state.revert();
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

   //min node evaluator. Most of the statements are similar to max node evaluator
   private int minValue(BoardState state, int a, int b, int depth) {
      int minimax = Integer.MAX_VALUE;
      List<Move> moves = state.getAllPossibleMoves();
      int unvisitedTrees = moves.size();
      //Terminal state evaluation
      if (depth==0 || unvisitedTrees==0) {
         return sideFac*sbe.evaluate(state.D);
      }
      //check mav values of children, prune if necessary
      for (Move move: moves) {
         unvisitedTrees--;
         state.execute(move); 
         minimax = Math.min(minimax,maxValue(bs,a, b, depth-1));
         state.revert(bs,reverseStack);
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
