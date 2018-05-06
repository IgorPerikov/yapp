package auth

object SecLists {
    private val weakPasswords = mutableSetOf<String>()

    init {
        this::class.java.classLoader.getResource("seclists/top1000").readText()
            .lines()
            .filter { it.isNotBlank() }
            .forEach { weakPasswords.add(it) }
    }

    fun isWeak(password: String): Boolean {
        return weakPasswords.contains(password)
    }
}
