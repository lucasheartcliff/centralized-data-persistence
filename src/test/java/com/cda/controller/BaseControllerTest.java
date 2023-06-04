package com.cda.controller;

import com.google.gson.Gson;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {
  protected final Gson gson = new Gson();
}
