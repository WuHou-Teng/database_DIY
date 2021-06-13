package Carole.com;

public class NoExistKeyException extends Exception{

    public NoExistKeyException () {
        super();
    }

    public NoExistKeyException (String name) {
        super(name);
    }
}
