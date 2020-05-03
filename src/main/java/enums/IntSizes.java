package enums;

/**
 * Enum for setting and getting Sizes.
 * @author jaris
 */
public enum IntSizes {
    BIGMRGN (85),
    MDMMRGN (43),
    SMLMRGN (10),
    BIGSIZE (765),
    MDMSIZE (680),
    SMLSIZE (553),
    DIFFW (450),
    DIFFH (808),
    SAWSIZE (650),
    SIZE (595),
    HEIGHT (800);
    
    public int intSize;
    
    IntSizes(int size) {
        this.intSize = size;
    }
    
    public int getIntSize() {
        return this.intSize;
    }
}