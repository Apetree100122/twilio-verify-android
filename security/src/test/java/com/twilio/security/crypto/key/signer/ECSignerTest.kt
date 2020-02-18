/*
 * Copyright (c) 2020, Twilio Inc.
 */
package com.twilio.security.crypto.key.signer

import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.twilio.security.crypto.KeyException
import com.twilio.security.crypto.mocks.keystore.addProvider
import com.twilio.security.crypto.mocks.keystore.keyStoreMockName
import com.twilio.security.crypto.mocks.keystore.setProviderAsVerified
import com.twilio.security.crypto.mocks.signature.SignatureMockInput
import com.twilio.security.crypto.mocks.signature.SignatureMockOutput
import com.twilio.security.crypto.mocks.signature.signatureMockInput
import com.twilio.security.crypto.mocks.signature.signatureMockName
import com.twilio.security.crypto.mocks.signature.signatureMockOutput
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.security.KeyStore.PrivateKeyEntry
import java.security.PrivateKey
import java.security.Provider
import java.security.PublicKey
import java.security.Security
import java.security.cert.Certificate

@RunWith(RobolectricTestRunner::class)
class ECSignerTest {

  @get:Rule
  val exceptionRule: ExpectedException = ExpectedException.none()

  private val providerName = "TestKeyStore"
  private val signatureAlgorithm = "TestSignatureAlgorithm"

  private lateinit var ecSigner: ECSigner
  private lateinit var provider: Provider

  @Before
  fun setup() {
    provider = object : Provider(
        providerName, 1.0, "Fake KeyStore which is used for Robolectric tests"
    ) {
      init {
        put(
            "KeyStore.$providerName",
            keyStoreMockName
        )
        put(
            "Signature.$signatureAlgorithm",
            signatureMockName
        )
      }
    }
    setProviderAsVerified(provider)
    addProvider(provider)
    signatureMockInput = SignatureMockInput()
    signatureMockOutput = SignatureMockOutput()
    val entry: PrivateKeyEntry = mock()
    val privateKey: PrivateKey = mock()
    val certificate: Certificate = mock()
    val publicKey: PublicKey = mock()
    whenever(entry.privateKey).thenReturn(privateKey)
    whenever(entry.certificate).thenReturn(certificate)
    whenever(certificate.publicKey).thenReturn(publicKey)
    ecSigner = ECSigner(entry, signatureAlgorithm)
  }

  @After
  fun tearDown() {
    Security.removeProvider(providerName)
  }

  @Test
  fun `Sign data using algorithm should return signature`() {
    val data = "test".toByteArray()
    val expectedSignature = "signature"
    signatureMockInput.signature = expectedSignature
    val signature = ecSigner.sign(data)
    assertEquals(ecSigner.entry.privateKey, signatureMockOutput.privateKey)
    assertTrue(signatureMockOutput.initialized)
    assertTrue(data.contentEquals(signatureMockOutput.updatedData!!))
    assertTrue(expectedSignature.toByteArray().contentEquals(signature))
  }

  @Test
  fun `Error signing data should throw exception`() {
    val data = "test".toByteArray()
    val error: RuntimeException = mock()
    signatureMockInput.error = error
    exceptionRule.expect(KeyException::class.java)
    exceptionRule.expectCause(
        Matchers.instanceOf(
            RuntimeException::class.java
        )
    )
    ecSigner.sign(data)
  }

  @Test
  fun `Get PublicKey should return expected key`() {
    val certificate: Certificate = mock()
    whenever(ecSigner.entry.certificate).thenReturn(certificate)
    val publicKey: PublicKey = mock()
    whenever(ecSigner.entry.certificate.publicKey).thenReturn(publicKey)
    val expectedPublicKey = "publicKey"
    whenever(ecSigner.entry.certificate.publicKey.encoded).thenReturn(
        expectedPublicKey.toByteArray()
    )

    assertTrue(ecSigner.getPublic().contentEquals(expectedPublicKey.toByteArray()))
  }

  @Test(expected = KeyException::class)
  fun `Error getting PublicKey should throw exception`() {
    val certificate: Certificate = mock()
    whenever(ecSigner.entry.certificate).thenReturn(certificate)
    val publicKey: PublicKey = mock()
    whenever(ecSigner.entry.certificate.publicKey).thenReturn(publicKey)
    val exception: KeyException = mock()
    given(ecSigner.entry.certificate.publicKey).willAnswer {
      throw KeyException(exception)
    }
    ecSigner.getPublic()
  }

  @Test
  fun `Verify signature using algorithm should return true`() {
    val data = "test".toByteArray()
    val signature = "signature".toByteArray()
    val expectedResult = true
    signatureMockInput.result = expectedResult
    val result = ecSigner.verify(data, signature)
    assertEquals(ecSigner.entry.certificate.publicKey, signatureMockOutput.publicKey)
    assertTrue(signatureMockOutput.initialized)
    assertTrue(data.contentEquals(signatureMockOutput.updatedData!!))
    assertEquals(expectedResult, result)
  }

  @Test
  fun `Error verifying signature should throw exception`() {
    val data = "test".toByteArray()
    val signature = "signature".toByteArray()
    val error: RuntimeException = mock()
    signatureMockInput.error = error
    exceptionRule.expect(KeyException::class.java)
    exceptionRule.expectCause(
        Matchers.instanceOf(
            RuntimeException::class.java
        )
    )
    ecSigner.verify(data, signature)
  }
}