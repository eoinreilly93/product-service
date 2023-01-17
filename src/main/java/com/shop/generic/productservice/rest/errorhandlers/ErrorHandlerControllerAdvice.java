package com.shop.generic.productservice.rest.errorhandlers;

import com.shop.generic.common.rest.response.RestApiResponse;
import com.shop.generic.common.rest.response.RestApiResponseFactory;
import com.shop.generic.productservice.exceptions.ProductDoesNotExistException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandlerControllerAdvice {

    private final RestApiResponseFactory restApiResponseFactory;

    public ErrorHandlerControllerAdvice(final RestApiResponseFactory restApiResponseFactory) {
        this.restApiResponseFactory = restApiResponseFactory;
    }

    @ExceptionHandler(ProductDoesNotExistException.class)
    public ResponseEntity<RestApiResponse> handleProductDoesNotExistException(
            final ProductDoesNotExistException e) {
        return ResponseEntity.badRequest()
                .body(restApiResponseFactory.createErrorResponse(e.getMessage()));
    }

    /**
     * Catch all exception handler. In case any exceptions slip through the cracks, we want to
     * return a 500
     *
     * @param e the exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse> handleGeneralException(final Exception e) {
        return ResponseEntity.internalServerError()
                .body(restApiResponseFactory.createErrorResponse(e.getMessage()));
    }

}
