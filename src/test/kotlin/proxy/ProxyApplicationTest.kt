package proxy

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.*
import org.junit.After
import org.junit.Before

import org.junit.Test

class ProxyApplicationTest {
    private val objectMapper = ObjectMapper()

    private val testEntity = objectMapper.createObjectNode().put("title","test").put("body","test")!!

    private val lookupService : LookupService = mock<LookupService> {
        onGeneric { get("1") }.thenReturn(testEntity)
    }

    private val app = ProxyApplication(lookupService, arrayOf("test","test"),7005)

    @Before
    fun setUp() {

        Fuel.testMode {
            timeout = 15000 // Optional feature, set all requests' timeout to this value.
        }

        Thread.sleep(1000)
    }

    @Test
    fun shouldReturnExistentObject() {
        val (_, _, result) = "http://localhost:7005/cache/1".httpGet().authenticate("test","test").response()
        when(result) {
            is Result.Success -> {
                assertThat(objectMapper.readTree(result.get())).isEqualTo(testEntity)
            }
            is Result.Failure -> {
                fail(result.error.message)
            }
        }
    }

    @Test
    fun shouldReturnUnauthorizedOnWrongCredentials() {
        val (_, _, result) = "http://localhost:7005/cache/1".httpGet().authenticate("another","hey").response()
        assertThat(result.component2()?.response!!.statusCode).isEqualTo(401)
    }

    @After
    fun after() {
        app.app.stop()
    }
}