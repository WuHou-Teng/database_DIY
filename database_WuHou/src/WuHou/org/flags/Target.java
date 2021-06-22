package WuHou.org.flags;

import WuHou.org.attributes.ReWr;

public class Target extends Flag{

    public Target(String flag) {
        super(flag);
    }

    public Target(String flag, String author) {
        super(flag, author);
    }

    public Target(String flag, ReWr permission) {
        super(flag, permission);
    }

    public Target(String flag, int id) {
        super(flag, id);
    }

    public Target(String flag, int id, ReWr permission) {
        super(flag, id, permission);
    }

    public Target(String flag, int id, ReWr permission, String author) {
        super(flag, id, permission, author);
    }
}
