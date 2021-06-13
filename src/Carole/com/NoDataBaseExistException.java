package Carole.com;

public class NoDataBaseExistException extends Exception {

    public NoDataBaseExistException() {
        super();
    }

    public NoDataBaseExistException(String name) {
        super(name);
    }
}
