package proxy

import com.fasterxml.jackson.databind.JsonNode
import kotlin.concurrent.fixedRateTimer

open class LookupService(private val lookupRepo: LookupRepo,
                    private val cache : MutableMap<String, JsonNode>,
                    reloadEvery : Long = 5000) {

    init {
        fixedRateTimer(name = "reload-cache",
                initialDelay = 1000, period = reloadEvery) {
            println("reloading")
            cache.keys.forEach { key -> cache[key] = lookupRepo.load(key) }
        }
    }

    open fun get(path : String) : JsonNode {
        return cache.computeIfAbsent(path, { _ ->
            lookupRepo.load(path)
        })
    }

}