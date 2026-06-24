public class Rook extends Piece {
    public Rook(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new RookMoveStrategy();
    }
    public char type() { return 'R'; }

    public boolean checkForCheck(Board board, Position kingPos) {
        if (checkThreat(board, kingPos, 0, 1)) return true;
        if (checkThreat(board, kingPos, 0, -1)) return true;
        if (checkThreat(board, kingPos, 1, 0)) return true;
        if (checkThreat(board, kingPos, -1, 0)) return true;
        return false;
    }
    private boolean checkThreat(Board board, Position kingPos, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();
        boolean ok = true;
        boolean kingFound = false;
        while (ok) {
            currentCol = (char) (currentCol + colOffset);
            currentRow = currentRow + rowOffset;
            if (currentCol < 'A' || currentCol > 'H' || currentRow < 1 || currentRow > 8) {
                ok = false;
            } else {
                if (currentCol == kingPos.getX() && currentRow == kingPos.getY()) {
                    kingFound = true;
                    ok = false;
                } else if (board.getPieceAt(new Position(currentCol, currentRow)) != null) {
                    ok = false;
                }
            }
        }
        return kingFound;
    }
}