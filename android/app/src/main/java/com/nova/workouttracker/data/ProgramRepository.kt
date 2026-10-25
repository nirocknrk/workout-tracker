package com.nova.workouttracker.data

import android.content.Context
import android.net.Uri
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Owns the program content. The app always loads from [programFile] in the
 * app's private files dir (seeded from assets on first run), so an imported
 * bundle takes effect immediately without rebuilding the app.
 */
class ProgramRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val programFile: File get() = File(context.filesDir, "program.json")
    val mediaDir: File get() = File(context.filesDir, "media")

    /** Copies the bundled program + icons into private storage on first run. */
    fun ensureDefaults() {
        if (!programFile.exists()) {
            context.assets.open("program.json").use { input ->
                programFile.outputStream().use { output -> input.copyTo(output) }
            }
        }
        if (!mediaDir.exists()) {
            mediaDir.mkdirs()
            context.assets.list("media").orEmpty().filter { it.endsWith(".svg") }.forEach { name ->
                context.assets.open("media/$name").use { input ->
                    File(mediaDir, name).outputStream().use { output -> input.copyTo(output) }
                }
            }
        }
    }

    fun load(): Program = json.decodeFromString(Program.serializer(), programFile.readText())

    /** Parses and returns the program; throws [SerializationException] when the schema does not match. */
    fun parseProgramText(text: String): Program =
        json.decodeFromString(Program.serializer(), text)

    /** File holding the SVG icon for an exercise, or null when missing. */
    fun iconFile(iconName: String): File? {
        val f = File(mediaDir, "$iconName.svg")
        return if (f.exists()) f else null
    }

    /**
     * Writes a bundle ZIP containing `program.json` + `media/*.svg` to [uri].
     */
    fun exportBundle(uri: Uri) {
        context.contentResolver.openOutputStream(uri)?.use { raw ->
            ZipOutputStream(raw).use { zip ->
                zip.putNextEntry(ZipEntry("program.json"))
                programFile.inputStream().use { it.copyTo(zip) }
                zip.closeEntry()
                mediaDir.listFiles { f -> f.isFile && f.name.endsWith(".svg") }
                    .orEmpty().sortedBy { it.name }.forEach { f ->
                        zip.putNextEntry(ZipEntry("media/${f.name}"))
                        f.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }
            }
        } ?: throw IllegalStateException("Could not open destination for writing")
    }

    /**
     * Reads a bundle ZIP from [uri], validates that it contains a schema-valid
     * `program.json`, then replaces the stored program and media.
     * @throws IllegalArgumentException when the bundle is invalid.
     */
    fun importBundle(uri: Uri) {
        val entries = mutableMapOf<String, ByteArray>()
        context.contentResolver.openInputStream(uri)?.use { raw ->
            ZipInputStream(raw).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (!entry.isDirectory) {
                        entries[entry.name] = zip.readBytes()
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } ?: throw IllegalArgumentException("Could not open the selected file")

        val programBytes = entries["program.json"]
            ?: throw IllegalArgumentException("Bundle is missing program.json")
        val programText = programBytes.toString(Charsets.UTF_8)
        try {
            parseProgramText(programText) // schema validation
        } catch (e: SerializationException) {
            throw IllegalArgumentException("program.json does not match the expected schema: ${e.message}")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("program.json is not valid JSON: ${e.message}")
        }

        // Validation passed — replace stored files.
        programFile.writeText(programText)
        mediaDir.mkdirs()
        mediaDir.listFiles()?.forEach { it.delete() }
        entries.filterKeys { it.startsWith("media/") && it.endsWith(".svg") }.forEach { (name, bytes) ->
            File(mediaDir, name.removePrefix("media/")).writeBytes(bytes)
        }
    }
}
