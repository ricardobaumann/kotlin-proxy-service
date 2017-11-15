package proxy

import com.fasterxml.jackson.databind.JsonNode
import kotlin.concurrent.fixedRateTimer

class LookupService(private val lookupRepo: LookupRepo,
                    private val cache : MutableMap<String, JsonNode>,
                    reloadEvery : Long = 5000) {

    init {
        fixedRateTimer(name = "reload-cache",
                initialDelay = 1000, period = reloadEvery) {
            println("reloading")
            cache.keys.forEach { key -> cache[key] = lookupRepo.load(key) }
        }
    }

    fun get(path : String, fn : (JsonNode) -> Unit) {
        fn(cache.computeIfAbsent(path,{ _ ->
            lookupRepo.load(path)
        }))
    }

}