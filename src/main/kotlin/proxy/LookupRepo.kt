package proxy

import com.fasterxml.jackson.databind.JsonNode
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.jackson.responseObject

class LookupRepo(basePath : String) {

    init {
        FuelManager.instance.basePath = basePath
    }

    fun load(path : String) : JsonNode {

        val (result, error) = path.httpGet().responseObject<JsonNode>().third
        if (error==null) {
            print(result)
            return result!!
        }
        throw RuntimeException(error.message)
    }
}