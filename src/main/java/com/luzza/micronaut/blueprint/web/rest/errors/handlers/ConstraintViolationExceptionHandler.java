package com.luzza.micronaut.blueprint.web.rest.errors.handlers;

import com.luzza.micronaut.blueprint.web.rest.errors.ErrorConstants;
import com.luzza.micronaut.blueprint.web.rest.errors.FieldErrorVM;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.hateoas.JsonError;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import io.micronaut.validation.exceptions.ValidationExceptionHandler;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ElementKind;
import javax.validation.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Replaces(ConstraintExceptionHandler.class)
@Singleton
public class ConstraintViolationExceptionHandler extends ProblemHandler implements ExceptionHandler<ConstraintViolationException, HttpResponse> {

    @Override
    public HttpResponse handle(HttpRequest request, ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();

        List<FieldErrorVM> fieldErrors = constraintViolations.stream()
            .map(f -> {
                String property = null;
                String parameter = null;
                Iterator<Path.Node> pathIterator = f.getPropertyPath().iterator();
                while (pathIterator.hasNext()) {
                    Path.Node node = pathIterator.next();
                    if (node.getKind() == ElementKind.PROPERTY && property == null) {
                        property = node.getName();
                    }
                    if (node.getKind() == ElementKind.PARAMETER && parameter == null) {
                        parameter = node.getName();
                    }
                }
                return new FieldErrorVM(parameter, property, f.getMessage());
            })
            .collect(Collectors.toList());

        Problem problem = Problem.builder()
            .withType(ErrorConstants.CONSTRAINT_VIOLATION_TYPE)
            .withTitle("Method argument not valid")
            .withStatus(Status.BAD_REQUEST)
            .with(MESSAGE_KEY, ErrorConstants.ERR_VALIDATION)
            .with(FIELD_ERRORS_KEY, fieldErrors)
            .build();

        return create(problem, request, exception);
    }
}
