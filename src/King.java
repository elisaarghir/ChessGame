public class King extends Piece {
    public King(Colors color, Position position) {
        super(color, position);
        this.moveStrategy = new KingMoveStrategy();
    }
    public char type() { return 'K'; }

    public boolean checkForCheck(Board board, Position kingPos) {
        char x = position.getX();
        int y = position.getY();
        if (checkOtherKing((char)(x - 1), y + 1, kingPos)) return true;
        if (checkOtherKing(x, y + 1, kingPos)) return true;
        if (checkOtherKing((char)(x + 1), y + 1, kingPos)) return true;
        if (checkOtherKing((char)(x - 1), y, kingPos)) return true;
        if (checkOtherKing((char)(x + 1), y, kingPos)) return true;
        if (checkOtherKing((char)(x - 1), y - 1, kingPos)) return true;
        if (checkOtherKing(x, y - 1, kingPos)) return true;
        if (checkOtherKing((char)(x + 1), y - 1, kingPos)) return true;
        return false;
    }
    private boolean checkOtherKing(char x, int y, Position kingPos) {
        if (x == kingPos.getX() && y == kingPos.getY()) return true;
        return false;
    }
}