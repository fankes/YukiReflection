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
 * This file is Created by fankes on 2022/2/4.
 * This file is Modified by fankes on 2023/1/21.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "KotlinConstantConditions")

package com.highcapable.yukireflection.finder.members

import com.highcapable.yukireflection.annotation.YukiPrivateApi
import com.highcapable.yukireflection.bean.VariousClass
import com.highcapable.yukireflection.factory.hasExtends
import com.highcapable.yukireflection.finder.base.BaseFinder
import com.highcapable.yukireflection.finder.base.MemberBaseFinder
import com.highcapable.yukireflection.finder.members.data.MethodRulesData
import com.highcapable.yukireflection.finder.tools.ReflectionTool
import com.highcapable.yukireflection.finder.type.factory.*
import com.highcapable.yukireflection.log.yLoggerW
import com.highcapable.yukireflection.type.defined.UndefinedType
import com.highcapable.yukireflection.type.defined.VagueType
import com.highcapable.yukireflection.utils.runBlocking
import java.lang.reflect.Method

/**
 * [Method] ?????????
 *
 * ????????????????????????????????? [Method] ????????? [Method]
 * @param classSet ????????????????????? [Class] ??????
 */
class MethodFinder @PublishedApi internal constructor(@PublishedApi override val classSet: Class<*>? = null) :
    MemberBaseFinder(tag = "Method", classSet) {

    @PublishedApi
    override var rulesData = MethodRulesData()

    /** ??????????????? [classSet] */
    private var usedClassSet = classSet

    /** ??????????????????????????? */
    private var remedyPlansCallback: (() -> Unit)? = null

    /**
     * ?????? [Method] ??????
     *
     * - ??????????????????????????????????????????????????????
     * @return [String]
     */
    var name
        get() = rulesData.name
        set(value) {
            rulesData.name = value
        }

    /**
     * ?????? [Method] ????????????
     *
     * ?????????????????? [param] ????????????????????????????????????????????????????????????
     *
     * ?????????????????????????????????????????? [param]
     * @return [Int]
     */
    var paramCount
        get() = rulesData.paramCount
        set(value) {
            rulesData.paramCount = value
        }

    /**
     * ?????? [Method] ?????????
     *
     * - ???????????? [Class]???[String]???[VariousClass]
     *
     * - ?????????????????????
     * @return [Any] or null
     */
    var returnType
        get() = rulesData.returnType
        set(value) {
            rulesData.returnType = value.compat()
        }

    /**
     * ?????? [Method] ?????????????????????
     *
     * - ????????????????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param conditions ???????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun modifiers(conditions: ModifierConditions): IndexTypeCondition {
        rulesData.modifiers = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ?????????????????????
     *
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun emptyParam() = paramCount(num = 0)

    /**
     * ?????? [Method] ??????
     *
     * ????????????????????? [paramCount] ??? [paramType] ?????????????????? [paramCount] ????????????
     *
     * ?????? [Method] ?????????????????????????????????????????? - ??????????????? [VagueType] ????????????
     *
     * ?????????????????????????????? ???
     *
     * ```java
     * void foo(String var1, boolean var2, com.demo.Test var3, int var4)
     * ```
     *
     * ?????????????????????????????? ???
     *
     * ```kotlin
     * param(StringType, BooleanType, VagueType, IntType)
     * ```
     *
     * - ????????? [Method] ????????? [emptyParam] ??????????????????
     *
     * - ????????? [Method] ?????????????????????????????????????????? [paramCount] ????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param paramType ?????????????????? - ???????????? [Class]???[String]???[VariousClass]
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun param(vararg paramType: Any): IndexTypeCondition {
        if (paramType.isEmpty()) error("paramTypes is empty, please use emptyParam() instead")
        rulesData.paramTypes = arrayListOf<Class<*>>().apply { paramType.forEach { add(it.compat() ?: UndefinedType) } }.toTypedArray()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ????????????
     *
     * ?????????????????? ???
     *
     * ```kotlin
     * param { it[1] == StringClass || it[2].name == "java.lang.String" }
     * ```
     *
     * - ????????? [Method] ????????? [emptyParam] ??????????????????
     *
     * - ????????? [Method] ?????????????????????????????????????????? [paramCount] ????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param conditions ???????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun param(conditions: ObjectsConditions): IndexTypeCondition {
        rulesData.paramTypesConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ??????????????????????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * ?????? [Method] ??????
     *
     * - ??????????????????????????????????????????????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param value ??????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(value: String): IndexTypeCondition {
        rulesData.name = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ????????????
     *
     * - ??????????????????????????????????????????????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param conditions ???????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(conditions: NameConditions): IndexTypeCondition {
        rulesData.nameConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ????????????
     *
     * ?????????????????? [param] ????????????????????????????????????????????????????????????
     *
     * ?????????????????????????????????????????? [param]
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param num ??????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun paramCount(num: Int): IndexTypeCondition {
        rulesData.paramCount = num
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ??????????????????
     *
     * ?????????????????? [param] ??????????????????????????????????????????????????????????????????
     *
     * ?????????????????? ???
     *
     * ```kotlin
     * paramCount(1..5)
     * ```
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param numRange ????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun paramCount(numRange: IntRange): IndexTypeCondition {
        rulesData.paramCountRange = numRange
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ??????????????????
     *
     * ?????????????????? [param] ??????????????????????????????????????????????????????????????????
     *
     * ?????????????????? ???
     *
     * ```kotlin
     * paramCount { it >= 5 || it.isZero() }
     * ```
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param conditions ???????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun paramCount(conditions: CountConditions): IndexTypeCondition {
        rulesData.paramCountConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ?????????
     *
     * - ?????????????????????
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param value ??????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun returnType(value: Any): IndexTypeCondition {
        rulesData.returnType = value.compat()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ?????? [Method] ???????????????
     *
     * - ?????????????????????
     *
     * ?????????????????? ???
     *
     * ```kotlin
     * returnType { it == StringClass || it.name == "java.lang.String" }
     * ```
     *
     * - ??????????????? [BaseFinder.IndexTypeCondition] ????????? [order] ????????????????????????
     * @param conditions ???????????????
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun returnType(conditions: ObjectConditions): IndexTypeCondition {
        rulesData.returnTypeConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * ????????? [classSet] ?????????????????????????????? [Method]
     *
     * - ???????????? [classSet] ?????????????????????????????? - API ????????????????????????????????? [Any] ?????????????????????
     * @param isOnlySuperClass ?????????????????? [classSet] ?????????????????? - ???????????? [Any] ???????????????
     */
    fun superClass(isOnlySuperClass: Boolean = false) {
        rulesData.isFindInSuper = true
        if (isOnlySuperClass && classSet?.hasExtends == true) usedClassSet = classSet.superclass
    }

    /**
     * ?????? [Method] ????????? [Method]
     * @return [HashSet]<[Method]>
     * @throws NoSuchMethodError ??????????????? [Method]
     */
    private val result get() = ReflectionTool.findMethods(usedClassSet, rulesData)

    /**
     * ????????????
     * @param methods ??????????????? [Method] ??????
     */
    private fun setInstance(methods: HashSet<Method>) {
        memberInstances.clear()
        methods.takeIf { it.isNotEmpty() }?.forEach { memberInstances.add(it) }
    }

    /** ?????? [Method] ?????? */
    private fun internalBuild() {
        if (classSet == null) error(CLASSSET_IS_NULL)
        runBlocking {
            setInstance(result)
        }.result { ms ->
            memberInstances.takeIf { it.isNotEmpty() }?.forEach { onDebuggingMsg(msg = "Find Method [$it] takes ${ms}ms") }
        }
    }

    @YukiPrivateApi
    override fun build() = runCatching {
        internalBuild()
        Result()
    }.getOrElse {
        onFailureMsg(throwable = it)
        Result(isNoSuch = true, it)
    }

    @YukiPrivateApi
    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    /**
     * [Method] ??????????????????
     *
     * ???????????????????????????????????????
     */
    inner class RemedyPlan @PublishedApi internal constructor() {

        /** ???????????????????????? */
        @PublishedApi
        internal val remedyPlans = HashSet<Pair<MethodFinder, Result>>()

        /**
         * ??????????????????????????? [Method]
         *
         * ??????????????????????????? [Method] - ??????????????????
         *
         * ????????????????????? - ????????????????????????????????????
         * @param initiate ?????????
         * @return [Result] ??????
         */
        inline fun method(initiate: MethodConditions) = Result().apply { remedyPlans.add(Pair(MethodFinder(classSet).apply(initiate), this)) }

        /** ??????????????? */
        @PublishedApi
        internal fun build() {
            if (classSet == null) return
            if (remedyPlans.isNotEmpty()) run {
                var isFindSuccess = false
                var lastError: Throwable? = null
                remedyPlans.forEachIndexed { p, it ->
                    runCatching {
                        runBlocking {
                            setInstance(it.first.result)
                        }.result { ms ->
                            memberInstances.takeIf { it.isNotEmpty() }?.forEach { onDebuggingMsg(msg = "Find Method [$it] takes ${ms}ms") }
                        }
                        isFindSuccess = true
                        it.second.onFindCallback?.invoke(memberInstances.methods())
                        remedyPlansCallback?.invoke()
                        memberInstances.takeIf { it.isNotEmpty() }
                            ?.forEach { onDebuggingMsg(msg = "Method [$it] trying ${p + 1} times success by RemedyPlan") }
                        return@run
                    }.onFailure {
                        lastError = it
                        onFailureMsg(msg = "Trying ${p + 1} times by RemedyPlan --> $it", isAlwaysPrint = true)
                    }
                }
                if (isFindSuccess.not()) {
                    onFailureMsg(
                        msg = "Trying ${remedyPlans.size} times and all failure by RemedyPlan",
                        throwable = lastError,
                        isAlwaysPrint = true
                    )
                    remedyPlans.clear()
                }
            } else yLoggerW(msg = "RemedyPlan is empty, forgot it?")
        }

        /**
         * [RemedyPlan] ???????????????
         *
         * ???????????????????????????????????????
         */
        inner class Result @PublishedApi internal constructor() {

            /** ???????????????????????? */
            internal var onFindCallback: (HashSet<Method>.() -> Unit)? = null

            /**
             * ??????????????????
             * @param initiate ??????
             */
            fun onFind(initiate: HashSet<Method>.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Method] ?????????????????????
     * @param isNoSuch ?????????????????? [Method] - ?????????
     * @param throwable ????????????
     */
    inner class Result internal constructor(
        @PublishedApi internal val isNoSuch: Boolean = false,
        @PublishedApi internal val throwable: Throwable? = null
    ) : BaseResult {

        /**
         * ?????????????????????????????????
         * @param initiate ?????????
         * @return [Result] ?????????????????????
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * ?????? [Method] ???????????????
         *
         * - ???????????? [Method] ???????????????????????????
         *
         * - ?????? [memberInstances] ???????????????????????????????????????????????????
         *
         * - ?????????????????? [remedys] ????????? [wait] ??????????????????
         * @param instance ????????????
         * @return [Instance]
         */
        fun get(instance: Any? = null) = Instance(instance, give())

        /**
         * ?????? [Method] ?????????????????????
         *
         * - ??????????????????????????????????????? [Method] ????????????
         *
         * - ?????? [memberInstances] ???????????????????????????????????????????????????
         *
         * - ?????????????????? [remedys] ????????? [waitAll] ??????????????????
         * @param instance ????????????
         * @return [ArrayList]<[Instance]>
         */
        fun all(instance: Any? = null) =
            arrayListOf<Instance>().apply { giveAll().takeIf { it.isNotEmpty() }?.forEach { add(Instance(instance, it)) } }

        /**
         * ?????? [Method] ??????
         *
         * - ???????????? [Method] ???????????????????????????
         *
         * - ?????????????????????????????????????????????????????? null
         * @return [Method] or null
         */
        fun give() = giveAll().takeIf { it.isNotEmpty() }?.first()

        /**
         * ?????? [Method] ????????????
         *
         * - ??????????????????????????????????????? [Method] ??????
         *
         * - ???????????????????????????????????????????????????????????? [HashSet]
         * @return [HashSet]<[Method]>
         */
        fun giveAll() = memberInstances.takeIf { it.isNotEmpty() }?.methods() ?: HashSet()

        /**
         * ?????? [Method] ???????????????
         *
         * - ???????????? [Method] ???????????????????????????
         *
         * - ?????????????????? [remedys] ???????????????????????????????????????
         *
         * - ????????????????????? [remedys] ???????????????????????????
         * @param instance ????????????
         * @param initiate ?????? [Instance]
         */
        fun wait(instance: Any? = null, initiate: Instance.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(get(instance))
            else remedyPlansCallback = { initiate(get(instance)) }
        }

        /**
         * ?????? [Method] ?????????????????????
         *
         * - ??????????????????????????????????????? [Method] ????????????
         *
         * - ?????????????????? [remedys] ???????????????????????????????????????
         *
         * - ????????????????????? [remedys] ???????????????????????????
         * @param instance ????????????
         * @param initiate ?????? [ArrayList]<[Instance]>
         */
        fun waitAll(instance: Any? = null, initiate: ArrayList<Instance>.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(all(instance))
            else remedyPlansCallback = { initiate(all(instance)) }
        }

        /**
         * ?????? [Method] ???????????????
         *
         * ?????????????????? [Method] ????????????????????????????????????
         *
         * ???????????? [RemedyPlan] ??????????????? - ????????????????????? [onNoSuchMethod] ???????????????????????? [Method]
         *
         * ??????????????????????????? - ???????????????????????????????????????????????????????????????
         * @param initiate ?????????
         * @return [Result] ?????????????????????
         */
        inline fun remedys(initiate: RemedyPlan.() -> Unit): Result {
            isUsingRemedyPlan = true
            if (isNoSuch) RemedyPlan().apply(initiate).build()
            return this
        }

        /**
         * ??????????????? [Method] ???
         *
         * - ???????????????????????????????????? - ???????????? [RemedyPlan] ???????????????
         * @param result ????????????
         * @return [Result] ?????????????????????
         */
        inline fun onNoSuchMethod(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * ?????????????????????????????????????????????
         *
         * - ????????????????????????????????? - ????????????????????? [onNoSuchMethod] ??????
         * @return [Result] ?????????????????????
         */
        fun ignored(): Result {
            isShutErrorPrinting = true
            return this
        }

        /**
         * [Method] ???????????????
         *
         * - ???????????? [get]???[wait]???[all]???[waitAll] ??????????????? [Instance]
         * @param instance ?????? [Method] ????????????????????????
         * @param method ?????? [Method] ????????????
         */
        inner class Instance internal constructor(private val instance: Any?, private val method: Method?) {

            /**
             * ?????? [Method]
             * @param args ????????????
             * @return [Any] or null
             */
            private fun baseCall(vararg args: Any?) = method?.invoke(instance, *args)

            /**
             * ?????? [Method] - ????????????????????????
             * @param args ????????????
             * @return [Any] or null
             */
            fun call(vararg args: Any?) = baseCall(*args)

            /**
             * ?????? [Method] - ?????? [T] ???????????????
             * @param args ????????????
             * @return [T] or null
             */
            fun <T> invoke(vararg args: Any?) = baseCall(*args) as? T?

            /**
             * ?????? [Method] - ?????? [Byte] ???????????????
             *
             * - ????????????????????????????????? - ????????????????????? null
             * @param args ????????????
             * @return [Byte] or null
             */
            fun byte(vararg args: Any?) = invoke<Byte?>(*args)

            /**
             * ?????? [Method] - ?????? [Int] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Int] ??????????????? 0
             */
            fun int(vararg args: Any?) = invoke(*args) ?: 0

            /**
             * ?????? [Method] - ?????? [Long] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Long] ??????????????? 0L
             */
            fun long(vararg args: Any?) = invoke(*args) ?: 0L

            /**
             * ?????? [Method] - ?????? [Short] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Short] ??????????????? 0
             */
            fun short(vararg args: Any?) = invoke<Short?>(*args) ?: 0

            /**
             * ?????? [Method] - ?????? [Double] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Double] ??????????????? 0.0
             */
            fun double(vararg args: Any?) = invoke(*args) ?: 0.0

            /**
             * ?????? [Method] - ?????? [Float] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Float] ??????????????? 0f
             */
            fun float(vararg args: Any?) = invoke(*args) ?: 0f

            /**
             * ?????? [Method] - ?????? [String] ???????????????
             * @param args ????????????
             * @return [String] ??????????????? ""
             */
            fun string(vararg args: Any?) = invoke(*args) ?: ""

            /**
             * ?????? [Method] - ?????? [Char] ???????????????
             * @param args ????????????
             * @return [Char] ??????????????? ' '
             */
            fun char(vararg args: Any?) = invoke(*args) ?: ' '

            /**
             * ?????? [Method] - ?????? [Boolean] ???????????????
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @param args ????????????
             * @return [Boolean] ??????????????? false
             */
            fun boolean(vararg args: Any?) = invoke(*args) ?: false

            /**
             * ?????? [Method] - ?????? [Array] ??????????????? - ???????????? [T]
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @return [Array] ????????????????????????
             */
            inline fun <reified T> array(vararg args: Any?) = invoke(*args) ?: arrayOf<T>()

            /**
             * ?????? [Method] - ?????? [List] ??????????????? - ???????????? [T]
             *
             * - ?????????????????? [Method] ???????????? - ??????????????????????????????
             * @return [List] ????????????????????????
             */
            inline fun <reified T> list(vararg args: Any?) = invoke(*args) ?: listOf<T>()

            override fun toString() = "[${method?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}]"
        }
    }
}