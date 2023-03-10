/*
 * YukiReflection - An efficient Reflection API for the Android platform built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiReflection
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/3/27.
 * This file is Modified by fankes on 2023/1/21.
 */
@file:Suppress("KotlinConstantConditions", "KDocUnresolvedReference")

package com.highcapable.yukireflection.finder.tools

import com.highcapable.yukireflection.factory.*
import com.highcapable.yukireflection.finder.base.data.BaseRulesData
import com.highcapable.yukireflection.finder.classes.data.ClassRulesData
import com.highcapable.yukireflection.finder.members.data.ConstructorRulesData
import com.highcapable.yukireflection.finder.members.data.FieldRulesData
import com.highcapable.yukireflection.finder.members.data.MemberRulesData
import com.highcapable.yukireflection.finder.members.data.MethodRulesData
import com.highcapable.yukireflection.finder.store.ReflectsCacheStore
import com.highcapable.yukireflection.log.yLoggerW
import com.highcapable.yukireflection.type.defined.UndefinedType
import com.highcapable.yukireflection.type.defined.VagueType
import com.highcapable.yukireflection.type.java.DalvikBaseDexClassLoader
import com.highcapable.yukireflection.type.java.NoClassDefFoundErrorClass
import com.highcapable.yukireflection.type.java.NoSuchFieldErrorClass
import com.highcapable.yukireflection.type.java.NoSuchMethodErrorClass
import com.highcapable.yukireflection.utils.*
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.*
import kotlin.math.abs

/**
 * ??????????????? [Class]???[Member] ????????????????????????
 */
@PublishedApi
internal object ReflectionTool {

    /** ???????????????????????? */
    private const val TAG = "YukiReflection#ReflectionTool"

    /** ?????????????????? [ClassLoader] */
    private val reflectionClassLoader = javaClass.classLoader ?: error("Operating system not supported")

    /**
     * ???????????? [ClassLoader] ????????? [Class] ????????????
     * @param loader ??????????????? [ClassLoader]
     * @return [List]<[String]>
     * @throws IllegalStateException ?????? [loader] ?????? [BaseDexClassLoader]
     */
    internal fun findDexClassList(loader: ClassLoader?) = ReflectsCacheStore.findDexClassList(loader.hashCode())
        ?: DalvikBaseDexClassLoader.field { name = "pathList" }.ignored().get(loader.value().let {
            while (it.value !is BaseDexClassLoader) {
                if (it.value?.parent != null) it.value = it.value?.parent
                else error("ClassLoader [$loader] is not a DexClassLoader")
            }; it.value ?: error("ClassLoader [$loader] load failed")
        }).current(ignored = true)?.field { name = "dexElements" }?.array<Any>()?.flatMap { element ->
            element.current(ignored = true).field { name = "dexFile" }.current(ignored = true)
                ?.method { name = "entries" }?.invoke<Enumeration<String>>()?.toList().orEmpty()
        }.orEmpty().also { if (it.isNotEmpty()) ReflectsCacheStore.putDexClassList(loader.hashCode(), it) }

    /**
     * ??????????????????????????? [Class] ????????????
     * @param name [Class] ????????????
     * @param loader [Class] ????????? [ClassLoader]
     * @return [Boolean]
     */
    internal fun hasClassByName(name: String, loader: ClassLoader?) = runCatching { findClassByName(name, loader); true }.getOrNull() ?: false

    /**
     * ??????????????????????????? [Class]
     * @param name [Class] ????????????
     * @param loader [Class] ????????? [ClassLoader]
     * @param initialize ??????????????? [Class] ?????????????????? - ?????????
     * @return [Class]
     * @throws NoClassDefFoundError ??????????????? [Class] ????????????????????? [ClassLoader]
     */
    @PublishedApi
    internal fun findClassByName(name: String, loader: ClassLoader?, initialize: Boolean = false): Class<*> {
        val hashCode = ("[$name][$loader]").hashCode()

        /**
         * ?????? [Class.forName] ??? [Class] ??????
         * @param name [Class] ????????????
         * @param initialize ??????????????? [Class] ??????????????????
         * @param loader [Class] ????????? [ClassLoader] - ????????? [reflectionClassLoader]
         * @return [Class]
         */
        fun classForName(name: String, initialize: Boolean, loader: ClassLoader? = reflectionClassLoader) =
            Class.forName(name, initialize, loader)

        /**
         * ????????????????????? [ClassLoader] ?????? [Class]
         * @return [Class] or null
         */
        fun loadWithDefaultClassLoader() = if (initialize.not()) loader?.loadClass(name) else classForName(name, initialize, loader)
        return ReflectsCacheStore.findClass(hashCode) ?: runCatching {
            (loadWithDefaultClassLoader() ?: classForName(name, initialize)).also { ReflectsCacheStore.putClass(hashCode, it) }
        }.getOrNull() ?: throw createException(loader ?: reflectionClassLoader, name = "Class", "name:[$name]")
    }

