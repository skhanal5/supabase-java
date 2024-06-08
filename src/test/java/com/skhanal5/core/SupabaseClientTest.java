package com.skhanal5.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SupabaseClientTest {

    @Test
    void testNewInstanceMinimal() {
        Assertions.assertThrows(NullPointerException.class,
                () -> SupabaseClient.newInstance(null,null,null,null));
    }

}
