/**
 * 
 */
package co.ppk.exception;

import org.springframework.validation.BindingResult;

/**
 * This exception is thrown when spring validator is invoked explicitly and there are some binding errors.
 * It is caught by controller advice and error response is generated
 */
public class PpkFieldValidationException extends RootException {
    private BindingResult bindingResult;

    public PpkFieldValidationException(BindingResult bindingResult) {
        this.bindingResult = bindingResult;
    }

    public BindingResult getBindingResult() {
        return bindingResult;
    }

	@Override
	public int getCode() {
		// TODO Auto-generated method stub
		return 0;
	}
}