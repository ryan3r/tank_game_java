package state.board;

import rule.type.IMetaElement;
import state.board.floor.IFloor;
import state.board.floor.StandardFloor;
import state.board.unit.IUnit;
import state.board.unit.EmptyUnit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Board implements IMetaElement {

    private final IUnit[][] unitBoard;
    private final IFloor[][] floorBoard;

    private final int width;
    private final int height;

    public Board(int width, int height) {
        assert width > 0;
        assert height > 0;
        this.width = width;
        this.height = height;
        this.unitBoard = new IUnit[width][height];
        this.floorBoard = new IFloor[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                unitBoard[i][j] = new EmptyUnit(new Position(i, j));
                floorBoard[i][j] = new StandardFloor(new Position(i, j));
            }
        }
    }

    private boolean validPosition(Position p) {
        return (p.x() >= 0 && p.y() >= 0 && p.x() < width && p.y() < height);
    }


    private <T extends IPositioned> boolean putElementOnBoard(T[][] board, T element) {
        if (validPosition(element.getPosition())) {
            board[element.getPosition().y()][element.getPosition().x()] = element;
            return true;
        }
        return false;
    }

    private <T extends IPositioned> Optional<T> getElementOnBoard(T[][] board, Position p) {
        if (validPosition(p)) {
            return Optional.of(board[p.y()][p.x()]);
        }
        return Optional.empty();
    }

    public boolean putUnit(IUnit unit) {
        return putElementOnBoard(unitBoard, unit);
    }

    public Optional<IUnit> getUnit(Position p) {
        return getElementOnBoard(unitBoard, p);
    }

    public boolean putFloor(IFloor floor) {
        return putElementOnBoard(floorBoard, floor);
    }

    public Optional<IFloor> getFloor(Position p) {
        return getElementOnBoard(floorBoard, p);
    }


    public <T extends IUnit> List<T> gatherUnits(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < unitBoard.length; ++i) {
            for (int j = 0; j < unitBoard[0].length; ++j) {
                IUnit unit = unitBoard[i][j];
                try {
                    output.add(t.cast(unit));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }

    public <T extends IFloor> List<T> gatherFloors(Class<T> t) {
        List<T> output = new ArrayList<>();
        for (int i = 0; i < floorBoard.length; ++i) {
            for (int j = 0; j < floorBoard[0].length; ++j) {
                IFloor floor = floorBoard[i][j];
                try {
                    output.add(t.cast(floor));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }

    public List<?> gather(Class<?> t) {
        // Handle the caller asking for a position
        try {
            t.cast(new Position(0, 0));
            List<Position> positions = new ArrayList<>();
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    positions.add(new Position(i, j));
                }
            }
            return positions; // T extends Position iff t.cast(zero) succeeds
        } catch (Exception ignored) {}

        List output = new ArrayList<>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                Position p = new Position(i, j);
                try {
                    output.add(t.cast(getUnit(p).orElse(null)));
                } catch (Exception ignored) {}
                try {
                    output.add(t.cast(getFloor(p).orElse(null)));
                } catch (Exception ignored) {}
            }
        }
        return output;
    }


    private static <T extends IElement> String toGridString(T[][] board) {

        int pad = (int) Math.log10(board.length) + 1;

        StringBuilder sb = new StringBuilder();

        sb.repeat(' ', 2*pad);

        for (int i = 0; i < board.length; ++i) {
            sb.append((char)('A' + i)).append(' ');
        }

        sb.append("\n").repeat(' ', pad).append("+-");

        sb.repeat("--", board.length);

        sb.append('\n');

        for (int i = 0; i < board.length; ++i) {
            String paddedNumber = String.format(("%1$" + pad + "s"), (i + 1));
            sb.append(paddedNumber).append("| ");
            for (int j = 0; j < board[0].length; ++j) {
                sb.append(board[i][j].toBoardCharacter()).append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public String toUnitString() {
        return toGridString(unitBoard);
    }

    public String toFloorString() {
        return toGridString(floorBoard);
    }

}
