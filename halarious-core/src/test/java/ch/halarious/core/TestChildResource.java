package ch.halarious.core;

/**
 * Kind-Klasse für eine HAL-Ressource
 */
public class TestChildResource implements HalResource {
    private String name;

    private String prename;

    public TestChildResource(String name, String prename) {
        this.name = name;
        this.prename = prename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrename() {
        return prename;
    }

    public void setPrename(String prename) {
        this.prename = prename;
    }
}
