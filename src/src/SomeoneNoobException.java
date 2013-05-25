package src;

@SuppressWarnings("serial")
public class SomeoneNoobException extends Exception{
    private final String name;
    private final String message;
    
    public SomeoneNoobException(String name, String message) {
        this.name = name;
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return name + "... what a movl!\n" + message;
    }
}
