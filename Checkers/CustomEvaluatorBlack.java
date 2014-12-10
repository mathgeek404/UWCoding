package cs540.checkers.sahit;
import cs540.checkers.*;
import static cs540.checkers.CheckersConsts.*;

import java.util.*;

/**
 * This simplistic static board evaluator assigns points for material.  Each 
 * pawn remaining on the board contributes one point, and each remaining king 
 * remaining on the board contributes two points. 
 */
public class CustomEvaluatorBlack implements Evaluator
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
                case BLK_PAWN:
                    pawns[BLK]++;
                    if (i<=7) backPieces++;
                    if (i>9) {
                       if (bs[i-7]==BLK_PAWN) backup++;
                       if (bs[i-9]==BLK_PAWN) backup++;
                    }
                    if (i>23) advancing++;
                    break;
                case RED_PAWN:
                    pawns[RED] ++;
                    break;
                case BLK_KING:
                   kings[BLK]++;
                   if (i>9) {
                      if (bs[i-7]==BLK_PAWN) backup++;
                      if (bs[i-9]==BLK_PAWN) backup++;
                   }
                   break;
                case RED_KING:
                    kings[RED] ++;
                    break;
            }
        }

        if (pawns[RED]==0 && kings[RED]==0) {
           return Integer.MAX_VALUE;
        }
        else if (pawns[BLK]==0 && kings[BLK]==0) {
           return Integer.MIN_VALUE;
        }
        return 10 * (pawns[BLK] - pawns[RED]) + 
               20 * (kings[BLK] - kings[RED]) +
               2*(advancing) +
               2*backup +
               1*backPieces;
        
    }
}
