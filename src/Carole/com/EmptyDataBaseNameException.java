package Carole.com;

public class EmptyDataBaseNameException extends Exception{

    public EmptyDataBaseNameException () {
        super();
    }

    public EmptyDataBaseNameException (String message) {
        super(message);
    }
}
