package proxy

import io.javalin.ApiBuilder.*
import io.javalin.Javalin

val lookupService = LookupService(LookupRepo("http://localhost:7000/posts/"),HashMap())

fun main(args: Array<String>) {
    val app = Javalin.start(7001)
    app.routes {
        get("/cache/:id",{ ctx ->
            lookupService.get(ctx.param("id")!!, {jsonNode -> ctx.json(jsonNode) })
        })
    }
}