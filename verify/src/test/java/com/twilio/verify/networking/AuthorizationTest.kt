package com.twilio.verify.networking

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AuthorizationTest {

  @Test
  fun `Authorization should return a pair with key = Authorization & value = Basic auth`() {
    val accountSid = "accountSid"
    val authToken = "authToken"
    val expectedBasicAuthorization = "$BasicAuth YWNjb3VudFNpZDphdXRoVG9rZW4="
    val authorization = Authorization(accountSid, authToken)
    assertEquals(AuthorizationHeader, authorization.header.first)
    assertEquals(expectedBasicAuthorization, authorization.header.second)
  }
}