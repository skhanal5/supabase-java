package com.skhanal5.core.util;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

public class RequestBuilderHelper {

    public static URI buildURI(String baseURI, String path, Map<String, List<String>> queryParameters) {
        String fullURI = baseURI + "/" + path + "?" + serializeQueryParameters(queryParameters);
        return URI.create(fullURI);
    }

    private static StringJoiner serializeQueryParameters(Map<String, List<String>> queryParameters) {
        StringJoiner stringifyPathParams = new StringJoiner("&");
        for (Entry<String,List<String>> keyValuePair : queryParameters.entrySet()) {
            String currPathParam = keyValuePair.getKey() + "=" + keyValuePair.getValue();
            stringifyPathParams
                    .add(currPathParam);
        }
        return stringifyPathParams;
    }
}
