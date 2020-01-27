package exceptions;

public class ExitProgram extends Exception {

    private static final long serialVersionUID = -4794515064723413398L;

    public ExitProgram(String message) {
        super(message);
    }

}
