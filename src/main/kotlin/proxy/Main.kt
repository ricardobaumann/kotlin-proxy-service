package proxy

import com.fasterxml.jackson.databind.JsonNode
import io.javalin.ApiBuilder.*
import io.javalin.Context
import io.javalin.HaltException
import io.javalin.Javalin
import java.util.*

fun main(args: Array<String>) {

    ProxyApplication(LookupService(
            LookupRepo("http://localhost:7000/posts/"),HashMap()),
            arrayOf("admin","admin"),
            7001)

}

class ProxyApplication(lookupService: LookupService, appCredentials: Array<String>, port: Int) {
    val app = Javalin.start(port)

    private val toResponse = fun JsonNode.(ctx : Context): Context = ctx.json(this)

    init {
        app.routes {
            before { ctx ->
                val credentials = ctx.basicAuthCredentials()
                if (!appCredentials.contentEquals(arrayOf(credentials?.username,credentials?.password))) {
                    throw HaltException(401)
                }
            }
            get("/cache/:id",{ ctx ->
                lookupService.get(ctx.param("id")!!).toResponse(ctx)
            })
        }
    }
}
