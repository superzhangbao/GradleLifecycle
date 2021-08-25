package com.hikvision.router_processor

import com.google.auto.service.AutoService
import com.hikvision.router.annotations.Destination
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

@AutoService(value = [DestinationProcessor::class])
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

            allDestinationEnvironment?.onEach {element->
                val typeElement = element as TypeElement
                val destination = typeElement.getAnnotation(Destination::class.java)
                destination?.apply {
                    val realPath = typeElement.qualifiedName
                    println("$TAG >>> url:$url destination:$destination realPath:$realPath")
                }
            }
            println("$TAG >>> process finish...")
        }

        return true
    }
}