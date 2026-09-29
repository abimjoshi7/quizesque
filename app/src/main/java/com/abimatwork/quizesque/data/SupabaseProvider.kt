package com.abimatwork.quizesque.data

import com.abimatwork.quizesque.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Optional Supabase client. The app continues to work offline when project
 * settings have not been supplied in Gradle properties.
 */
object SupabaseProvider {
    val client: SupabaseClient? by lazy {
        val url = BuildConfig.SUPABASE_URL.trim()
        val anonKey = BuildConfig.SUPABASE_ANON_KEY.trim()
        if (url.isBlank() || anonKey.isBlank()) {
            null
        } else {
            createSupabaseClient(supabaseUrl = url, supabaseKey = anonKey) {
                install(Auth)
                install(Postgrest)
            }
        }
    }
}
