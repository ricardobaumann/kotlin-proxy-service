package proxy

import io.javalin.ApiBuilder.*
import io.javalin.HaltException
import io.javalin.Javalin
import java.util.*

val lookupService = LookupService(LookupRepo("http://localhost:7000/posts/"),HashMap())

val appCredentials = arrayOf("admin","admin")

fun main(args: Array<String>) {
    val app = Javalin.start(7001)
    app.routes {
        before { ctx ->
            val credentials = ctx.basicAuthCredentials()
            if (!appCredentials.contentEquals(arrayOf(credentials?.username,credentials?.password))) {
                throw HaltException(401)
            }
        }
        get("/cache/:id",{ ctx ->
            lookupService.get(ctx.param("id")!!, {jsonNode -> ctx.json(jsonNode) })
        })
    }
}