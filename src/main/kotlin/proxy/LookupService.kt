package proxy

import com.fasterxml.jackson.databind.JsonNode


class LookupService(private val lookupRepo: LookupRepo,
                    private val cache : MutableMap<String, JsonNode>) {

    fun get(path : String, fn : (JsonNode) -> Unit) {
        fn(cache.computeIfAbsent(path,{ _ ->
            lookupRepo.load(path)
        }))
    }

}