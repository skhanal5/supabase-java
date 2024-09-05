package com.skhanal5.constants;

import java.util.Map;

/**
 * Represents constant headers that may be passed in, in addition to the default authorization
 * headers.
 */
public class HeaderType {

  // This header is used when select() is invoked to get back data from the server
  public static Map<String, String> RETRIEVE_RESPONSE_VALUES =
      Map.of("Prefer", "return=representation");

  private HeaderType() {}
}
