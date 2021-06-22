package WuHou.org.flags;

import WuHou.org.attributes.ReWr;

public class Key extends Flag{

    public Key(String flag) {
        super(flag);
    }

    public Key(String flag, String author) {
        super(flag, author);
    }

    public Key(String flag, ReWr permission) {
        super(flag, permission);
    }

    public Key(String flag, int id) {
        super(flag, id);
    }

    public Key(String flag, int id, ReWr permission) {
        super(flag, id, permission);
    }

    public Key(String flag, int id, ReWr permission, String author) {
        super(flag, id, permission, author);
    }
}

