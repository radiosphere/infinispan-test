package io.radiosphere.inifispantest

import javax.ws.rs.*

@Path("/values")
class ValueController {

    val store = mutableMapOf<String, String>()

    @POST
    fun saveValue(@QueryParam("key") key: String, @QueryParam("value") value: String) {
        store.put(key, value)

    }

    @GET
    @Path("/{key}")
    fun saveValue(@PathParam(value = "key") key: String): String? {
        return store[key]
    }
}