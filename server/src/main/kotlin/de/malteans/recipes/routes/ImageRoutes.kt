package de.malteans.recipes.routes

import de.malteans.recipes.dto.FinalizeResp
import de.malteans.recipes.dto.ImagePresignReq
import de.malteans.recipes.dto.PresignResp
import de.malteans.recipes.services.ImageService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*

fun Route.registerImageRoutes(
    imageService: ImageService
) {
    route("/images") {
        post("presign") {
            val req = call.receive<ImagePresignReq>()
            val ps = imageService.presignUpload(req)
            call.respond(PresignResp(ps.id, ps.putUrl, ps.key))
        }
        put("{id}/upload") {
            val id = call.parameters["id"]!!
            val bytes = call.receiveChannel().toByteArray() // requires kotlinx-io ext
            imageService.saveFile(id, bytes)
            call.respond(HttpStatusCode.Created)
        }
        post("{id}/finalize") {
            val id = call.parameters["id"]!!
            val meta = imageService.finalize(id)
            call.respond(FinalizeResp(meta.id, meta.publicUrl, meta.width, meta.height))
        }
        get {
            val list = imageService.list()
            call.respond(list)
        }
        delete("{id}") {
            val id = call.parameters["id"]!!
            imageService.delete(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
