package ch.halarious.core;

/**
 * Basisklasse für HAL-Resourcen, welche gleich die Referenz auf sich selbst beinhaltet
 * Created by surech on 18.01.14.
 */
public class HalBaseResource implements HalResource {
    @HalLink(name = "self")
    private String selfRef;

    public String getSelfRef() {
        return selfRef;
    }

    public void setSelfRef(String selfRef) {
        this.selfRef = selfRef;
    }
}
