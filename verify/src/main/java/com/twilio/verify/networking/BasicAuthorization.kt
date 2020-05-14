package com.twilio.verify.networking

/*
 * Copyright (c) 2020, Twilio Inc.
 */
import android.util.Base64

internal const val AuthorizationHeader = "Authorization"
internal const val BasicAuth = "Basic"

internal data class BasicAuthorization constructor(
  private val username: String,
  private val password: String
) {
  val header: Pair<String, String>
    get() {
      val encodedAuthorization = Base64.encodeToString(
          "${this.username}:${this.password}".toByteArray(Charsets.UTF_8),
          Base64.NO_WRAP
      )
      return AuthorizationHeader to "$BasicAuth $encodedAuthorization"
    }
}