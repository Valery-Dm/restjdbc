package dmv.spring.demo.rest.exceptionhandler;

/**
 * Error info collector to be returned as JSON
 * @author dmv
 */
public class ErrorInfo {

    public final String url;
    public final String ex;

    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.ex = ex.getLocalizedMessage();
    }

    public ErrorInfo(String url, String exMsg) {
        this.url = url;
        this.ex = exMsg;
    }
}
