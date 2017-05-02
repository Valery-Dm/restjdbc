package dmv.spring.demo.rest.exceptionhandler;

/**
 * Error info collector that could be returned as JSON
 * @author dmv
 */
public class ErrorInfo {

    private final String url;
    private final String ex;

    /**
     * Create a new object with given details
     * @param url URL where the error happened
     * @param ex corresponding Exception
     */
    public ErrorInfo(String url, Exception ex) {
        this.url = url;
        this.ex = ex.getLocalizedMessage();
    }

    /**
     * Create a new object with given details
     * @param url URL where the error happened
     * @param exMsg corresponding Exception message
     */
    public ErrorInfo(String url, String exMsg) {
        this.url = url;
        this.ex = exMsg;
    }

	public String getUrl() {
		return url;
	}

	public String getEx() {
		return ex;
	}
}
