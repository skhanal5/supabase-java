package com.skhanal5.constants;

import java.util.List;
import java.util.Map;

public class HeaderConstants {
    public static Map<String, List<String>> RETRIEVE_RESPONSE_VALUES = Map.of("Prefer",List.of("return=representation"));

    private HeaderConstants() {
    }
}
