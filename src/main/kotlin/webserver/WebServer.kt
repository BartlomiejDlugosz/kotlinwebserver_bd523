package webserver

// write your web framework code here:

fun scheme(url: String): String {
  return url.split("://")[0]
}

fun host(url: String): String {
  return url.split("://")[1].split("/")[0]
}

fun path(url: String): String {
  return "/".plus(url.split("://")[1].split("?")[0].split("/").drop(1).joinToString("/"))
}

fun queryParams(url: String): List<Pair<String, String>> = TODO()

// http handlers for a particular website...

fun homePageHandler(request: Request): Response = Response(Status.OK, "This is Imperial.")
