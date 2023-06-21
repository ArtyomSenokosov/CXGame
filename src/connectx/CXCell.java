package connectx;

public class CXCell {

    /**
     * Cell row index
     */
    public final int i;
    /**
     * Cell column index
     */
    public final int j;
    /**
     * Cell state
     */
    public final CXCellState state;

    /**
     * Allocates a cell
     *
     * @param i     cell row index
     * @param j     cell column index
     * @param state cell state
     */
    public CXCell(int i, int j, CXCellState state) {
        this.i = i;
        this.j = j;
        this.state = state;
    }
}
