package cs540.checkers.sahit;
import cs540.checkers.*;
import static cs540.checkers.CheckersConsts.*;

import java.util.*;

/**
 * This simplistic static board evaluator assigns points for material.  Each 
 * pawn remaining on the board contributes one point, and each remaining king 
 * remaining on the board contributes two points. 
 */
public class CustomEvaluatorRed implements Evaluator
{
    public int evaluate(int[] bs)
    {
        int[] pawns = new int[2],
            kings = new int[2] ;
        int backPieces = 0;
        int backup = 0;
        int advancing = 0;

        for (int i = 0; i < H * W; i++)
        {
            int v = bs[i];
            switch(v)
            {
                case RED_PAWN:
                    pawns[RED] ++;
                    if (i>=56) backPieces++;
                    if (i>9) {
                       if (bs[i-7]==RED_PAWN) backup++;
                       if (bs[i-9]==RED_PAWN) backup++;
                    }
                    if (i<56) advancing++;
                    break;
                case BLK_PAWN:
                    pawns[BLK] ++;
                    break;
                case RED_KING:
                   kings[RED]++;
                   if (i>9) {
                      if (bs[i-7]==RED_PAWN) backup++;
                      if (bs[i-9]==RED_PAWN) backup++;
                   }
                   break;
                case BLK_KING:
                    kings[BLK] ++;
                    break;
            }
        }

        if (pawns[RED]==0 && kings[RED]==0) {
           return Integer.MIN_VALUE;
        }
        else if (pawns[BLK]==0 && kings[BLK]==0) {
           return Integer.MAX_VALUE;
        }
        return 10 * (pawns[RED] - pawns[BLK]) + 
               20 * (kings[RED] - kings[BLK]) +
               2*(advancing) +
               3*backup +
               1*backPieces;
        
    }
}
