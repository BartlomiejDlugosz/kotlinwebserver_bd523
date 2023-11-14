package webserver

// write your web framework code here:

fun scheme(url: String): String =
  url.split("://")[0]

fun host(url: String): String =
  url.split("://")[1]
    .split("/")[0]

fun path(url: String): String =
  "/".plus(
    url.split("://")[1]
      .split("?")[0]
      .split("/")
      .drop(1)
      .joinToString("/")
  )

fun queryParams(url: String): List<Pair<String, String>> =
  when {
    url.contains("?") ->
      url.split("?")[1]
        .split("&")
        .map { x: String ->
          val pair = x.split("=")
          Pair(pair[0], pair[1])
        }

    else -> emptyList()
  }

// http handlers for a particular website...

fun homePageHandler(request: Request): Response = Response(Status.OK, "This is Imperial.")
fun computingPageHandler(request: Request): Response = Response(Status.OK, "This is DoC.")
fun extractParam(params: List<Pair<String, String>>, id: String): String? = params.find { it.first == id }?.second

fun helloHandler(request: Request): Response {
  val params = queryParams(request.url)
  val name = extractParam(params, "name")
  val style = extractParam(params, "style")
  var body = "Hello, "
  body += when {
    name != null -> name + "!"
    else -> "World!"
  }
  body = when (style) {
    "shouting" -> body.uppercase()
    else -> body
  }
  return Response(Status.OK, body)
}

fun route(request: Request): Response =
  when (path(request.url)) {
    "/" -> homePageHandler(request)
    "/say-hello" -> helloHandler(request)
    "/computing" -> computingPageHandler(request)
    else -> Response(Status.NOT_FOUND, "")
  }
