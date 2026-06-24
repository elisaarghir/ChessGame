public class Pawn extends Piece {
    public Pawn(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new PawnMoveStrategy();
    }
    public char type() { return 'P'; }

    public boolean checkForCheck(Board board, Position kingPos) {
        int moveDirection = -1;
        if (this.color == Colors.WHITE) moveDirection = 1;
        if (checkThreat(kingPos, -1, moveDirection)) return true;
        if (checkThreat(kingPos, 1, moveDirection)) return true;
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