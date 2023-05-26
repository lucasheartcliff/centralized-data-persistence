package com.cda.controller;

import com.cda.RequestHandler;
import com.cda.persistence.DatabaseContext;
import com.cda.service.ServiceFactory;
import com.cda.utils.functional.ThrowableConsumer;
import com.cda.utils.functional.ThrowableFunction;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public abstract class BaseController {
  private final RequestHandler requestHandler;
  protected static final Gson gson = new Gson();

  protected BaseController(RequestHandler requestHandler) {
    this.requestHandler = requestHandler;
  }

  protected ResponseEntity<?> encapsulateRequest(
      ThrowableFunction<ServiceFactory, ResponseEntity<?>> function) {
    try (DatabaseContext databaseContext = requestHandler.buildDatabaseContext(); ) {
      ServiceFactory serviceFactory = requestHandler.buildServiceFactory(databaseContext);
      return function.apply(serviceFactory);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      return buildErrorResponse(e);
    }
  }

  protected void encapsulateRequest(ThrowableConsumer<ServiceFactory> function) {
    try (DatabaseContext databaseContext = requestHandler.buildDatabaseContext(); ) {
      ServiceFactory serviceFactory = requestHandler.buildServiceFactory(databaseContext);
      function.run(serviceFactory);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  protected ResponseEntity<?> buildOKResponse(Object rawBody) {
    return new ResponseEntity<>(gson.toJsonTree(rawBody), HttpStatus.OK);
  }

  protected ResponseEntity<?> buildOKResponse() {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  protected ResponseEntity<?> buildErrorResponse(Throwable e) {
    Map<String, String> body = new HashMap<>();
    body.put("message", e.getMessage());
    return new ResponseEntity<Map<String, String>>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
