package org.jclouds.codec;

import org.jclouds.ContextBuilder;
import org.jclouds.representations.Context;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Test
public class ToContextTest {

   @Test
   void testConversion() {
      assertNull(ToContext.INSTANCE.apply(null));
      org.jclouds.Context context = ContextBuilder.newBuilder("stub").name("test-stub").credentials("user", "pass").build();
      Context representation = ToContext.INSTANCE.apply(context);
      assertNotNull(representation);
      assertEquals("test-stub", representation.getName());
   }
}
