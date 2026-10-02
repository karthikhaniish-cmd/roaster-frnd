package com.example

import com.example.data.model.RoastLanguage
import com.example.data.model.RoastLevel
import com.example.data.model.RoastRequest
import com.example.data.model.RoastStyle
import com.example.data.remote.GeminiRoastService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testDemoRoastsGeneration() {
    val service = GeminiRoastService()
    val request = RoastRequest(
      name = "Harish",
      description = "Always late to class",
      language = RoastLanguage.TANGLISH,
      level = RoastLevel.SAVAGE,
      style = RoastStyle.COLLEGE_FRIEND
    )

    val roasts = service.getDemoRoasts(request)
    assertEquals(3, roasts.size)
    assertTrue(roasts[0].contains("Harish"))
  }
}
