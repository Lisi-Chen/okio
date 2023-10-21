object CustomResourceLoader {

  fun getResourceAsStream(classLoader: ClassLoader, resourceName: String): InputStream? {
    val resourceURL = classLoader.getResource(resourceName) ?: return null
    val connection = resourceURL.openConnection()

    // Explicitly disable caching
    connection.useCaches = false

    return connection.getInputStream().let { CustomResourceInputStream(it) }
  }

  private class CustomResourceInputStream(inputStream: InputStream) : FilterInputStream(inputStream) {
    override fun close() {
      try {
        super.close()
      } finally {
        // Ensure the underlying jar file is closed
        (this.in as? JarURLConnection)?.jarFile?.close()
      }
    }
  }
}

// Usage:
val stream = CustomResourceLoader.getResourceAsStream(ClassLoader.getSystemClassLoader(), "path/to/resource")
stream?.use { /* process the stream */ }