    /**
     * ???????????? [Class] ????????? [Class]
     * @param loaderSet ????????? [ClassLoader]
     * @param rulesData ??????????????????
     * @return [HashSet]<[Class]>
     * @throws IllegalStateException ?????? [loaderSet] ??? null ????????????????????????
     * @throws NoClassDefFoundError ??????????????? [Class]
     */
    internal fun findClasses(loaderSet: ClassLoader?, rulesData: ClassRulesData) = rulesData.createResult {
        ReflectsCacheStore.findClasses(hashCode(loaderSet)) ?: hashSetOf<Class<*>>().also { classes ->
            /**
             * ??????????????????
             * @param instance ?????? [Class] ??????
             */
            fun startProcess(instance: Class<*>) {
                conditions {
                    fromPackages.takeIf { it.isNotEmpty() }?.also { and(true) }
                    fullName?.also { it.equals(instance, it.TYPE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    simpleName?.also { it.equals(instance, it.TYPE_SIMPLE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    singleName?.also { it.equals(instance, it.TYPE_SINGLE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    fullNameConditions?.also { instance.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                    simpleNameConditions?.also { instance.simpleName.also { n -> runCatching { and(it(n.cast(), n)) } } }
                    singleNameConditions?.also { classSingleName(instance).also { n -> runCatching { and(it(n.cast(), n)) } } }
                    modifiers?.also { runCatching { and(it(instance.cast())) } }
                    extendsClass.takeIf { it.isNotEmpty() }?.also { and(instance.hasExtends && it.contains(instance.superclass.name)) }
                    implementsClass.takeIf { it.isNotEmpty() }
                        ?.also { and(instance.interfaces.isNotEmpty() && instance.interfaces.any { e -> it.contains(e.name) }) }
                    enclosingClass.takeIf { it.isNotEmpty() }
                        ?.also { and(instance.enclosingClass != null && it.contains(instance.enclosingClass.name)) }
                    isAnonymousClass?.also { and(instance.isAnonymousClass && it) }
                    isNoExtendsClass?.also { and(instance.hasExtends.not() && it) }
                    isNoImplementsClass?.also { and(instance.interfaces.isEmpty() && it) }
                    /**
                     * ?????? [MemberRulesData]
                     * @param size [Member] ??????
                     * @param result ??????????????????
                     */
                    fun MemberRulesData.matchCount(size: Int, result: (Boolean) -> Unit) {
                        takeIf { it.isInitializeOfMatch }?.also { rule ->
                            rule.conditions {
                                value.matchCount.takeIf { it >= 0 }?.also { and(it == size) }
                                value.matchCountRange.takeIf { it.isEmpty().not() }?.also { and(size in it) }
                                value.matchCountConditions?.also { runCatching { and(it(size.cast(), size)) } }
                            }.finally { result(true) }.without { result(false) }
                        } ?: result(true)
                    }

                    /**
                     * ?????????????????? [Class] ???????????? - ???????????? [UndefinedType]
                     * @param type ??????
                     * @return [Boolean]
                     */
                    fun MemberRulesData.exists(vararg type: Any?): Boolean {
                        if (type.isEmpty()) return true
                        for (i in type.indices) if (type[i] == UndefinedType) {
                            yLoggerW(msg = "$objectName type[$i] mistake, it will be ignored in current conditions")
                            return false
                        }
                        return true
                    }
                    memberRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existMembers?.apply {
                            var numberOfFound = 0
                            if (rule.isInitializeOfSuper) forEach { member ->
                                rule.conditions {
                                    value.modifiers?.also { runCatching { and(it(member.cast())) } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(size) { and(it) }
                        }
                    }
                    fieldRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existFields?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { field ->
                                rule.conditions {
                                    value.type?.takeIf { value.exists(it) }?.also { and(it == field.type) }
                                    value.name.takeIf { it.isNotBlank() }?.also { and(it == field.name) }
                                    value.modifiers?.also { runCatching { and(it(field.cast())) } }
                                    value.nameConditions?.also { field.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                                    value.typeConditions?.also { field.also { t -> runCatching { and(it(t.type(), t.type)) } } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(size) { and(it) }
                        }
                    }
                    methodRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existMethods?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { method ->
                                rule.conditions {
                                    value.name.takeIf { it.isNotBlank() }?.also { and(it == method.name) }
                                    value.returnType?.takeIf { value.exists(it) }?.also { and(it == method.returnType) }
                                    value.returnTypeConditions
                                        ?.also { method.also { r -> runCatching { and(it(r.returnType(), r.returnType)) } } }
                                    value.paramCount.takeIf { it >= 0 }?.also { and(method.parameterTypes.size == it) }
                                    value.paramCountRange.takeIf { it.isEmpty().not() }?.also { and(method.parameterTypes.size in it) }
                                    value.paramCountConditions
                                        ?.also { method.parameterTypes.size.also { s -> runCatching { and(it(s.cast(), s)) } } }
                                    value.paramTypes?.takeIf { value.exists(*it) }?.also { and(paramTypesEq(it, method.parameterTypes)) }
                                    value.paramTypesConditions
                                        ?.also { method.also { t -> runCatching { and(it(t.paramTypes(), t.parameterTypes)) } } }
                                    value.modifiers?.also { runCatching { and(it(method.cast())) } }
                                    value.nameConditions?.also { method.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(size) { and(it) }
                        }
                    }
                    constroctorRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existConstructors?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { constructor ->
                                rule.conditions {
                                    value.paramCount.takeIf { it >= 0 }?.also { and(constructor.parameterTypes.size == it) }
                                    value.paramCountRange.takeIf { it.isEmpty().not() }?.also { and(constructor.parameterTypes.size in it) }
                                    value.paramCountConditions
                                        ?.also { constructor.parameterTypes.size.also { s -> runCatching { and(it(s.cast(), s)) } } }
                                    value.paramTypes?.takeIf { value.exists(*it) }?.also { and(paramTypesEq(it, constructor.parameterTypes)) }
                                    value.paramTypesConditions
                                        ?.also { constructor.also { t -> runCatching { and(it(t.paramTypes(), t.parameterTypes)) } } }
                                    value.modifiers?.also { runCatching { and(it(constructor.cast())) } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(size) { and(it) }
                        }
                    }
                }.finally { classes.add(instance) }
            }
            findDexClassList(loaderSet).takeIf { it.isNotEmpty() }?.forEach { className ->
                /** ???????????? ??? com.demo.Test ??? com.demo (?????????????????? "." + ?????????????????????) ??? ?????????????????? "." ??????????????? 1 ????????? */
                (if (className.contains("."))
                    className.substring(0, className.length - className.split(".").let { it[it.lastIndex] }.length - 1)
                else className).also { packageName ->
                    if ((fromPackages.isEmpty() || fromPackages.any {
                            if (it.isAbsolute) packageName == it.name else packageName.startsWith(it.name)
                        }) && className.hasClass(loaderSet)
                    ) startProcess(className.toClass(loaderSet))
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putClasses(hashCode(loaderSet), it) } ?: throwNotFoundError(loaderSet)
    }

    /**
     * ???????????? [Field] ????????? [Field]
     * @param classSet [Field] ?????????
     * @param rulesData ??????????????????
     * @return [HashSet]<[Field]>
     * @throws IllegalStateException ?????????????????????????????? [FieldRulesData.type] ??????????????????
     * @throws NoSuchFieldError ??????????????? [Field]
     */
    internal fun findFields(classSet: Class<*>?, rulesData: FieldRulesData) = rulesData.createResult {
        if (type == UndefinedType) error("Field match type class is not found")
        if (classSet == null) return@createResult hashSetOf()
        ReflectsCacheStore.findFields(hashCode(classSet)) ?: hashSetOf<Field>().also { fields ->
            classSet.existFields?.also { declares ->
                var iType = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                var iTypeCds = -1
                val iLType = type?.let(matchIndex) { e -> declares.filter { e == it.type }.lastIndex } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.filter { e == it.name }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.cast()) } }.lastIndex } ?: -1
                val iLNameCds = nameConditions
                    ?.let(matchIndex) { e -> declares.filter { it.name.let { n -> runOrFalse { e(n.cast(), n) } } }.lastIndex } ?: -1
                val iLTypeCds = typeConditions?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.type(), it.type) } }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        type?.also {
                            and((it == instance.type).let { hold ->
                                if (hold) iType++
                                hold && matchIndex.compare(iType, iLType)
                            })
                        }
                        name.takeIf { it.isNotBlank() }?.also {
                            and((it == instance.name).let { hold ->
                                if (hold) iName++
                                hold && matchIndex.compare(iName, iLName)
                            })
                        }
                        modifiers?.also {
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(instance.name.let { n -> runOrFalse { it(n.cast(), n) } }.let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        typeConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.type(), t.type) } }.let { hold ->
                                if (hold) iTypeCds++
                                hold && matchIndex.compare(iTypeCds, iLTypeCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { fields.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putFields(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * ???????????? [Method] ????????? [Method]
     * @param classSet [Method] ?????????
     * @param rulesData ??????????????????
     * @return [HashSet]<[Method]>
     * @throws IllegalStateException ?????????????????????????????? [MethodRulesData.paramTypes] ?????? [MethodRulesData.returnType] ??????????????????
     * @throws NoSuchMethodError ??????????????? [Method]
     */
    internal fun findMethods(classSet: Class<*>?, rulesData: MethodRulesData) = rulesData.createResult {
        if (returnType == UndefinedType) error("Method match returnType class is not found")
        if (classSet == null) return@createResult hashSetOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Method match paramType[$p] class is not found") }
        ReflectsCacheStore.findMethods(hashCode(classSet)) ?: hashSetOf<Method>().also { methods ->
            classSet.existMethods?.also { declares ->
                var iReturnType = -1
                var iReturnTypeCds = -1
                var iParamTypes = -1
                var iParamTypesCds = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iParamCountCds = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                val iLReturnType = returnType?.let(matchIndex) { e -> declares.filter { e == it.returnType }.lastIndex } ?: -1
                val iLReturnTypeCds = returnTypeConditions
                    ?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.returnType(), it.returnType) } }.lastIndex } ?: -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.filter { e == it.parameterTypes.size }.lastIndex } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.filter { it.parameterTypes.size in e }.lastIndex } ?: -1
                val iLParamCountCds = paramCountConditions?.let(matchIndex) { e ->
                    declares.filter { it.parameterTypes.size.let { s -> runOrFalse { e(s.cast(), s) } } }.lastIndex
                } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.filter { paramTypesEq(e, it.parameterTypes) }.lastIndex } ?: -1
                val iLParamTypesCds = paramTypesConditions
                    ?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.paramTypes(), it.parameterTypes) } }.lastIndex } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.filter { e == it.name }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.cast()) } }.lastIndex } ?: -1
                val iLNameCds = nameConditions
                    ?.let(matchIndex) { e -> declares.filter { it.name.let { n -> runOrFalse { e(n.cast(), n) } } }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        name.takeIf { it.isNotBlank() }?.also {
                            and((it == instance.name).let { hold ->
                                if (hold) iName++
                                hold && matchIndex.compare(iName, iLName)
                            })
                        }
                        returnType?.also {
                            and((it == instance.returnType).let { hold ->
                                if (hold) iReturnType++
                                hold && matchIndex.compare(iReturnType, iLReturnType)
                            })
                        }
                        returnTypeConditions?.also {
                            and(instance.let { r -> runOrFalse { it(r.returnType(), r.returnType) } }.let { hold ->
                                if (hold) iReturnTypeCds++
                                hold && matchIndex.compare(iReturnTypeCds, iLReturnTypeCds)
                            })
                        }
                        paramCount.takeIf { it >= 0 }?.also {
                            and((instance.parameterTypes.size == it).let { hold ->
                                if (hold) iParamCount++
                                hold && matchIndex.compare(iParamCount, iLParamCount)
                            })
                        }
                        paramCountRange.takeIf { it.isEmpty().not() }?.also {
                            and((instance.parameterTypes.size in it).let { hold ->
                                if (hold) iParamCountRange++
                                hold && matchIndex.compare(iParamCountRange, iLParamCountRange)
                            })
                        }
                        paramCountConditions?.also {
                            and(instance.parameterTypes.size.let { s -> runOrFalse { it(s.cast(), s) } }.let { hold ->
                                if (hold) iParamCountCds++
                                hold && matchIndex.compare(iParamCountCds, iLParamCountCds)
                            })
                        }
                        paramTypes?.also {
                            and(paramTypesEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        paramTypesConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.paramTypes(), t.parameterTypes) } }.let { hold ->
                                if (hold) iParamTypesCds++
                                hold && matchIndex.compare(iParamTypesCds, iLParamTypesCds)
                            })
                        }
                        modifiers?.also {
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(instance.name.let { n -> runOrFalse { it(n.cast(), n) } }.let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { methods.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putMethods(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * ???????????? [Constructor] ????????? [Constructor]
     * @param classSet [Constructor] ?????????
     * @param rulesData ??????????????????
     * @return [HashSet]<[Constructor]>
     * @throws IllegalStateException ?????????????????????????????? [ConstructorRulesData.paramTypes] ??????????????????
     * @throws NoSuchMethodError ??????????????? [Constructor]
     */
    internal fun findConstructors(classSet: Class<*>?, rulesData: ConstructorRulesData) = rulesData.createResult {
        if (classSet == null) return@createResult hashSetOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Constructor match paramType[$p] class is not found") }
        ReflectsCacheStore.findConstructors(hashCode(classSet)) ?: hashSetOf<Constructor<*>>().also { constructors ->
            classSet.existConstructors?.also { declares ->
                var iParamTypes = -1
                var iParamTypesCds = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iParamCountCds = -1
                var iModify = -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.filter { e == it.parameterTypes.size }.lastIndex } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.filter { it.parameterTypes.size in e }.lastIndex } ?: -1
                val iLParamCountCds = paramCountConditions?.let(matchIndex) { e ->
                    declares.filter { it.parameterTypes.size.let { s -> runOrFalse { e(s.cast(), s) } } }.lastIndex
                } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.filter { paramTypesEq(e, it.parameterTypes) }.lastIndex } ?: -1
                val iLParamTypesCds = paramTypesConditions
                    ?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.paramTypes(), it.parameterTypes) } }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { runOrFalse { e(it.cast()) } }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        paramCount.takeIf { it >= 0 }?.also {
                            and((instance.parameterTypes.size == it).let { hold ->
                                if (hold) iParamCount++
                                hold && matchIndex.compare(iParamCount, iLParamCount)
                            })
                        }
                        paramCountRange.takeIf { it.isEmpty().not() }?.also {
                            and((instance.parameterTypes.size in it).let { hold ->
                                if (hold) iParamCountRange++
                                hold && matchIndex.compare(iParamCountRange, iLParamCountRange)
                            })
                        }
                        paramCountConditions?.also {
                            and(instance.parameterTypes.size.let { s -> runOrFalse { it(s.cast(), s) } }.let { hold ->
                                if (hold) iParamCountCds++
                                hold && matchIndex.compare(iParamCountCds, iLParamCountCds)
                            })
                        }
                        paramTypes?.also {
                            and(paramTypesEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        paramTypesConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.paramTypes(), t.parameterTypes) } }.let { hold ->
                                if (hold) iParamTypesCds++
                                hold && matchIndex.compare(iParamTypesCds, iLParamTypesCds)
                            })
                        }
                        modifiers?.also {
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { constructors.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putConstructors(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * ?????????????????????????????????
     * @param need ????????????
     * @param last ????????????
     * @return [Boolean] ??????????????????
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int) = this == null || ((first >= 0 && first == need && second) ||
            (first < 0 && abs(first) == (last - need) && second) || (last == need && second.not()))

    /**
     * ?????????????????????????????????
     * @param need ????????????
     * @param last ????????????
     * @param result ??????????????????
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int, result: (Boolean) -> Unit) {
        if (this == null) return
        ((first >= 0 && first == need && second) ||
                (first < 0 && abs(first) == (last - need) && second) ||
                (last == need && second.not())).also(result)
    }

    /**
     * ???????????????????????????
     * @param result ???????????????
     * @return [T]
     * @throws IllegalStateException ???????????? [BaseRulesData.isInitialize]
     */
    private inline fun <reified T, R : BaseRulesData> R.createResult(result: R.() -> T): T {
        when (this) {
            is FieldRulesData -> isInitialize.not()
            is MethodRulesData -> isInitialize.not()
            is ConstructorRulesData -> isInitialize.not()
            else -> true
        }.takeIf { it }?.also { error("You must set a condition when finding a $objectName") }
        return result(this)
    }

    /**
     * ??? [Class.getSuperclass] ????????????????????????
     * @param classSet ?????????
     * @return [T]
     * @throws NoSuchFieldError ??????????????? [throwNotFoundError] ?????????
     * @throws NoSuchMethodError ??????????????? [throwNotFoundError] ?????????
     * @throws IllegalStateException ?????? [R] ???????????????
     */
    private inline fun <reified T, R : MemberRulesData> R.findSuperOrThrow(classSet: Class<*>): T = when (this) {
        is FieldRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findFields(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        is MethodRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findMethods(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        is ConstructorRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findConstructors(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        else -> error("Type [$this] not allowed")
    }

    /**
     * ??????????????? [Class]???[Member] ?????????
     * @param instanceSet ?????? [ClassLoader] or [Class]
     * @throws NoClassDefFoundError ??????????????? [Class]
     * @throws NoSuchFieldError ??????????????? [Field]
     * @throws NoSuchMethodError ??????????????? [Method] or [Constructor]
     * @throws IllegalStateException ?????? [BaseRulesData] ???????????????
     */
    private fun BaseRulesData.throwNotFoundError(instanceSet: Any?): Nothing = when (this) {
        is FieldRulesData -> throw createException(instanceSet, objectName, *templates)
        is MethodRulesData -> throw createException(instanceSet, objectName, *templates)
        is ConstructorRulesData -> throw createException(instanceSet, objectName, *templates)
        else -> error("Type [$this] not allowed")
    }

    /**
     * ??????????????????
     * @param instanceSet ?????? [ClassLoader] or [Class]
     * @param name ????????????
     * @param content ????????????
     * @return [Throwable]
     */
    private fun createException(instanceSet: Any?, name: String, vararg content: String): Throwable {
        /**
         * ?????? [Class.getName] ?????????????????????????????? "->" ??????
         * @return [String]
         */
        fun Class<*>.space(): String {
            var space = ""
            for (i in 0..this.name.length) space += " "
            return "$space -> "
        }
        if (content.isEmpty()) return IllegalStateException("Exception content is null")
        val space = when (name) {
            "Class" -> NoClassDefFoundErrorClass.space()
            "Field" -> NoSuchFieldErrorClass.space()
            "Method", "Constructor" -> NoSuchMethodErrorClass.space()
            else -> error("Invalid Exception type")
        }
        var splicing = ""
        content.forEach { if (it.isNotBlank()) splicing += "$space$it\n" }
        val template = "Can't find this $name in [$instanceSet]:\n${splicing}Generated by $TAG"
        return when (name) {
            "Class" -> NoClassDefFoundError(template)
            "Field" -> NoSuchFieldError(template)
            "Method", "Constructor" -> NoSuchMethodError(template)
            else -> error("Invalid Exception type")
        }
    }

    /**
     * ???????????? [Class] ???????????? [Member] ??????
     * @return [Array]<[Member]>
     */
    private val Class<*>.existMembers
        get() = runCatching {
            arrayListOf<Member>().apply {
                addAll(declaredFields.toList())
                addAll(declaredMethods.toList())
                addAll(declaredConstructors.toList())
            }.toTypedArray()
        }.onFailure {
            yLoggerW(msg = "Failed to get the declared Members in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * ???????????? [Class] ???????????? [Field] ??????
     * @return [Array]<[Field]>
     */
    private val Class<*>.existFields
        get() = runCatching { declaredFields }.onFailure {
            yLoggerW(msg = "Failed to get the declared Fields in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * ???????????? [Class] ???????????? [Method] ??????
     * @return [Array]<[Method]>
     */
    private val Class<*>.existMethods
        get() = runCatching { declaredMethods }.onFailure {
            yLoggerW(msg = "Failed to get the declared Methods in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * ???????????? [Class] ???????????? [Constructor] ??????
     * @return [Array]<[Constructor]>
     */
    private val Class<*>.existConstructors
        get() = runCatching { declaredConstructors }.onFailure {
            yLoggerW(msg = "Failed to get the declared Constructors in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * ?????????????????????????????????????????????????????????
     *
     * ????????? [Class] ?????? [Class.arrayContentsEq]
     * @param compare ?????????????????????
     * @param original ?????????????????????????????????
     * @return [Boolean] ????????????
     * @throws IllegalStateException ?????? [VagueType] ???????????????
     */
    private fun paramTypesEq(compare: Array<out Any>?, original: Array<out Any>?): Boolean {
        return when {
            (compare == null && original == null) || (compare?.isEmpty() == true && original?.isEmpty() == true) -> true
            (compare == null && original != null) || (compare != null && original == null) || (compare?.size != original?.size) -> false
            else -> {
                if (compare == null || original == null) return false
                if (compare.all { it == VagueType }) error("The number of VagueType must be at least less than the count of paramTypes")
                for (i in compare.indices) if ((compare[i] !== VagueType) && (compare[i] !== original[i])) return false
                true
            }
        }
    }
}