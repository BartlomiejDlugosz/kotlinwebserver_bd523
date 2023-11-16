package webserver

// write your web framework code here:

typealias HttpHandler = (Request) -> Response

fun scheme(url: String): String = url.substringBefore("://")

fun host(url: String): String =
  url.substringAfter("://")
    .substringBefore("/")

fun path(url: String): String =
  "/".plus(
    url.substringAfter("://")
      .substringBefore("?")
      .substringAfter("/")
  )

fun queryParams(url: String): List<Pair<String, String>> =
  when {
    url.contains("?") ->
      url.substringAfter("?")
        .split("&")
        .map { x: String ->
          val pair = x.split("=")
          Pair(pair[0], pair[1])
        }

    else -> emptyList()
  }

// http handlers for a particular website...

fun homePageHandler(req: Request): Response = Response(Status.OK, "This is Imperial.")

fun computingPageHandler(req: Request): Response = Response(Status.OK, "This is DoC.")

fun restrictedPageHandler(req: Request): Response = Response(Status.OK, "This is very secret.")

fun notFound(req: Request): Response = Response(Status.NOT_FOUND, "")
fun forbidden(req: Request): Response = Response(Status.FORBIDDEN, "")

fun extractParam(
  params: List<Pair<String, String>>,
  id: String
): String? = params.find { it.first == id }?.second

fun helloHandler(req: Request): Response {
  val params = queryParams(req.url)
  val name = extractParam(params, "name")
  val style = extractParam(params, "style")
  var body = "Hello, "
  body +=
    when {
      name != null -> "$name!"
      else -> "World!"
    }
  body =
    when (style) {
      "shouting" -> body.uppercase()
      else -> body
    }
  return Response(Status.OK, body)
}

fun route(req: Request): Response =
  when (path(req.url)) {
    "/" -> homePageHandler(req)
    "/say-hello" -> helloHandler(req)
    "/computing" -> computingPageHandler(req)
    else -> notFound(req)
  }

fun requireToken(token: String, wrapped: HttpHandler): HttpHandler = { req: Request ->
  when (req.authToken) {
    token -> wrapped(req)
    else -> forbidden(req)
  }
}

val mappings: List<Pair<String, HttpHandler>> = listOf(
  "/" to ::homePageHandler,
  "/say-hello" to ::helloHandler,
  "/computing" to ::computingPageHandler,
  "/exam-marks" to requireToken("password1", ::restrictedPageHandler)
)

fun configureRoutes(mappings: List<Pair<String, HttpHandler>>): HttpHandler {
  return { req: Request ->
    when (val x = mappings.find { it.first == path(req.url) }) {
      null -> notFound(req)
      else -> x.second(req)
    }
  }
}
