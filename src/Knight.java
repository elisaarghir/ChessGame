public class Knight extends Piece {
    public Knight(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new KnightMoveStrategy();
    }
    public char type() { return 'N'; }

    public boolean checkForCheck(Board board, Position kingPos) {
        if (checkThreat(kingPos, 1, 2)) return true;
        if (checkThreat(kingPos, 2, 1)) return true;
        if (checkThreat(kingPos, 2, -1)) return true;
        if (checkThreat(kingPos, 1, -2)) return true;
        if (checkThreat(kingPos, -1, -2)) return true;
        if (checkThreat(kingPos, -2, -1)) return true;
        if (checkThreat(kingPos, -2, 1)) return true;
        if (checkThreat(kingPos, -1, 2)) return true;
        return false;
    }
    private boolean checkThreat(Position kingPos, int colOffset, int rowOffset) {
        char currentCol = position.getX();
        int currentRow = position.getY();
        char targetCol = (char) (currentCol + colOffset);
        int targetRow = currentRow + rowOffset;
        if (targetCol == kingPos.getX() && targetRow == kingPos.getY()) return true;
        return false;
    }
}