package proxy

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.getAs
import org.assertj.core.api.Assertions
import org.hamcrest.core.Is
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.HashMap

class ProxyApplicationTest {

    val lookupService : LookupService by lazy { LookupService(
            LookupRepo("http://localhost:7000/posts/"), HashMap()) }

    val objectMapper = ObjectMapper()

    val testEntity = objectMapper.createObjectNode().put("title","test").put("body","test")

    @Before
    fun setUp() {
        //`when`(lookupService.get(Mockito.anyString())).thenReturn(testEntity)

        ProxyApplication(lookupService, arrayOf("test","test"),7005)

        Fuel.testMode {
            timeout = 15000 // Optional feature, set all requests' timeout to this value.
        }

        Thread.sleep(1000)
    }

    //

    @Test
    fun shouldReturnExistentObject() {
        val (request, response, result) = "http://localhost:7005/cache/1".httpGet().authenticate("test","test").response()
        when(result) {
            is Result.Success -> {
                Assertions.assertThat(objectMapper.readTree(result.get())).isEqualTo(testEntity)
            }
            is Result.Failure -> {
                Assertions.fail(result.error.message)
            }
        }
    }

    @After
    fun after() {
        //proxyApplication.app.stop()
    }

}