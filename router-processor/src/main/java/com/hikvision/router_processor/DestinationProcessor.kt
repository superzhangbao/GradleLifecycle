package com.hikvision.router_processor

import com.google.auto.service.AutoService
import com.hikvision.router.annotations.Destination
import java.io.Writer
import java.lang.RuntimeException
import java.lang.StringBuilder
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(value = [Processor::class])
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class DestinationProcessor : AbstractProcessor(){

    companion object{
        const val TAG = "DestinationProcessor"
    }

    /**
     * 告诉编译器当前处理器支持的注解类型
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return Collections.singleton(Destination::class.java.canonicalName)
    }

    override fun process(set: MutableSet<out TypeElement>?, environment: RoundEnvironment?): Boolean {
        environment?.apply {
            if(processingOver()) return false
            println("$TAG >>> process start...")
            val allDestinationEnvironment = getElementsAnnotatedWith(Destination::class.java)
            println("$TAG all Destination count ${allDestinationEnvironment?.size}")

            if(allDestinationEnvironment!=null&& allDestinationEnvironment.isEmpty()) return false

            val className = "RouterMapping_${System.currentTimeMillis()}"

            val builder = StringBuilder()
            builder.append("package com.hikvision.gradlelifecycle.mappinng;\n\n")
            builder.append("import java.util.HashMap;\n")
            builder.append("import java.util.Map;\n\n")
            builder.append("public class $className {\n")
            builder.append("    public static Map<String,String> get() {\n")
            builder.append("        HashMap<String,String> map = new HashMap<>();\n")


            allDestinationEnvironment?.onEach {element->
                val typeElement = element as TypeElement
                val destination = typeElement.getAnnotation(Destination::class.java)
                destination?.apply {
                    val realPath = typeElement.qualifiedName
                    println("$TAG >>> url:$url destination:$destination realPath:$realPath")
                    builder.append("        map.put(\"$url\",\"$realPath\");\n")
                }
            }

            builder.append("        return map;\n")
            builder.append("    }\n").append("}\n")

            val mappingFullClassName = "com.hikvision.gradlelifecycle.mappinng.$className"
            println("$TAG >>> mappingFullClassName = $mappingFullClassName")

            println("$TAG >>> class content =\n$builder")

            var writer:Writer? = null
            try {
                val sourceFile = processingEnv.filer.createSourceFile(mappingFullClassName)
                writer = sourceFile.openWriter()
                writer.write(builder.toString())
                writer.flush()
            } catch (e: Exception) {
                throw RuntimeException("Error while create file",e)
            }finally {
                writer?.close()
            }

            println("$TAG >>> process finish...")
        }

        return true
    }
}