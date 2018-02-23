package run.brief.beans;

/**
 * Created by coops on 30/12/14.
 */
public class Theme {
    public final String name;
    public final int portRdrawable;
    public final int landRdrawable;
    public Theme(String name, int portRdrawable, int landRdrawable) {
        this.name=name;
        this.portRdrawable=portRdrawable;
        this.landRdrawable=landRdrawable;
    }
}