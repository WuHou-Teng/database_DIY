package Carole.com;

public class NoExistTargetException extends Exception {

    public NoExistTargetException () {
        super();
    }

    public NoExistTargetException (String name) {
        super(name);
    }
}
