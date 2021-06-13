package WuHou.org.util;

public class NoExistTargetException extends Exception {

    public NoExistTargetException () {
        super();
    }

    public NoExistTargetException (String name) {
        super(name);
    }
}
