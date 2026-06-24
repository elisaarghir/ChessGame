import java.util.List;

public interface MoveStrategy {
    List<Position> getPossibleMoves(Board board,
                                    Position from);
}