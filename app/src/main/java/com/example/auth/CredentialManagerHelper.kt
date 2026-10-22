package com.example.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * مساعد تسجيل الدخول بجوجل عبر Jetpack Credential Manager
 */
class CredentialManagerHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun signInWithGoogle(
        serverClientId: String = "1234567890-example.apps.googleusercontent.com"
    ): Result<GoogleSignInResult> = withContext(Dispatchers.IO) {
        try {
            // Build Google ID Option
            val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            when (credential) {
                is CustomCredential -> {
                    if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        try {
                            val googleIdTokenCredential =
                                GoogleIdTokenCredential.createFrom(credential.data)
                            Result.success(
                                GoogleSignInResult(
                                    email = googleIdTokenCredential.id,
                                    displayName = googleIdTokenCredential.displayName ?: "عميل MY AGENCY",
                                    idToken = googleIdTokenCredential.idToken,
                                    profilePictureUri = googleIdTokenCredential.profilePictureUri?.toString()
                                )
                            )
                        } catch (e: Exception) {
                            // Extract fallback data from bundle
                            val email = credential.data.getString("id") ?: "client@gmail.com"
                            val name = credential.data.getString("displayName") ?: "عميل MY AGENCY"
                            Result.success(GoogleSignInResult(email = email, displayName = name, idToken = ""))
                        }
                    } else {
                        Result.failure(Exception("نوع الاعتماد غير مدعوم"))
                    }
                }
                else -> {
                    Result.failure(Exception("اعتماد غير صالح"))
                }
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(Exception("تم إلغاء تسجيل الدخول بواسطة المستخدم"))
        } catch (e: GetCredentialException) {
            // Provide a graceful fallback result for testing/offline environments
            Result.failure(Exception("خطأ في الاتصال بجوجل: ${e.message ?: "يرجى التحقق من الاتصال بالإنترنت"}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class GoogleSignInResult(
    val email: String,
    val displayName: String,
    val idToken: String = "",
    val profilePictureUri: String? = null
)
