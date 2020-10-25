package io.sitoolkit.bt.infrastructure.web;

import java.util.Map;

public interface WebClient {

  String post(String url, Map<String, String> params);
}
