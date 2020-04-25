package enums;

/**
 * Enum for setting and getting Sizes.
 * @author jaris
 */
public enum DblSizes {
    PANEW (170.0),
    SAWCOMPH (43.0),
    SAWCOMPW (175.0),
    BIGCOMPW (130.0),
    SMLCOMPW (110.0),
    ANIMSIZE (638.0),
    SAWTXTW (574.0),
    TXTW (615.0),
    TXTH (510.0),
    STGW (800.0),
    STGH (560.0),
    BUTW (128.0),
    SAWBUTW (175.0),
    MCSAWBUTW (85.0),
    SMLBUTW (30.0),
    BIGBUTW (47.0);
    
    public double dblSize;
    
    DblSizes(double size) {
        this.dblSize = size;
    }
    
    public double getDblSize() {
        return this.dblSize;
    }
}
