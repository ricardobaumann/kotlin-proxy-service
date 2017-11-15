package proxy

import com.fasterxml.jackson.databind.JsonNode


class LookupService(private val lookupRepo: LookupRepo,
                    private val cache : MutableMap<String, JsonNode>) {

    fun get(path : String): JsonNode {
        val result = cache.getOrElse(path) {lookupRepo.load(path)}
        cache[path] = result
        return result
    }

}