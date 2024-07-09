package booking.common.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Long id) {
        super("Item Not Found " + id);
    }
}