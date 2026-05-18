package de.malteans.recipes

sealed class Endpoints(
    private val relativeUrl: String,
) {
    data class RelativePath(val path: String) : Endpoints(path)

    sealed interface Recipes {
        data object GetAll : Endpoints(
            "/recipes",
        )

        data object Add : Endpoints(
            "/recipes",
        )
    }

    sealed interface Images {
        data class Get(val imageId: String) : Endpoints(
            "/images/$imageId",
        )

        data object Presign : Endpoints(
            "/images/presign",
        )

        data class Upload(val imageId: String) : Endpoints(
            "/images/$imageId/upload",
        )

        data class Finalize(val imageId: String) : Endpoints(
            "/images/$imageId/finalize",
        )
    }

    val url: String
        get() = "${Constants.BASE_URL}/${Constants.API_VERSION}" + relativeUrl
}