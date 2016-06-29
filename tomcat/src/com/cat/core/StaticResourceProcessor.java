package com.cat.core;

import java.io.IOException;

import com.cat.core.http.HttpRequest;
import com.cat.core.http.HttpResponse;

public class StaticResourceProcessor {

  public void process(HttpRequest request, HttpResponse response) {
    try {
      response.sendStaticResource();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

}
